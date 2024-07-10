package dev.endoy.bungeeutilisalsx.common.commands.plugin;

import dev.endoy.bungeeutilisalsx.common.api.command.CommandBuilder;
import dev.endoy.bungeeutilisalsx.common.api.command.CommandCall;
import dev.endoy.bungeeutilisalsx.common.api.command.ParentCommand;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.commands.plugin.sub.ReloadSubCommandCall;
import dev.endoy.bungeeutilisalsx.common.commands.plugin.sub.VersionSubCommandCall;

public class PluginCommandCall extends ParentCommand implements CommandCall
{

    public PluginCommandCall()
    {
        super( "general-commands.bungeeutilisals.help" );

        registerSubCommand(
                CommandBuilder.builder()
                        .name( "version" )
                        .fromSection( ConfigFiles.GENERALCOMMANDS.getConfig().getSection( "bungeeutilisals.subcommands.version" ) )
                        .executable( new VersionSubCommandCall() )
                        .build()
        );

        registerSubCommand(
                CommandBuilder.builder()
                        .name( "reload" )
                        .fromSection( ConfigFiles.GENERALCOMMANDS.getConfig().getSection( "bungeeutilisals.subcommands.reload" ) )
                        .executable( new ReloadSubCommandCall() )
                        .build()
        );
    }

    @Override
    public String getDescription()
    {
        return "The default / admin command to help manage the plugin.";
    }

    @Override
    public String getUsage()
    {
        return "/bungeeutilisals";
    }
}
