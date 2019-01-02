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

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Arrays;
import java.util.List;

public class FindCommand extends BUCommand {

    public FindCommand() {
        super(
                "find",
                Arrays.asList(FileLocation.GENERALCOMMANDS.getConfiguration().getString("find.aliases").split(", ")),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString("find.permission")
        );
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length != 1) {
            user.sendLangMessage("general-commands.find.usage");
            return;
        }
        ServerInfo server = BUCore.getApi().getPlayerUtils().findPlayer(args[0]);

        if (server == null) {
            user.sendLangMessage("offline");
            return;
        }
        user.sendLangMessage("general-commands.find.message", "{user}", args[0], "{server}", server.getName());
    }
}
