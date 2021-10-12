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

package be.dieterblancke.bungeeutilisalsx.bungee.listeners;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserChatEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserCommandEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserTabCompleteEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Optional;

public class UserChatListener implements Listener
{

    @EventHandler
    public void onChat( ChatEvent event )
    {
        final Optional<User> optional = BuX.getApi().getUser( ( (CommandSender) event.getSender() ).getName() );

        if ( !optional.isPresent() )
        {
            return;
        }
        final User user = optional.get();

        if ( event.isCommand() )
        {
            final UserCommandEvent commandEvent = new UserCommandEvent( user, event.getMessage() );
            BuX.getApi().getEventLoader().launchEvent( commandEvent );

            if ( commandEvent.isCancelled() )
            {
                event.setCancelled( true );
                return;
            }
            event.setMessage( commandEvent.getCommand() );
        }
        else
        {
            final UserChatEvent chatEvent = new UserChatEvent( user, event.getMessage() );
            BuX.getApi().getEventLoader().launchEvent( chatEvent );

            if ( chatEvent.isCancelled() )
            {
                event.setCancelled( true );
                return;
            }

            event.setMessage( chatEvent.getMessage() );
        }
    }

    @EventHandler
    public void onTabComplete( final TabCompleteEvent event )
    {
        final Optional<User> optional = BuX.getApi().getUser( ( (CommandSender) event.getSender() ).getName() );

        if ( !optional.isPresent() )
        {
            return;
        }
        final User user = optional.get();
        final UserTabCompleteEvent userTabCompleteEvent = new UserTabCompleteEvent(
                user,
                event.getCursor(),
                event.getSuggestions()
        );

        BuX.getApi().getEventLoader().launchEvent( userTabCompleteEvent );

        if ( userTabCompleteEvent.isCancelled() )
        {
            event.setCancelled( true );
        }
        event.getSuggestions().clear();
        event.getSuggestions().addAll( userTabCompleteEvent.getSuggestions() );
    }
}