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
import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.user.UserChatEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.user.UserCommandEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Optional;

public class UserChatListener implements Listener
{

    @EventHandler
    public void onChat( ChatEvent event )
    {
        BUAPI api = BungeeUtilisals.getApi();
        Optional<User> optional = api.getUser( ( (CommandSender) event.getSender() ).getName() );

        if ( !optional.isPresent() )
        {
            return;
        }
        User user = optional.get();

        if ( event.isCommand() )
        {
            UserCommandEvent commandEvent = new UserCommandEvent( user, event.getMessage() );
            api.getEventLoader().launchEvent( commandEvent );

            if ( commandEvent.isCancelled() )
            {
                event.setCancelled( true );
                return;
            }
            event.setMessage( commandEvent.getCommand() );
        }
        else
        {
            UserChatEvent chatEvent = new UserChatEvent( user, event.getMessage() );
            api.getEventLoader().launchEvent( chatEvent );

            if ( chatEvent.isCancelled() )
            {
                event.setCancelled( true );
                return;
            }

            event.setMessage( chatEvent.getMessage() );
        }
    }
}