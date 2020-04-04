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
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;
import java.util.Optional;

public class ServerCommandCall implements CommandCall
{

    @Override
    public void onExecute( User user, List<String> args, List<String> parameters )
    {
        if ( args.size() < 1 )
        {
            user.sendLangMessage( "general-commands.server.usage", "{server}", user.getServerName() );
            return;
        }

        if ( args.size() == 2 )
        {
            if ( !user.hasPermission( ConfigFiles.GENERALCOMMANDS.getConfig().getString( "server.permission-other" ) ) )
            {
                user.sendLangMessage( "no-permission" );
                return;
            }

            final Optional<User> optionalTarget = BUCore.getApi().getUser( args.get( 0 ) );
            final ServerInfo server = ProxyServer.getInstance().getServerInfo( args.get( 1 ) );

            if ( !optionalTarget.isPresent() )
            {
                user.sendLangMessage( "offline" );
                return;
            }
            final User target = optionalTarget.get();

            if ( server == null )
            {
                user.sendLangMessage( "general-commands.server.notfound", "{server}", args.get( 1 ) );
                return;
            }

            user.sendLangMessage(
                    "general-commands.server.sent-other",
                    "{user}", target.getName(),
                    "{server}", server.getName()
            );
            sendToServer( target, server );
            return;
        }

        final ServerInfo server = ProxyServer.getInstance().getServerInfo( args.get( 0 ) );

        if ( server == null )
        {
            user.sendLangMessage( "general-commands.server.notfound", "{server}", args.get( 0 ) );
            return;
        }

        sendToServer( user, server );
    }

    private void sendToServer( final User user, final ServerInfo server )
    {
        if ( user.getServerName().equalsIgnoreCase( server.getName() ) )
        {
            user.sendLangMessage( "general-commands.server.alreadyconnected", "{server}", server.getName() );
            return;
        }

        user.getParent().connect( server );
        user.sendLangMessage( "general-commands.server.connecting", "{server}", server.getName() );
    }
}
