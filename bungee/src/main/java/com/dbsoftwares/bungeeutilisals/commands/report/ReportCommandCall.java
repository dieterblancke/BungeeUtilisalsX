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

package com.dbsoftwares.bungeeutilisals.commands.report;

import com.dbsoftwares.bungeeutilisals.api.command.CommandBuilder;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.command.ParentCommand;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.commands.report.sub.ReportAcceptSubCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.report.sub.ReportCreateSubCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.report.sub.ReportHistorySubCommandCall;
import com.dbsoftwares.bungeeutilisals.commands.report.sub.ReportListSubCommandCall;

public class ReportCommandCall extends ParentCommand implements CommandCall {

    public ReportCommandCall() {
        super("general-commands.report.help");

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name("create")
                        .fromSection(FileLocation.GENERALCOMMANDS.getConfiguration().getSection("report.subcommands.create"))
                        .executable(new ReportCreateSubCommandCall())
                        .build()
        );
        super.registerSubCommand(
                CommandBuilder.builder()
                        .name("list")
                        .fromSection(FileLocation.GENERALCOMMANDS.getConfiguration().getSection("report.subcommands.list"))
                        .executable(new ReportListSubCommandCall())
                        .build()
        );
        super.registerSubCommand(
                CommandBuilder.builder()
                        .name("accept")
                        .fromSection(FileLocation.GENERALCOMMANDS.getConfiguration().getSection("report.subcommands.accept"))
                        .executable(new ReportAcceptSubCommandCall())
                        .build()
        );
        super.registerSubCommand(
                CommandBuilder.builder()
                        .name("deny")
                        .fromSection(FileLocation.GENERALCOMMANDS.getConfiguration().getSection("report.subcommands.deny"))
                        .executable(new ReportAcceptSubCommandCall())
                        .build()
        );
        super.registerSubCommand(
                CommandBuilder.builder()
                        .name("history")
                        .fromSection(FileLocation.GENERALCOMMANDS.getConfiguration().getSection("report.subcommands.history"))
                        .executable(new ReportHistorySubCommandCall())
                        .build()
        );
    }
}