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

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Level;

@Deprecated
public abstract class BUCommand extends Command implements CommandCall, TabExecutor
{

    protected List<SubCommand> subCommands = Lists.newArrayList();
    private String permission;

    public BUCommand( String name )
    {
        this( name, Lists.newArrayList(), null );
    }

    public BUCommand( String name, String... aliases )
    {
        this( name, Lists.newArrayList( aliases ), null );
    }

    public BUCommand( String name, List<String> aliases )
    {
        this( name, aliases, null );
    }

    public BUCommand( String name, List<String> aliases, String permission )
    {
        super( name, "", aliases.toArray( new String[]{} ) );
        this.permission = permission;

        ProxyServer.getInstance().getPluginManager().registerCommand( BUCore.getApi().getPlugin(), this );
    }

    @Override
    public void execute( CommandSender sender, String[] args )
    {
        BUAPI api = BUCore.getApi();
        IConfiguration configuration = api.getLanguageManager().getLanguageConfiguration( api.getPlugin().getDescription().getName(), sender );

        if ( permission != null
                && !permission.isEmpty()
                && !sender.hasPermission( permission )
                && !sender.hasPermission( "bungeeutilisals.commands.*" )
                && !sender.hasPermission( "bungeeutilisals.*" )
                && !sender.hasPermission( "*" ) )
        {
            BUCore.sendMessage( sender, configuration.getString( "no-permission" ).replace( "%permission%", permission ) );
            return;
        }

        if ( sender instanceof ProxiedPlayer )
        {
            Optional<User> optional = api.getUser( sender.getName() );

            if ( optional.isPresent() )
            {
                User user = optional.get();

                BUCore.getApi().getSimpleExecutor().asyncExecute( () ->
                {
                    try
                    {
                        onExecute( user, args );
                    }
                    catch ( Exception e )
                    {
                        BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
                    }
                } );
                return;
            }
        }
        try
        {
            BUCore.getApi().getSimpleExecutor().asyncExecute( () ->
            {
                try
                {
                    onExecute( BUCore.getApi().getConsole(), args );
                }
                catch ( Exception e )
                {
                    BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
                }
            } );
        }
        catch ( Exception e )
        {
            BUCore.logException( e );
        }
    }

    @Override
    public Iterable<String> onTabComplete( CommandSender sender, String[] args )
    {
        if ( !( sender instanceof ProxiedPlayer ) )
        {
            return ImmutableList.of();
        }

        final Optional<User> user = BUCore.getApi().getUser( sender.getName() );
        if ( user.isPresent() )
        {
            final List<String> tabCompletion = onTabComplete( user.get(), args );

            if ( tabCompletion == null )
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
            }
            return tabCompletion;
        }
        else
        {
            return ImmutableList.of();
        }
    }

    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        onExecute( user, args.toArray( new String[0] ) );
    }

    public abstract List<String> onTabComplete( User user, String[] args );

    public abstract void onExecute( User user, String[] args );

    public void unload()
    {
        ProxyServer.getInstance().getPluginManager().unregisterCommand( this );
    }

    protected SubCommand findSubCommand( String name )
    {
        return subCommands.stream().filter(
                subCommand -> subCommand.getName().equalsIgnoreCase( name ) || subCommand.getAliases().contains( name )
        ).findFirst().orElse( null );
    }

    protected List<String> getSubcommandCompletions( User user, String[] args )
    {
        List<String> completions = Lists.newArrayList();

        if ( args.length == 0 )
        {
            subCommands.forEach( subCommand -> completions.add( subCommand.getName() ) );
        }
        else if ( args.length == 1 )
        {
            subCommands.stream().filter( subCommand -> subCommand.getName().toLowerCase().startsWith( args[0].toLowerCase() ) )
                    .forEach( subCommand -> completions.add( subCommand.getName() ) );
        }
        else
        {
            SubCommand command = findSubCommand( args[0] );

            if ( command != null )
            {
                return command.getCompletions( user, args );
            }
        }
        return completions;
    }

    @Override
    public boolean equals( final Object other )
    {
        if ( this == other )
        {
            return true;
        }
        if ( other == null || getClass() != other.getClass() )
        {
            return false;
        }
        if ( !super.equals( other ) )
        {
            return false;
        }
        final BUCommand command = (BUCommand) other;
        return Objects.equals( subCommands, command.subCommands ) &&
                Objects.equals( permission, command.permission );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( super.hashCode(), subCommands, permission );
    }
}