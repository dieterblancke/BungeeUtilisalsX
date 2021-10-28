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

import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.TabCompleter;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import net.md_5.bungee.api.ChatColor;

import java.lang.management.ManagementFactory;
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

        user.sendLangMessage( "general-commands.glag",
                "{maxmemory}", ( Runtime.getRuntime().maxMemory() / 1024 / 1024 ) + " MB",
                "{freememory}", ( Runtime.getRuntime().freeMemory() / 1024 / 1024 ) + " MB",
                "{totalmemory}", ( Runtime.getRuntime().totalMemory() / 1024 / 1024 ) + " MB",
                "{onlinesince}", Utils.formatDate( new Date( uptime ), user.getLanguageConfig().getConfig() )
        );
    }

    @Override
    public String getDescription()
    {
        return "Gives you some basic information about the current proxy (online time and memory usage).";
    }

    @Override
    public String getUsage()
    {
        return "/glag";
    }
}
