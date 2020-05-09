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

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.data.StaffUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.List;

public class FindCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() != 1 )
        {
            user.sendLangMessage( "general-commands.find.usage" );
            return;
        }

        if ( isHidden( args.get( 0 ) ) )
        {
            user.sendLangMessage( "offline" );
            return;
        }

        final ServerInfo server = BUCore.getApi().getPlayerUtils().findPlayer( args.get( 0 ) );

        if ( server == null )
        {
            user.sendLangMessage( "offline" );
            return;
        }
        // This is because some people apparently can't handle that the name shown isn't in correct capitalisation ...
        // This doesn't support redis, not planning to do it either (as this is quite useless in comparison to the load it takes)
        final String playerName = server.getPlayers()
                .stream()
                .filter( p -> p.getName().equalsIgnoreCase( args.get( 0 ) ) )
                .map( CommandSender::getName )
                .findFirst()
                .orElse( args.get( 0 ) );

        user.sendLangMessage( "general-commands.find.message", "{user}", playerName, "{server}", server.getName() );
    }

    private boolean isHidden( final String name )
    {
        for ( StaffUser user : BungeeUtilisals.getInstance().getStaffMembers() )
        {
            if ( user.isHidden() )
            {
                return true;
            }
        }
        return false;
    }
}
