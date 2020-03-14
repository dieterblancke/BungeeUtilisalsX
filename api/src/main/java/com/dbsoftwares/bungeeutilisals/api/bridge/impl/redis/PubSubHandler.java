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
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Level;

public class PubSubHandler extends JedisPubSub implements Runnable
{

    private final RedisBridge bridge;
    private final Map<String, Set<Consumer<String>>> consumers = Maps.newHashMap();

    public PubSubHandler( final RedisBridge bridge )
    {
        this.bridge = bridge;
    }

    public void registerConsumer( final String channel, final Consumer<String> consumer )
    {
        final Set<Consumer<String>> consumerList;
        if ( consumers.containsKey( channel ) )
        {
            consumerList = consumers.get( channel );
        }
        else
        {
            addChannel( channel );
            consumerList = Sets.newHashSet();
        }
        consumerList.add( consumer );
        this.consumers.put( channel, consumerList );
    }

    @Override
    public void onMessage( final String channel, final String message )
    {
        if ( message.trim().isEmpty() )
        {
            return;
        }

        if ( consumers.containsKey( channel ) )
        {
            consumers.get( channel ).forEach( consumer -> consumer.accept( message ) );
        }
    }

    @Override
    public void run()
    {
        try ( Jedis jedis = bridge.getPool().getResource() )
        {
            try
            {
                jedis.subscribe(
                        this,
                        "redismessenger-" + FileLocation.CONFIG.getConfiguration().getString( "bridging.name" )
                );
            }
            catch ( Exception e )
            {
                BUCore.getLogger().log( Level.SEVERE, "Could not create default pubsub channels.", e );
                unsubscribe();
            }
        }
    }

    private void addChannel( String... channel )
    {
        subscribe( channel );
    }

    private void removeChannel( String... channel )
    {
        unsubscribe( channel );
    }

    public void poison()
    {
        unsubscribe();
    }
}