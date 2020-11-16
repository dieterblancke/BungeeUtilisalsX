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

package com.dbsoftwares.bungeeutilisalsx.common.commands.general;

import com.dbsoftwares.bungeeutilisalsx.common.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.api.command.TabCall;
import com.dbsoftwares.bungeeutilisalsx.common.api.command.TabCompleter;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import net.md_5.bungee.api.ChatColor;

import java.lang.management.ManagementFactory;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class GLagCommandCall implements CommandCall, TabCall
{

    @Override
    public List<String> onTabComplete( User user, String[] args )
    {
        return TabCompleter.empty();
    }

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final long uptime = ManagementFactory.getRuntimeMXBean().getStartTime();
        final SimpleDateFormat df2 = new SimpleDateFormat( "kk:mm dd-MM-yyyy" );
        final String date = df2.format( new Date( uptime ) );

        user.sendLangMessage( "general-commands.glag",
                "{maxmemory}", ( Runtime.getRuntime().maxMemory() / 1024 / 1024 ) + " MB",
                "{freememory}", ( Runtime.getRuntime().freeMemory() / 1024 / 1024 ) + " MB",
                "{totalmemory}", ( Runtime.getRuntime().totalMemory() / 1024 / 1024 ) + " MB",
                "{onlinesince}", date
        );
    }

    private ChatColor getColor( double tps )
    {
        if ( tps >= 18.0 )
        {
            return ChatColor.GREEN;
        }
        else if ( tps >= 14.0 )
        {
            return ChatColor.YELLOW;
        }
        else if ( tps >= 8.0 )
        {
            return ChatColor.RED;
        }
        else
        {
            return ChatColor.DARK_RED;
        }
    }
}
