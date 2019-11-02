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

package com.dbsoftwares.bungeeutilisals.commands.plugin;

import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.commands.plugin.sub.*;
import com.google.common.collect.Lists;

import java.util.List;

public class PluginCommand extends BUCommand
{

    public PluginCommand()
    {
        super( "bungeeutilisals", Lists.newArrayList( "bu", "butilisals", "butili" ), "bungeeutilisals.admin" );

        subCommands.add( new VersionSubCommand() );
        subCommands.add( new ReloadSubCommand() );
        subCommands.add( new DumpSubCommand() );
        subCommands.add( new ImportSubCommand() );
        subCommands.add( new ConvertSubCommand() );
    }

    @Override
    public List<String> onTabComplete( User user, String[] args )
    {
        return getSubcommandCompletions( user, args );
    }

    @Override
    public void onExecute( User user, String[] args )
    {
        if ( args.length == 0 )
        {
            sendHelpList( user );
            return;
        }
        for ( SubCommand subCommand : subCommands )
        {
            if ( subCommand.execute( user, args ) )
            {
                return;
            }
        }
        sendHelpList( user );
    }

    private void sendHelpList( User user )
    {
        user.sendMessage( "&aAdmin Commands help:" );
        subCommands.forEach( cmd -> user.sendMessage( "&b- &e" + cmd.getUsage() ) );
    }
}
