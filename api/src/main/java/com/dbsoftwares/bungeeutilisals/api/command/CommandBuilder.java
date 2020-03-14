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

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class CommandBuilder
{

    private static final TabCall DEFAULT_TAB_CALL = ( user, args ) ->
    {
        if ( args.length == 0 )
        {
            return BUCore.getApi().getPlayerUtils().getPlayers();
        }
        else
        {
            final String lastWord = args[args.length - 1];
            final List<String> list = Lists.newArrayList();

            for ( String p : BUCore.getApi().getPlayerUtils().getPlayers() )
            {
                if ( p.toLowerCase().startsWith( lastWord.toLowerCase() ) )
                {
                    list.add( p );
                }
            }

            return list;
        }
    };
    private boolean enabled;
    private String name;
    private String[] aliases = new String[0];
    private String permission;
    private int cooldown;
    private CommandCall call;
    private TabCall tab;
    private List<String> parameters;

    public static CommandBuilder builder()
    {
        return new CommandBuilder();
    }

    public CommandBuilder enabled( final boolean enabled )
    {
        this.enabled = enabled;
        return this;
    }

    public CommandBuilder name( final String name )
    {
        this.name = name;
        return this;
    }

    public CommandBuilder aliases( final String... aliases )
    {
        this.aliases = aliases;
        return this;
    }

    public CommandBuilder aliases( final List<String> aliases )
    {
        return this.aliases( aliases.toArray( new String[0] ) );
    }

    public CommandBuilder cooldown( final int cooldown )
    {
        this.cooldown = cooldown;
        return this;
    }

    public CommandBuilder permission( final String permission )
    {
        this.permission = permission;
        return this;
    }

    public CommandBuilder parameters( final String... parameters )
    {
        return parameters( Arrays.asList( parameters ) );
    }

    public CommandBuilder parameters( final List<String> parameters )
    {
        this.parameters = parameters;
        return this;
    }

    public CommandBuilder fromSection( final IConfiguration config, final String section )
    {
        return this.fromSection( config.getSection( section ) );
    }

    public CommandBuilder fromSection( final ISection section )
    {
        this.enabled = section.exists( "enabled" ) ? section.getBoolean( "enabled" ) : true;
        if ( section.exists( "name" ) )
        {
            this.name = section.getString( "name" );
        }
        if ( !section.exists( "aliases" ) || section.getString( "aliases" ).isEmpty() )
        {
            this.aliases = new String[0];
        }
        else
        {
            this.aliases = section.getString( "aliases" ).split( ", " );
        }
        this.permission = section.getString( "permission" );
        this.cooldown = section.exists( "cooldown" ) ? section.getInteger( "cooldown" ) : -1;

        return this;
    }

    public CommandBuilder executable( final CommandCall call )
    {
        this.call = call;

        if ( call instanceof TabCall )
        {
            tab( (TabCall) call );
        }

        return this;
    }

    public CommandBuilder tab( final TabCall tab )
    {
        this.tab = tab;
        return this;
    }

    public Command build( final Consumer<Command> consumer )
    {
        final Command command = build();

        consumer.accept( command );

        return command;
    }

    public Command build()
    {
        if ( !enabled )
        {
            return null;
        }
        if ( call == null )
        {
            throw new NullPointerException( "Command call cannot be null!" );
        }
        if ( tab == null )
        {
            tab = DEFAULT_TAB_CALL;
        }

        return new Command( name, aliases, permission, parameters, cooldown, call, tab );
    }
}
