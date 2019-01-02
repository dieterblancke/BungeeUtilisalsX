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

package com.dbsoftwares.bungeeutilisals.commands.general;

import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Arrays;
import java.util.List;

public class ServerCommand extends BUCommand {

    public ServerCommand() {
        super(
                "server",
                Arrays.asList(FileLocation.GENERALCOMMANDS.getConfiguration().getString("server.aliases").split(", ")),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString("server.permission")
        );
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length != 1) {
            user.sendLangMessage("general-commands.server.usage", "{server}", user.getServerName());
            return;
        }
        ServerInfo server = ProxyServer.getInstance().getServerInfo(args[0]);

        if (server == null) {
            user.sendLangMessage("general-commands.server.notfound", "{server}", args[0]);
            return;
        }
        if (user.getServerName().equalsIgnoreCase(server.getName())) {
            user.sendLangMessage("general-commands.server.alreadyconnected", "{server}", args[0]);
            return;
        }
        user.getParent().connect(server);
        user.sendLangMessage("general-commands.server.connecting", "{server}", args[0]);
    }
}
