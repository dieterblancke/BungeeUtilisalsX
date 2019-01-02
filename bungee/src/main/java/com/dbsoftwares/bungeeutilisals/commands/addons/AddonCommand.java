/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *  *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *  *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.commands.addons;

import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.commands.addons.sub.*;
import com.google.common.collect.Lists;

import java.util.List;

public class AddonCommand extends BUCommand {

    public AddonCommand() {
        super("addon", Lists.newArrayList("addons"), "bungeeutilisals.admin.addons");

        subCommands.add(new AddonListSubCommand());
        subCommands.add(new AddonInfoSubCommand());
        subCommands.add(new AddonReloadSubCommand());
        subCommands.add(new AddonInstallSubCommand());
        subCommands.add(new AddonUninstallSubCommand());
        subCommands.add(new AddonDisableSubCommand());
        subCommands.add(new AddonEnableSubCommand());
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return getSubcommandCompletions(user, args);
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length == 0) {
            sendHelpList(user);
            return;
        }
        for (SubCommand subCommand : subCommands) {
            if (subCommand.execute(user, args)) {
                return;
            }
        }
        sendHelpList(user);
    }

    private void sendHelpList(User user) {
        user.sendMessage("&aAddon Commands help:");
        subCommands.forEach(cmd -> user.sendMessage("&b- &e" + cmd.getUsage()));
    }
}
