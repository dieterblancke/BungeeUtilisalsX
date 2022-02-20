package be.dieterblancke.bungeeutilisalsx.common.commands.report;

import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandBuilder;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.ParentCommand;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.commands.report.sub.*;

public class ReportCommandCall extends ParentCommand implements CommandCall
{

    public ReportCommandCall()
    {
        super( "general-commands.report.help" );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "create" )
                        .fromSection( ConfigFiles.GENERALCOMMANDS.getConfig().getSection( "report.subcommands.create" ) )
                        .executable( new ReportCreateSubCommandCall() )
                        .build()
        );
        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "list" )
                        .fromSection( ConfigFiles.GENERALCOMMANDS.getConfig().getSection( "report.subcommands.list" ) )
                        .executable( new ReportListSubCommandCall() )
                        .build()
        );
        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "accept" )
                        .fromSection( ConfigFiles.GENERALCOMMANDS.getConfig().getSection( "report.subcommands.accept" ) )
                        .executable( new ReportAcceptSubCommandCall() )
                        .build()
        );
        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "deny" )
                        .fromSection( ConfigFiles.GENERALCOMMANDS.getConfig().getSection( "report.subcommands.deny" ) )
                        .executable( new ReportDenySubCommandCall() )
                        .build()
        );
        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "history" )
                        .fromSection( ConfigFiles.GENERALCOMMANDS.getConfig().getSection( "report.subcommands.history" ) )
                        .executable( new ReportHistorySubCommandCall() )
                        .build()
        );
    }

    @Override
    public String getDescription()
    {
        return "This command sends a list of available report commands.";
    }

    @Override
    public String getUsage()
    {
        return "/report";
    }
}