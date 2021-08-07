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
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.ClearChatJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;

import java.util.List;

public class ClearChatCommandCall implements CommandCall, TabCall
{

    public static void clearChat( final String server, final String by )
    {
        if ( server.equalsIgnoreCase( "ALL" ) )
        {
            BuX.getApi().getUsers().forEach( u -> clearChat( u, by ) );
        }
        else
        {
            final IProxyServer info = BuX.getInstance().proxyOperations().getServerInfo( server );

            if ( info != null )
            {
                BuX.getApi().getUsers()
                        .stream()
                        .filter( u -> u.getServerName().equalsIgnoreCase( info.getName() ) )
                        .forEach( u -> clearChat( u, by ) );
            }
        }
    }

    private static void clearChat( final User user, final String by )
    {
        for ( int i = 0; i < 250; i++ )
        {
            user.sendMessage( Utils.format( "&e  " ) );
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

        BuX.getInstance().getJobManager().executeJob( new ClearChatJob( server, user.getName() ) );
    }
}
