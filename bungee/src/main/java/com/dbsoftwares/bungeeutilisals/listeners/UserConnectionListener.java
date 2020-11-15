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

package com.dbsoftwares.bungeeutilisals.listeners;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.user.UserPreLoadEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.user.BUser;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Optional;

public class UserConnectionListener implements Listener
{

    @EventHandler
    public void onConnect( PostLoginEvent event )
    {
        final ProxiedPlayer player = event.getPlayer();

        final UserPreLoadEvent userPreLoadEvent = new UserPreLoadEvent( player, player.getAddress().getAddress() );
        BungeeUtilisals.getApi().getEventLoader().launchEvent( userPreLoadEvent );

        if ( userPreLoadEvent.isCancelled() )
        {
            return;
        }

        final BUser user = new BUser();
        user.load( player );
    }

    @EventHandler
    public void onDisconnect( PlayerDisconnectEvent event )
    {
        final ProxiedPlayer player = event.getPlayer();
        final Optional<User> optional = BungeeUtilisals.getApi().getUser( player );

        if ( !optional.isPresent() )
        {
            return;
        }
        final User user = optional.get();
        user.unload();
    }
}
