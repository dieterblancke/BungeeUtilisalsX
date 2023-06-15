package be.dieterblancke.bungeeutilisalsx.common.api.command;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TimeUnit;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
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
            if ( argument.startsWith( "-" ) && this.parameters.stream().anyMatch( argument::startsWith ) )
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
            user.sendLangMessage(
                    "no-permission",
                    MessagePlaceholders.create()
                            .append( "permission", permission )
            );
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
                            MessagePlaceholders.create()
                                    .append( "{time}", user.getCooldowns().getLeftTime( "COMMAND_COOLDOWNS_" + name ) / 1000 )
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
        if ( user == null || user.isConsole() )
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
        BuX.getInstance().serverOperations().unregisterCommand( this );
    }

    public Command register()
    {
        BuX.getInstance().serverOperations().registerCommand( this );
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