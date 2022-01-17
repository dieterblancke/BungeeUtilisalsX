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

package be.dieterblancke.bungeeutilisalsx.common.executors;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserCommandEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserPrivateMessageEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.CommandSpyJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpyEventExecutor implements EventExecutor
{

    @Event
    public void onPrivateMessage( final UserPrivateMessageEvent event )
    {
        final String permission = ConfigFiles.GENERALCOMMANDS.getConfig().getString( "socialspy.permission" );
        final List<User> users = Stream.concat( BuX.getApi().getUsers().stream(), Stream.of( BuX.getApi().getConsoleUser() ) )
                .filter( user -> user.isSocialSpy() && user.hasPermission( permission ) )
                .filter( user -> !user.getName().equals( event.getSender() )
                        && !user.getName().equals( event.getReceiver() ) )
                .collect( Collectors.toList() );

        if ( users.isEmpty() )
        {
            return;
        }

        for ( User user : users )
        {
            user.sendLangMessage(
                    "general-commands.socialspy.message",
                    "{sender}", event.getSender(),
                    "{receiver}", event.getReceiver(),
                    "{message}", event.getMessage()
            );
        }
    }

    @Event
    public void onCommand( final UserCommandEvent event )
    {
        final String commandName = event.getActualCommand().replaceFirst( "/", "" );

        for ( String command : ConfigFiles.GENERALCOMMANDS.getConfig().getStringList( "commandspy.ignored-commands" ) )
        {
            if ( command.trim().equalsIgnoreCase( commandName.trim() ) )
            {
                return;
            }
        }

        BuX.getInstance().getJobManager().executeJob( new CommandSpyJob(
                event.getUser().getUuid(),
                event.getUser().getName(),
                event.getUser().getServerName(),
                event.getCommand()
        ) );
    }
}
