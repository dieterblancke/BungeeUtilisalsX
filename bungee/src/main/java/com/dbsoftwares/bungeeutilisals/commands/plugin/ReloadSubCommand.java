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

package com.dbsoftwares.bungeeutilisals.commands.plugin;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.List;

public class ReloadSubCommand extends SubCommand {

    public ReloadSubCommand() {
        super("reload", 0);
    }

    @Override
    public String getUsage() {
        return "/bungeeutilisals reload";
    }

    @Override
    public String getPermission() {
        return "bungeeutilisals.admin.reload";
    }

    @Override
    public void onExecute(User user, String[] args) {
        for (FileLocation location : FileLocation.values()) {
            try {
                location.getConfiguration().reload();

                location.getData().clear();
                location.getDataList().clear();
                location.loadData();
            } catch (IOException e) {
                e.printStackTrace();
                user.sendMessage("&fCould not reload " + location.toString().toLowerCase().replace("_", " ") + "!");
            }
        }
        BungeeUtilisals.getInstance().reload();
        user.sendMessage("&fAll configuration files have been reloaded!");
    }

    @Override
    public List<String> getCompletions(User user, String[] args) {
        return ImmutableList.of();
    }
}
