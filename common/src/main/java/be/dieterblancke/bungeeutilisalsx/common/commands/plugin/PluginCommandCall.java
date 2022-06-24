package be.dieterblancke.bungeeutilisalsx.common.commands.plugin;

import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandBuilder;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.ParentCommand;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.commands.plugin.sub.ReloadSubCommandCall;
import be.dieterblancke.bungeeutilisalsx.common.commands.plugin.sub.VersionSubCommandCall;

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
