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

package com.dbsoftwares.bungeeutilisalsx.spigot.listeners;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.user.UserChatEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.user.UserCommandEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.Optional;

public class UserChatListener implements Listener
{

    @EventHandler
    public void onChat( final AsyncPlayerChatEvent event )
    {
        final Optional<User> optional = BuX.getApi().getUser( event.getPlayer().getName() );

        if ( !optional.isPresent() )
        {
            return;
        }
        final User user = optional.get();

        final UserChatEvent chatEvent = new UserChatEvent( user, event.getMessage() );
        BuX.getApi().getEventLoader().launchEvent( chatEvent );

        if ( chatEvent.isCancelled() )
        {
            event.setCancelled( true );
            return;
        }

        event.setMessage( chatEvent.getMessage() );

    }

    @EventHandler
    public void onCommand( final PlayerCommandPreprocessEvent event )
    {
        final Optional<User> optional = BuX.getApi().getUser( event.getPlayer().getName() );

        if ( !optional.isPresent() )
        {
            return;
        }
        final User user = optional.get();

        final UserCommandEvent commandEvent = new UserCommandEvent( user, event.getMessage() );
        BuX.getApi().getEventLoader().launchEvent( commandEvent );

        if ( commandEvent.isCancelled() )
        {
            event.setCancelled( true );
            return;
        }
        event.setMessage( commandEvent.getCommand() );
    }
}