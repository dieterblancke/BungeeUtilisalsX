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

package be.dieterblancke.bungeeutilisalsx.common.bridge.redis;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.IBuXApi;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.Bridge;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.BridgeType;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.message.BridgedMessage;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.redis.RedisManager;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.IEventHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.redis.RedisMessageEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class RedisBridge extends Bridge
{

    @Getter
    private RedisManager redisManager;

    @Override
    public boolean setup( final IBuXApi api )
    {
        super.setup( api );
        try
        {
            this.redisManager = RedisManagerFactory.create();
            this.redisManager.subscribeToChannels( "BUX_DEFAULT_CHANNEL" );
            api.getEventLoader().register( RedisMessageEvent.class, new RedisDefaultPubSubListener( this ) );

            BuX.getLogger().info( "Successfully connected to Redis server." );
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
            BuX.getLogger().info( "Sending message on BUX_DEFAULT_CHANNEL (redis):" );
            BuX.getLogger().info( message.toString() );
        }
        if ( message.getIgnoredTargets() == null )
        {
            message.setIgnoredTargets( Lists.newArrayList() );
        }
        message.getIgnoredTargets().add( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) );

        this.redisManager.publishToChannel( "BUX_DEFAULT_CHANNEL", BuX.getGson().toJson( message ) );
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
        this.redisManager.closeConnections();
        this.consumersMap.clear();

        if ( this.eventHandlers != null )
        {
            this.eventHandlers.forEach( IEventHandler::unregister );
            this.eventHandlers.clear();
        }
    }
}
