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

package com.dbsoftwares.bungeeutilisals.api.bridge;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.bridge.impl.PluginMessageBridge;
import com.dbsoftwares.bungeeutilisals.api.bridge.impl.redis.RedisBridge;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.configuration.api.ISection;

public class BridgeManager
{
    private RedisBridge redisBridge;
    private PluginMessageBridge pluginMessageBridge;

    public BridgeManager()
    {
    }

    public void setup()
    {
        final ISection config = FileLocation.CONFIG.getConfiguration().getSection( "bridging" );

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
                            "Spigot bridging will fall back onto Plugin Message Briding."
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
        redisBridge.shutdownBridge();
        pluginMessageBridge.shutdownBridge();
    }
}
