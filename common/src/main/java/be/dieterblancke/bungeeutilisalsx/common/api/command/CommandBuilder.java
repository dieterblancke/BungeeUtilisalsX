package be.dieterblancke.bungeeutilisalsx.common.api.command;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.StaffUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.ProtocolizeManager.SoundData;
import be.dieterblancke.configuration.api.IConfiguration;
import be.dieterblancke.configuration.api.ISection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class CommandBuilder
{

    private static final TabCall DEFAULT_TAB_CALL = ( user, args ) -> TabCompleter.buildTabCompletion(
            StaffUtils.filterPlayerList( BuX.getApi().getPlayerUtils().getPlayers() ), args
    );
    private boolean enabled;
    private String name;
    private String[] aliases = new String[0];
    private String permission;
    private int cooldown;
    private CommandCall call;
    private TabCall tab;
    private List<String> parameters;
    private List<ServerGroup> disabledServers = new ArrayList<>();
    private boolean listenerBased;
    private SoundData soundData;

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

    public CommandBuilder disabledServers( final List<ServerGroup> disabledServers )
    {
        this.disabledServers = disabledServers;
        return this;
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

        if ( section.exists( "disabled-servers" ) )
        {
            this.disabledServers = section.getStringList( "disabled-servers" )
                    .stream()
                    .filter( server -> ConfigFiles.SERVERGROUPS.getServers().containsKey( server ) )
                    .map( server -> ConfigFiles.SERVERGROUPS.getServer( server ) )
                    .collect( Collectors.toList() );
        }

        if ( section.exists( "listener-based" ) && section.getBoolean( "listener-based" ) )
        {
            this.listenerBased = true;
        }

        if ( section.exists( "sound" ) )
        {
            this.soundData = SoundData.fromSection( section, "sound" );
        }

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

    public CommandBuilder listenerBased( final boolean listenerBased )
    {
        this.listenerBased = listenerBased;
        return this;
    }

    public CommandBuilder tab( final TabCall tab )
    {
        this.tab = tab;
        return this;
    }

    public CommandBuilder soundData( final SoundData soundData )
    {
        this.soundData = soundData;
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

        return new Command( name, aliases, permission, parameters, cooldown, call, tab, disabledServers, listenerBased, soundData );
    }
}
