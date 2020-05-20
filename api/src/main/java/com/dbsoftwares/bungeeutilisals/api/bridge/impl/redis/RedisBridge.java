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
import com.dbsoftwares.bungeeutilisals.api.bridge.impl.redis.codecs.RedisUserCodec;
import com.dbsoftwares.bungeeutilisals.api.bridge.message.BridgedMessage;
import com.dbsoftwares.bungeeutilisals.api.bridge.util.RedisUser;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventHandler;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.Getter;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;

public class RedisBridge extends Bridge
{

    private final static String REDIS_USER_KEY = "REDIS_USER_KEY";
    private final Cache<Object, Map<String, RedisUser>> redisUserCache = CacheBuilder.newBuilder()
            .expireAfterWrite( 5, TimeUnit.SECONDS )
            .build();

    @Getter
    private RedisClient redisClient;
    private StatefulRedisPubSubConnection<String, String> pubSubConnection;
    @Getter
    private StatefulRedisConnection<String, RedisUser> userConnection;

    @Override
    public boolean setup()
    {
        try
        {
            eventHandlers = BUCore.getApi().getEventLoader().register( BridgeResponseEvent.class, this );

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
            this.userConnection = this.redisClient.connect( new RedisUserCodec() );


            this.pubSubConnection.addListener( new RedisDefaultPubSubListener( this ) );

            final RedisUserExecutor executor = new RedisUserExecutor( this );
            BUCore.getApi().getEventLoader().register( UserLoadEvent.class, executor );
            BUCore.getApi().getEventLoader().register( UserUnloadEvent.class, executor );

            BUCore.getLogger().log( Level.INFO, "Successfully connected to Redis server." );
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
        if ( ConfigFiles.CONFIG.isDebug() )
        {
            BUCore.getLogger().info( "Sending message on BUX_DEFAULT_CHANNEL (redis):" );
            BUCore.getLogger().info( message.toString() );
        }
        pubSubConnection.sync().publish( "BUX_DEFAULT_CHANNEL", BUCore.getGson().toJson( message ) );
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
            eventHandlers.forEach( EventHandler::unregister );
            eventHandlers.clear();
        }
    }

    public Map<String, RedisUser> getAllRedisUsers()
    {
        try
        {
            return redisUserCache.get( REDIS_USER_KEY, () ->
            {
                final RedisAsyncCommands<String, RedisUser> commands = userConnection.async();
                final RedisFuture<Map<String, RedisUser>> future = commands.hgetall( "bungeeutilisalsx:users" );

                try
                {
                    return future.get();
                }
                catch ( InterruptedException | ExecutionException e )
                {
                    e.printStackTrace();
                    return Maps.newHashMap();
                }
            } );
        }
        catch ( ExecutionException e )
        {
            e.printStackTrace();
            return Maps.newHashMap();
        }
    }
}
