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

package com.dbsoftwares.bungeeutilisalsx.bungee.listeners;

import com.dbsoftwares.bungeeutilisalsx.bungee.user.BungeeUser;
import com.dbsoftwares.bungeeutilisalsx.bungee.utils.BungeeServer;
import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.user.UserServerConnectEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.user.UserServerConnectedEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Optional;

public class UserConnectionListener implements Listener
{

    @EventHandler
    public void onConnect( final PostLoginEvent event )
    {
        final BungeeUser user = new BungeeUser();

        user.load( event.getPlayer().getUniqueId() );
    }

    // Executing on LOWEST priority to get it to execute early on in the quit procedure
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDisconnect( final PlayerDisconnectEvent event )
    {
        final Optional<User> optional = BuX.getApi().getUser( event.getPlayer().getName() );

        if ( !optional.isPresent() )
        {
            return;
        }
        final User user = optional.get();
        user.unload();
    }

    @EventHandler
    public void onConnect( final ServerConnectEvent event )
    {
        final Optional<User> optional = BuX.getApi().getUser( event.getPlayer().getName() );

        if ( !optional.isPresent() )
        {
            return;
        }
        final UserServerConnectEvent userServerConnectEvent = new UserServerConnectEvent(
                optional.get(),
                BuX.getInstance().proxyOperations().getServerInfo( event.getTarget().getName() )
        );
        BuX.getApi().getEventLoader().launchEvent( userServerConnectEvent );
        if ( userServerConnectEvent.isCancelled() )
        {
            event.setCancelled( true );
        }
        event.setTarget( ( (BungeeServer) userServerConnectEvent.getTarget() ).getServerInfo() );
    }

    @EventHandler
    public void onConnect( final ServerConnectedEvent event )
    {
        final Optional<User> optional = BuX.getApi().getUser( event.getPlayer().getName() );

        if ( !optional.isPresent() )
        {
            return;
        }
        final UserServerConnectedEvent userServerConnectedEvent = new UserServerConnectedEvent(
                optional.get(),
                BuX.getInstance().proxyOperations().getServerInfo( event.getServer().getInfo().getName() )
        );
        BuX.getApi().getEventLoader().launchEvent( userServerConnectedEvent );
    }
}
