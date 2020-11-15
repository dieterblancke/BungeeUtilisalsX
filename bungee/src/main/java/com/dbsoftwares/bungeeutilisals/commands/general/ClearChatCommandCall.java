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
import com.dbsoftwares.bungeeutilisals.api.command.TabCall;
import com.dbsoftwares.bungeeutilisals.api.command.TabCompleter;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.utils.Utils;
import com.dbsoftwares.bungeeutilisalsx.common.utils.config.ConfigFiles;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;

public class ClearChatCommandCall implements CommandCall, TabCall
{

    public static void clearChat( final String server, final String by )
    {
        if ( server.equalsIgnoreCase( "ALL" ) )
        {
            BUCore.getApi().getUsers().forEach( u -> clearChat( u, by ) );
        }
        else
        {
            final ServerInfo info = ProxyServer.getInstance().getServerInfo( server );

            if ( info != null )
            {
                BUCore.getApi().getUsers().stream().filter( u -> u.getServerName().equalsIgnoreCase( info.getName() ) )
                        .forEach( u -> clearChat( u, by ) );
            }
        }
    }

    private static void clearChat( final User user, final String by )
    {
        for ( int i = 0; i < 250; i++ )
        {
            user.sendMessage( Utils.format( "&e" ) );
        }

        user.sendLangMessage( "general-commands.clearchat.cleared", "{user}", by );
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
            user.sendLangMessage( "general-commands.clearchat.usage" );
            return;
        }
        final String server = args.get( 0 ).toLowerCase().contains( "g" ) ? "ALL" : user.getServerName();
        clearChat( server, user.getName() );
    }
}
