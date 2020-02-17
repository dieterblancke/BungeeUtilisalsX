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

package com.dbsoftwares.bungeeutilisals.api.command;

import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class ParentCommand implements TabCall
{

    private final List<Command> subCommands = Lists.newArrayList();
    private final Consumer<User> helpConsumer;

    public ParentCommand( final String helpPath )
    {
        this( user -> user.sendLangMessage( helpPath ) );
    }

    public ParentCommand( final Consumer<User> helpConsumer )
    {
        this.helpConsumer = helpConsumer;
    }

    protected void registerSubCommand( final Command cmd )
    {
        if ( cmd == null )
        {
            return;
        }
        subCommands.add( cmd );
    }

    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        // handle sub commands ...
        if ( !subCommands.isEmpty() )
        {
            for ( Command subCommand : subCommands )
            {
                if ( subCommand.check( args ) )
                {
                    subCommand.execute( user, args.subList( 1, args.size() ), parameters );
                    return;
                }
            }
        }
        helpConsumer.accept( user );
    }

    @Override
    public List<String> onTabComplete( User user, String[] args )
    {
        final List<String> subCommandNames = subCommands.stream().map( Command::getName ).collect( Collectors.toList() );

        if ( args.length == 0 )
        {
            return subCommandNames;
        }
        else if ( args.length == 1 )
        {
            return Utils.copyPartialMatches( args[0], subCommandNames, Lists.newArrayList() );
        }
        else
        {
            return null;
        }
    }
}
