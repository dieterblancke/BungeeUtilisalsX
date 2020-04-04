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
import com.dbsoftwares.bungeeutilisals.api.bridge.Bridge;
import com.dbsoftwares.bungeeutilisals.api.bridge.IBridgeManager;
import com.dbsoftwares.bungeeutilisals.api.bridge.event.BridgeResponseEvent;
import com.dbsoftwares.bungeeutilisals.api.bridge.impl.PluginMessageBridge;
import com.dbsoftwares.bungeeutilisals.api.bridge.impl.redis.RedisBridge;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventHandler;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.handlers.BungeeBridgeResponseHandler;
import com.dbsoftwares.configuration.api.ISection;

import java.util.Set;

public class BridgeManager implements IBridgeManager
{
    private RedisBridge redisBridge;
    private PluginMessageBridge pluginMessageBridge;
    private Set<EventHandler<BridgeResponseEvent>> eventHandlers;

    public BridgeManager()
    {
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
            redisBridge = new RedisBridge();
            redisBridgeSetup = redisBridge.setup();
        }
        if ( config.getBoolean( "spigot.enabled" ) )
        {
            if ( redisBridge == null && config.getStringList( "spigot.methods" ).contains( "redis" ) )
            {
                redisBridge = new RedisBridge();
                redisBridgeSetup = redisBridge.setup();
            }

            if ( config.getStringList( "spigot.methods" ).contains( "pluginmessaging" ) )
            {
                pluginMessageBridge = new PluginMessageBridge();
            }
        }

        if ( redisBridge != null && !redisBridgeSetup )
        {
            BUCore.getLogger().warning(
                    "Could not set up the Redis Bridge! Please check your configuration."
            );
            BUCore.getLogger().warning(
                    "Bungee bridging will not work without the 'Redis Bridge' functioning!!"
            );

            if ( config.getBoolean( "spigot.enabled" ) )
            {
                if ( pluginMessageBridge != null )
                {
                    BUCore.getLogger().warning(
                            "Spigot bridging will fall back onto Plugin Message Bridging."
                    );
                }
                else
                {
                    BUCore.getLogger().warning(
                            "Spigot bridging will not work without the 'Redis Bridge' functioning! "
                                    + "This because the Plugin Message Fallback Bridge is disabled."
                    );
                }
            }
        }

        eventHandlers = BUCore.getApi().getEventLoader().register( BridgeResponseEvent.class, new BungeeBridgeResponseHandler() );

        BUCore.getLogger().info(
                "Successfully set up the bridging system!"
        );
    }

    public boolean useBungeeBridge()
    {
        return redisBridge != null;
    }

    public boolean useSpigotBridge()
    {
        return redisBridge != null || pluginMessageBridge != null;
    }

    public Bridge getBungeeBridge()
    {
        return redisBridge;
    }

    public Bridge getSpigotBridge()
    {
        return redisBridge != null ? redisBridge : pluginMessageBridge;
    }

    public void shutdown()
    {
        if ( redisBridge != null )
        {
            redisBridge.shutdownBridge();
        }
        if ( pluginMessageBridge != null )
        {
            pluginMessageBridge.shutdownBridge();
        }
        if ( eventHandlers != null )
        {
            eventHandlers.forEach( EventHandler::unregister );
            eventHandlers.clear();
        }
    }
}
