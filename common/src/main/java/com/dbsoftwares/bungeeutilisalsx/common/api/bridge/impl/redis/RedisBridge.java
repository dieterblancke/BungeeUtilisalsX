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

package com.dbsoftwares.bungeeutilisalsx.common.api.bridge.impl.redis;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.Bridge;
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.BridgeType;
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.message.BridgedMessage;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.event.IEventHandler;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

@Slf4j
public class RedisBridge extends Bridge
{

    @Getter
    private RedisClient redisClient;
    private StatefulRedisPubSubConnection<String, String> pubSubConnection;
    private StatefulRedisPubSubConnection<String, String> pubSubSubscriberConnection;

    @Override
    public boolean setup()
    {
        super.setup();
        try
        {
            // Getting credentials from configuration
            final ISection section = ConfigFiles.CONFIG.getConfig().getSection( "bridging.redis" );

            final String host = section.getString( "host", "localhost" );
            final int port = section.getInteger( "port", 6379 );
            final String password = section.getString( "password" );

            final RedisURI uri = RedisURI.builder()
                    .withHost( host )
                    .withPort( port )
                    .withPassword( password )
                    .build();
            this.redisClient = RedisClient.create( uri );
            this.pubSubConnection = this.redisClient.connectPubSub();
            this.pubSubSubscriberConnection = this.redisClient.connectPubSub();

            this.pubSubSubscriberConnection.sync().subscribe( "BUX_DEFAULT_CHANNEL" );
            this.pubSubSubscriberConnection.addListener( new RedisDefaultPubSubListener( this ) );

            log.info( "Successfully connected to Redis server." );
            setup = true;
        }
        catch ( Exception e )
        {
            setup = false;
            e.printStackTrace();
        }
        return setup;
    }

    @Override
    public void sendMessage( final BridgedMessage message )
    {
        if ( ConfigFiles.CONFIG.isDebug() )
        {
            log.info( "Sending message on BUX_DEFAULT_CHANNEL (redis):" );
            log.info( message.toString() );
        }
        if ( message.getIgnoredTargets() == null ) message.setIgnoredTargets( Lists.newArrayList() );
        message.getIgnoredTargets().add( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) );

        pubSubConnection.sync().publish( "BUX_DEFAULT_CHANNEL", BuX.getGson().toJson( message ) );
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
                ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ),
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
                ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ),
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
        redisClient.shutdown();
        consumersMap.clear();

        if ( eventHandlers != null )
        {
            eventHandlers.forEach( IEventHandler::unregister );
            eventHandlers.clear();
        }
    }
}
