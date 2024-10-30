package dev.endoy.bungeeutilisalsx.common.api.utils.config.configs;

import dev.endoy.bungeeutilisalsx.common.api.utils.config.Config;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Getter
public class CommandBlockerConfig extends Config
{

    private final List<BlockedCommand> blockedCommands = new ArrayList<>();

    public CommandBlockerConfig( final String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        this.blockedCommands.clear();
    }

    @Override
    protected void setup()
    {
        if ( config == null || !isEnabled() )
        {
            return;
        }

        blockedCommands.clear();
        blockedCommands.addAll(
            config.getSectionList( "blockedcommands" ).stream()
                .map( section -> new BlockedCommand(
                    section.getString( "command" ),
                    section.exists( "bypass-permission" ) ? section.getString( "bypass-permission" ) : "",
                    section.exists( "subcommands" ) ?
                        section.getSectionList( "subcommands" ).stream()
                            .map( subcommand -> new BlockedSubCommand(
                                subcommand.getString( "command" ),
                                subcommand.getInteger( "index" )
                            ) )
                            .collect( Collectors.toList() )
                        : new ArrayList<>(),
                    section.exists( "servers" ) ?
                        section.getStringList( "servers" )
                            .stream()
                            .map( server -> ConfigFiles.SERVERGROUPS.getServer( server ) )
                            .flatMap( Optional::stream )
                            .collect( Collectors.toList() )
                        : new ArrayList<>()
                ) )
                .collect( Collectors.toList() )
        );
    }

    @Value
    public static class BlockedCommand
    {
        String command;
        String bypassPermission;
        List<BlockedSubCommand> subCommands;
        List<ServerGroup> servers;
    }

    @Value
    public static class BlockedSubCommand
    {
        String command;
        int index;
    }
}