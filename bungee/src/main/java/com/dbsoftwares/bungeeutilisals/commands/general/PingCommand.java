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

package com.dbsoftwares.bungeeutilisals.commands.general;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.user.ConsoleUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class PingCommand extends BUCommand
{

    public PingCommand()
    {
        super(
                "ping",
                Arrays.asList( ConfigFiles.GENERALCOMMANDS.getConfig().getString( "ping.aliases" ).split( ", " ) ),
                ConfigFiles.GENERALCOMMANDS.getConfig().getString( "ping.permission" )
        );
    }

    @Override
    public List<String> onTabComplete( User user, String[] args )
    {
        return null;
    }

    @Override
    public void onExecute( User user, String[] args )
    {
        if ( user instanceof ConsoleUser )
        {
            return;
        }
        if ( args.length == 0 )
        {
            user.sendLangMessage( "general-commands.ping.message" );
        }
        else
        {
            final String permission = ConfigFiles.GENERALCOMMANDS.getConfig().getString( "ping.permission-other" );
            if ( permission != null
                    && !permission.isEmpty()
                    && !user.getParent().hasPermission( permission )
                    && !user.getParent().hasPermission( "bungeeutilisals.commands.*" )
                    && !user.getParent().hasPermission( "bungeeutilisals.*" )
                    && !user.getParent().hasPermission( "*" ) )
            {
                user.sendLangMessage( "no-permission", "%permission%", permission );
                return;
            }

            final Optional<User> optionalUser = BUCore.getApi().getUser( args[0] );

            if ( !optionalUser.isPresent() )
            {
                user.sendLangMessage( "offline" );
                return;
            }
            final User target = optionalUser.get();

            user.sendLangMessage(
                    "general-commands.ping.other",
                    "{target}", target.getName(),
                    "{targetPing}", target.getParent().getPing()
            );
        }
    }
}
