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
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.event.BridgeResponseEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.message.BridgedMessage;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.redis.RedisMessageEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

public class RedisDefaultPubSubListener implements EventExecutor
{

    private final RedisBridge bridge;

    public RedisDefaultPubSubListener( final RedisBridge bridge )
    {
        this.bridge = bridge;
    }

    @Event
    public void message( final RedisMessageEvent event )
    {
        if ( !event.getChannel().equals( "BUX_DEFAULT_CHANNEL" ) )
        {
            return;
        }
        final BridgedMessage message = BuX.getGson().fromJson( event.getMessage(), BridgedMessage.class );

        if ( ConfigFiles.CONFIG.isDebug() )
        {
            BuX.getLogger().info( "Received message on BUX_DEFAULT_CHANNEL (redis):" );
            BuX.getLogger().info( message.toString() );
        }

        if ( !bridge.canAccept( message ) )
        {
            if ( ConfigFiles.CONFIG.isDebug() )
            {
                BuX.getLogger().info( "Message with uuid " + message.getIdentifier() + " could not be accepted!" );
                BuX.getLogger().info( message.toString() );
            }
            return;
        }
        if ( ConfigFiles.CONFIG.isDebug() )
        {
            BuX.getLogger().info( "Message with uuid " + message.getIdentifier() + " was accepted, executing event ..." );
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
