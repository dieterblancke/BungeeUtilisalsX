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

package be.dieterblancke.bungeeutilisalsx.common.commands.report;

import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandBuilder;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.command.ParentCommand;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.commands.report.sub.*;
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
}