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
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.event.BridgeResponseEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.message.BridgedMessage;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import io.lettuce.core.pubsub.RedisPubSubAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisDefaultPubSubListener extends RedisPubSubAdapter<String, String>
{

    private final RedisBridge bridge;

    public RedisDefaultPubSubListener( final RedisBridge bridge )
    {
        this.bridge = bridge;
    }

    @Override
    public void message( final String channel, final String data )
    {
        if ( !channel.equals( "BUX_DEFAULT_CHANNEL" ) )
        {
            return;
        }
        final BridgedMessage message = BuX.getGson().fromJson( data, BridgedMessage.class );

        if ( ConfigFiles.CONFIG.isDebug() )
        {
            log.info( "Received message on BUX_DEFAULT_CHANNEL (redis):" );
            log.info( message.toString() );
        }

        if ( !bridge.canAccept( message ) )
        {
            if ( ConfigFiles.CONFIG.isDebug() )
            {
                log.info( "Message with uuid " + message.getIdentifier() + " could not be accepted!" );
                log.info( message.toString() );
            }
            return;
        }
        if ( ConfigFiles.CONFIG.isDebug() )
        {
            log.info( "Message with uuid " + message.getIdentifier() + " was accepted, executing event ..." );
        }

        final BridgeResponseEvent responseEvent = new BridgeResponseEvent(
                message.getType(),
                message.getIdentifier(),
                message.getFrom(),
                message.getAction(),
                message.getMessage()
        );
        BuX.getApi().getEventLoader().launchEventAsync( responseEvent );
    }
}
