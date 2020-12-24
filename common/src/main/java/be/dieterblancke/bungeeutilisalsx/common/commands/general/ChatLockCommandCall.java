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

package be.dieterblancke.bungeeutilisalsx.common.commands.general;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCompleter;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Stream;

public class ChatLockCommandCall implements CommandCall, TabCall
{

    public static final List<String> lockedChatServers = Lists.newArrayList();

    public static void lockChat( final String server, final String by )
    {
        Stream<User> users = server.equals( "ALL" )
                ? BuX.getApi().getUsers().stream()
                : BuX.getApi().getUsers().stream().filter( u -> u.getServerName().equalsIgnoreCase( server ) );

        if ( lockedChatServers.contains( server ) )
        {
            lockedChatServers.remove( server );

            users.forEach( u -> u.sendLangMessage( "general-commands.chatlock.unlocked", "{user}", by ) );
        }
        else
        {
            lockedChatServers.add( server );

            users.forEach( u -> u.sendLangMessage( "general-commands.chatlock.locked", "{user}", by ) );
        }
    }

    @Override
    public List<String> onTabComplete( final User user, final String[] args )
    {
        return TabCompleter.buildTabCompletion( ConfigFiles.SERVERGROUPS.getServers().keySet(), args );
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() == 0 )
        {
            user.sendLangMessage( "general-commands.chatlock.usage" );
            return;
        }
        final String server = args.get( 0 ).toLowerCase().contains( "g" ) ? "ALL" : user.getServerName();

        lockChat( server, user.getName() );
    }
}
