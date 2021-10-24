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

package be.dieterblancke.bungeeutilisalsx.common.api.command;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TimeUnit;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.ProtocolizeManager.SoundData;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

@Data
public class Command
{

    private final String name;
    private final String[] aliases;
    private final String permission;
    private final List<String> parameters;
    private final int cooldown;
    private final CommandCall command;
    private final TabCall tab;
    private final List<ServerGroup> disabledServers;
    private final boolean listenerBased;
    private final SoundData soundData;

    Command( final String name,
             final String[] aliases,
             final String permission,
             List<String> parameters,
             final int cooldown,
             final CommandCall command,
             final TabCall tab,
             final List<ServerGroup> disabledServers,
             final boolean listenerBased,
             final SoundData soundData )
    {
        if ( parameters == null )
        {
            parameters = Lists.newArrayList();
        }

        this.name = name;
        this.aliases = aliases;
        this.permission = permission;
        this.parameters = parameters;
        this.cooldown = cooldown;
        this.command = command;
        this.tab = tab;
        this.disabledServers = disabledServers == null ? new ArrayList<>() : disabledServers;
        this.listenerBased = listenerBased;
        this.soundData = soundData;
    }

    public void execute( final User user, final String[] argList )
    {
        final List<String> arguments = Lists.newArrayList();
        final List<String> params = Lists.newArrayList();

        for ( String argument : argList )
        {
            if ( argument.startsWith( "-" ) && this.parameters.contains( argument ) )
            {
                if ( user.hasPermission( this.permission + ".parameters." + argument.substring( 1 ) )
                        || user.hasPermission( this.permission + ".parameters.*" ) )
                {
                    params.add( argument );
                }
            }
            else
            {
                arguments.add( argument );
            }
        }

        execute( user, arguments, params );
    }

    public void execute( final User user, final List<String> arguments, final List<String> parameters )
    {
        if ( this.isDisabledInServer( user.getServerName() ) )
        {
            user.sendLangMessage( "command-disabled-in-this-server" );
            return;
        }
        if ( permission != null
                && !permission.isEmpty()
                && !user.hasPermission( permission )
                && !user.hasPermission( "bungeeutilisals.commands.*" )
                && !user.hasPermission( "bungeeutilisals.*" )
                && !user.hasPermission( "*" ) )
        {
            user.sendLangMessage( "no-permission", "%permission%", permission );
            return;
        }

        BuX.getInstance().getScheduler().runAsync( () ->
        {
            try
            {
                if ( cooldown > 0 && !user.getCooldowns().canUse( "COMMAND_COOLDOWNS_" + name ) )
                {
                    user.sendLangMessage(
                            "general-commands.cooldown",
                            "{time}",
                            user.getCooldowns().getLeftTime( "COMMAND_COOLDOWNS_" + name ) / 1000
                    );
                    return;
                }

                command.onExecute( user, arguments, parameters );

                if ( cooldown > 0 )
                {
                    user.getCooldowns().updateTime( "COMMAND_COOLDOWNS_" + name, TimeUnit.SECONDS, cooldown );
                }

                if ( BuX.getInstance().isProtocolizeEnabled() )
                {
                    BuX.getInstance().getProtocolizeManager().sendSound( user, soundData );
                }
            }
            catch ( Exception e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }
        } );
    }

    public boolean isDisabledInServer( final String serverName )
    {
        if ( serverName == null )
        {
            return false;
        }
        for ( ServerGroup server : this.disabledServers )
        {
            if ( server.isInGroup( serverName ) )
            {
                return true;
            }
        }
        return false;
    }

    public List<String> onTabComplete( final User user, final String[] args )
    {
        if ( user.isConsole() || user == null )
        {
            return TabCompleter.empty();
        }

        final List<String> tabCompletion = tab.onTabComplete( user, args );

        if ( tabCompletion == null )
        {
            if ( args.length == 0 )
            {
                return BuX.getApi().getPlayerUtils().getPlayers();
            }
            else
            {
                final String lastWord = args[args.length - 1];
                final List<String> list = Lists.newArrayList();

                for ( String p : BuX.getApi().getPlayerUtils().getPlayers() )
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

    public void unload()
    {
        BuX.getInstance().proxyOperations().unregisterCommand( this );
    }

    public Command register()
    {
        BuX.getInstance().proxyOperations().registerCommand( this );
        return this;
    }

    boolean check( final List<String> args )
    {
        if ( args.isEmpty() )
        {
            return false;
        }
        if ( name.equalsIgnoreCase( args.get( 0 ) ) )
        {
            return true;
        }
        for ( String alias : aliases )
        {
            if ( alias.equalsIgnoreCase( args.get( 0 ) ) )
            {
                return true;
            }
        }
        return false;
    }
}