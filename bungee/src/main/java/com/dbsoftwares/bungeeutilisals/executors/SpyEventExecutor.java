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

package com.dbsoftwares.bungeeutilisals.executors;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserCommandEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserPrivateMessageEvent;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;

import java.util.List;
import java.util.stream.Collectors;

public class SpyEventExecutor implements EventExecutor
{

    @Event
    public void onPrivateMessage( final UserPrivateMessageEvent event )
    {
        final String permission = FileLocation.GENERALCOMMANDS.getConfiguration().getString( "socialspy.permission" );
        final List<User> users = BUCore.getApi().getUsers()
                .stream()
                .filter( user -> user.isSocialSpy() && user.hasPermission( permission ) )
                .collect( Collectors.toList() );

        if ( users.isEmpty() )
        {
            return;
        }

        for ( User user : users )
        {
            user.sendLangMessage(
                    "general-commands.socialspy.message",
                    "{sender}", event.getSender().getName(),
                    "{receiver}", event.getReceiver().getName(),
                    "{message}", event.getMessage()
            );
        }
    }

    @Event
    public void onCommand( final UserCommandEvent event )
    {
        final String permission = FileLocation.GENERALCOMMANDS.getConfiguration().getString( "commandspy.permission" );
        final List<User> users = BUCore.getApi().getUsers()
                .stream()
                .filter( user -> user.isCommandSpy() && user.hasPermission( permission ) )
                .collect( Collectors.toList() );

        if ( users.isEmpty() )
        {
            return;
        }

        final String commandName = event.getActualCommand().replaceFirst( "/", "" );
        for ( String command : FileLocation.GENERALCOMMANDS.getConfiguration().getStringList( "commandspy.ignored-commands" ) )
        {
            if ( command.trim().equalsIgnoreCase( commandName.trim() ) )
            {
                return;
            }
        }

        for ( User user : users )
        {
            user.sendLangMessage(
                    "general-commands.commandspy.message",
                    "{user}", event.getUser().getName(),
                    "{server}", event.getUser().getServerName(),
                    "{command}", event.getCommand()
            );
        }
    }
}
