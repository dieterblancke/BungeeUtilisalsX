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

package be.dieterblancke.bungeeutilisalsx.common.bridge;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.IBuXApi;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.IBridgeManager;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.event.BridgeResponseEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.IEventHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.bridge.handlers.BungeeBridgeResponseHandler;
import be.dieterblancke.bungeeutilisalsx.common.bridge.redis.RedisBridge;
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

        this.redisBridge = new RedisBridge();
        final boolean redisBridgeSetup = redisBridge.setup( api );

        if ( redisBridge != null && !redisBridgeSetup )
        {
            BuX.getLogger().warning(
                    "Could not set up the Redis Bridge! Please check your configuration." +
                            "\nThe bridging system will not work without a proper redis configuration."
            );
        }

        eventHandlers = api.getEventLoader().register( BridgeResponseEvent.class, new BungeeBridgeResponseHandler() );

        BuX.getLogger().info(
                "Successfully set up the bridging system!"
        );
    }

    public boolean useBridging()
    {
        final ISection config = ConfigFiles.CONFIG.getConfig().getSection( "bridging" );

        return config.getBoolean( "enabled" ) && redisBridge != null && redisBridge.isSetup();
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
