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

package com.dbsoftwares.bungeeutilisalsx.common.bridge;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.IBuXApi;
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.IBridgeManager;
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.event.BridgeResponseEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.event.IEventHandler;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.common.bridge.handlers.BungeeBridgeResponseHandler;
import com.dbsoftwares.bungeeutilisalsx.common.bridge.redis.RedisBridge;
import com.dbsoftwares.configuration.api.ISection;

import java.util.Set;

public class BridgeManager implements IBridgeManager
{

    private RedisBridge redisBridge;
    private Set<IEventHandler<BridgeResponseEvent>> eventHandlers;

    public BridgeManager()
    {
        // empty constructor
    }

    public void setup( final IBuXApi api )
    {
        final ISection config = ConfigFiles.CONFIG.getConfig().getSection( "bridging" );

        if ( !config.getBoolean( "enabled" ) )
        {
            return;
        }
        boolean redisBridgeSetup = false;
        if ( config.getBoolean( "bungee.enabled" ) )
        {
            redisBridge = new RedisBridge();
            redisBridgeSetup = redisBridge.setup( api );

        }
        if ( config.getBoolean( "spigot.enabled" ) && redisBridge == null )
        {
            redisBridge = new RedisBridge();
            redisBridgeSetup = redisBridge.setup( api );
        }

        if ( redisBridge != null && !redisBridgeSetup )
        {
            BuX.getLogger().warning(
                    "Could not set up the Redis Bridge! Please check your configuration."
            );
            BuX.getLogger().warning(
                    "Bungee bridging will not work without the 'Redis Bridge' functioning!!"
            );
        }

        eventHandlers = api.getEventLoader().register( BridgeResponseEvent.class, new BungeeBridgeResponseHandler() );

        BuX.getLogger().info(
                "Successfully set up the bridging system!"
        );
    }

    public boolean useBridging()
    {
        return redisBridge != null;
    }

    public RedisBridge getBridge()
    {
        return redisBridge;
    }

    public void shutdown()
    {
        if ( redisBridge != null )
        {
            redisBridge.shutdownBridge();
        }
        if ( eventHandlers != null )
        {
            eventHandlers.forEach( IEventHandler::unregister );
            eventHandlers.clear();
        }
    }
}
