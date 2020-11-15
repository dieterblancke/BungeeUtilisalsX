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

package com.dbsoftwares.bungeeutilisalsx.common.commands.plugin;

import com.dbsoftwares.bungeeutilisalsx.common.api.command.CommandBuilder;
import com.dbsoftwares.bungeeutilisalsx.common.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.api.command.ParentCommand;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.common.commands.plugin.sub.*;

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

        registerSubCommand(
                CommandBuilder.builder()
                        .name( "dump" )
                        .fromSection( ConfigFiles.GENERALCOMMANDS.getConfig().getSection( "bungeeutilisals.subcommands.dump" ) )
                        .executable( new DumpSubCommandCall() )
                        .build()
        );

        registerSubCommand(
                CommandBuilder.builder()
                        .name( "convert" )
                        .fromSection( ConfigFiles.GENERALCOMMANDS.getConfig().getSection( "bungeeutilisals.subcommands.convert" ) )
                        .executable( new ConvertSubCommandCall() )
                        .build()
        );
    }
}
