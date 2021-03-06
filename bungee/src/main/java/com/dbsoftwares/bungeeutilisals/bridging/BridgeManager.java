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

package com.dbsoftwares.bungeeutilisals.bridging;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.bridge.IBridgeManager;
import com.dbsoftwares.bungeeutilisals.api.bridge.event.BridgeResponseEvent;
import com.dbsoftwares.bungeeutilisals.api.bridge.impl.redis.RedisBridge;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventHandler;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.handlers.BungeeBridgeResponseHandler;
import com.dbsoftwares.configuration.api.ISection;
import net.md_5.bungee.api.ProxyServer;

import java.util.Set;

public class BridgeManager implements IBridgeManager
{

    private RedisBridge redisBridge;
    private Set<EventHandler<BridgeResponseEvent>> eventHandlers;

    public BridgeManager()
    {
        // empty constructor
    }

    public void setup()
    {
        final ISection config = ConfigFiles.CONFIG.getConfig().getSection( "bridging" );

        if ( !config.getBoolean( "enabled" ) )
        {
            return;
        }
        boolean redisBridgeSetup = false;
        if ( config.getBoolean( "bungee.enabled" ) )
        {
            if ( ProxyServer.getInstance().getPluginManager().getPlugin( "RedisBungee" ) == null )
            {
                BUCore.getLogger().warning(
                        "Could not set up the Bungee Bridge, RedisBungee needs to be present for this!"
                );
            }
            else
            {
                redisBridge = new RedisBridge();
                redisBridgeSetup = redisBridge.setup();
            }
        }
        if ( config.getBoolean( "spigot.enabled" ) && redisBridge == null )
        {
            redisBridge = new RedisBridge();
            redisBridgeSetup = redisBridge.setup();
        }

        if ( redisBridge != null && !redisBridgeSetup )
        {
            BUCore.getLogger().warning(
                    "Could not set up the Redis Bridge! Please check your configuration."
            );
            BUCore.getLogger().warning(
                    "Bungee bridging will not work without the 'Redis Bridge' functioning!!"
            );
        }

        eventHandlers = BUCore.getApi().getEventLoader().register( BridgeResponseEvent.class, new BungeeBridgeResponseHandler() );

        BUCore.getLogger().info(
                "Successfully set up the bridging system!"
        );
    }

    public boolean useBridging()
    {
        return redisBridge != null
                && ProxyServer.getInstance().getPluginManager().getPlugin( "RedisBungee" ) != null;
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
            eventHandlers.forEach( EventHandler::unregister );
            eventHandlers.clear();
        }
    }
}
