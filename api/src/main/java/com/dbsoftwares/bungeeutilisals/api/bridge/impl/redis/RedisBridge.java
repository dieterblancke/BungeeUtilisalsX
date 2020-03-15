/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.api.bridge.impl.redis;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.bridge.Bridge;
import com.dbsoftwares.bungeeutilisals.api.bridge.BridgeType;
import com.dbsoftwares.bungeeutilisals.api.bridge.event.BridgeResponseEvent;
import com.dbsoftwares.bungeeutilisals.api.bridge.message.BridgedMessage;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventHandler;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.configuration.api.ISection;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;
import java.util.logging.Level;

public class RedisBridge extends Bridge
{

    @Getter
    private JedisPool pool;
    private PubSubHandler pubSubHandler;

    @Override
    public boolean setup()
    {
        try
        {
            eventHandlers = BUCore.getApi().getEventLoader().register( BridgeResponseEvent.class, this );

            // Getting credentials from configuration
            final ISection section = FileLocation.CONFIG.getConfiguration().getSection( "bridging.redis" );

            final String host = section.getString( "host", "localhost" );
            final int port = section.getInteger( "port", 6379 );
            final String password = section.getString( "password" ).isEmpty() ? null : section.getString( "password" );

            // Setting up jedis pool
            final FutureTask<JedisPool> task = new FutureTask<>( () ->
            {
                final JedisPoolConfig config = new JedisPoolConfig();
                config.setMaxTotal( section.getInteger( "max-connections", 5 ) );

                return new JedisPool( config, host, port, 0, password );
            } );
            ProxyServer.getInstance().getScheduler().runAsync( BUCore.getApi().getPlugin(), task );
            try
            {
                pool = task.get();
            }
            catch ( InterruptedException | ExecutionException e )
            {
                setup = false;
                throw new RuntimeException( "Unable to create Jedis pool", e );
            }

            try ( Jedis jedis = pool.getResource() )
            {
                jedis.ping();

                BUCore.getLogger().log( Level.INFO, "Successfully connected to Redis server." );
            }
            catch ( JedisConnectionException e )
            {
                pool.destroy();
                pool = null;
                setup = false;
                throw e;
            }

            // Setting up PubSub channel handler
            pubSubHandler = new PubSubHandler( this );
            ProxyServer.getInstance().getScheduler().runAsync( BUCore.getApi().getPlugin(), pubSubHandler );

            // Setting up default channel.
            pubSubHandler.registerConsumer( "BUX_DEFAULT_CHANNEL", this::onGeneralPubSubMessage );
            return setup = true;
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            return setup = false;
        }
    }

    private void sendMessage( final BridgedMessage message )
    {
        try ( Jedis jedis = pool.getResource() )
        {
            if ( FileLocation.CONFIG.getConfiguration().getBoolean( "debug" ) )
            {
                BUCore.getLogger().info( "Sending message on BUX_DEFAULT_CHANNEL (redis):" );
                BUCore.getLogger().info( message.toString() );
            }

            jedis.publish( "BUX_DEFAULT_CHANNEL", BUCore.getGson().toJson( message ) );
        }
        catch ( JedisConnectionException e )
        {
            BUCore.getLogger().log( Level.SEVERE, "Could not establish a connection.", e );
            throw new RuntimeException( "Unable to publish channel message", e );
        }
    }

    @Override
    public BridgedMessage sendMessage(
            final BridgeType type,
            final String action,
            final Object data
    )
    {
        return sendTargetedMessage( type, null, null, action, data );
    }

    @Override
    public <T> BridgedMessage sendMessage(
            final BridgeType type,
            final String action,
            final Object data,
            final Class<T> responseType,
            final Consumer<T> consumer
    )
    {
        return sendTargetedMessage( type, null, null, action, data, responseType, consumer );
    }

    @Override
    public BridgedMessage sendTargetedMessage(
            final BridgeType type,
            final List<String> targets,
            final List<String> ignoredTargets,
            final String action,
            final Object data )
    {
        final BridgedMessage message = new BridgedMessage(
                type,
                UUID.randomUUID(),
                FileLocation.CONFIG.getConfiguration().getString( "bridging.name" ),
                targets,
                ignoredTargets,
                action,
                data
        );

        this.sendMessage( message );
        return message;
    }

    @Override
    public <T> BridgedMessage sendTargetedMessage(
            final BridgeType type,
            final List<String> targets,
            final List<String> ignoredTargets,
            final String action,
            final Object data,
            final Class<T> responseType,
            final Consumer<T> consumer
    )
    {
        final BridgedMessage message = new BridgedMessage(
                type,
                UUID.randomUUID(),
                FileLocation.CONFIG.getConfiguration().getString( "bridging.name" ),
                targets,
                ignoredTargets,
                action,
                data
        );
        consumersMap.put( message.getIdentifier().toString(), new SimpleEntry<>( responseType, consumer ) );

        this.sendMessage( message );
        return message;
    }

    @Override
    public void shutdownBridge()
    {
        pubSubHandler.poison();
        pool.close();
        consumersMap.clear();

        if ( eventHandlers != null )
        {
            eventHandlers.forEach( EventHandler::unregister );
            eventHandlers.clear();
        }
    }

    private void onGeneralPubSubMessage( final String data )
    {
        final BridgedMessage message = BUCore.getGson().fromJson( data, BridgedMessage.class );

        if ( FileLocation.CONFIG.getConfiguration().getBoolean( "debug" ) )
        {
            BUCore.getLogger().info( "Received message on BUX_DEFAULT_CHANNEL (redis):" );
            BUCore.getLogger().info( message.toString() );
        }

        if ( !super.canAccept( message ) )
        {
            if ( FileLocation.CONFIG.getConfiguration().getBoolean( "debug" ) )
            {
                BUCore.getLogger().info( "Message with uuid " + message.getIdentifier() + " could not be accepted!" );
                BUCore.getLogger().info( message.toString() );
            }
            return;
        }
        if ( FileLocation.CONFIG.getConfiguration().getBoolean( "debug" ) )
        {
            BUCore.getLogger().info( "Message with uuid " + message.getIdentifier() + " was accepted, executing event ..." );
        }

        final BridgeResponseEvent responseEvent = new BridgeResponseEvent(
                message.getType(),
                message.getIdentifier(),
                message.getFrom(),
                message.getAction(),
                message.getMessage()
        );
        BUCore.getApi().getEventLoader().launchEventAsync( responseEvent );
    }
}
