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

package com.dbsoftwares.bungeeutilisals.commands.addons.sub;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.exceptions.AddonException;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class AddonEnableSubCommand extends SubCommand {

    public AddonEnableSubCommand() {
        super("enable", 1);
    }

    @Override
    public String getUsage() {
        return "/addons enable (name)";
    }

    @Override
    public String getPermission() {
        return "bungeeutilisals.admin.addons.enable";
    }

    @Override
    public void onExecute(User user, String[] args) {
        final String addonName = args[0];
        if (BUCore.getApi().getAddonManager().isRegistered(addonName)) {
            user.sendLangMessage("general-commands.addon.enable.alreadyloaded", "{name}", addonName);
        } else {
            final File addonFile = searchAddon(addonName);

            if (addonFile == null) {
                user.sendLangMessage("general-commands.addon.notfound", "{name}", addonName);
            } else {
                try {
                    BUCore.getApi().getAddonManager().loadSingleAddon(addonFile);
                    BUCore.getApi().getAddonManager().enableAddon(addonName);

                    user.sendLangMessage("general-commands.addon.enable.success", "{name}", addonName);
                } catch (AddonException e) {
                    user.sendLangMessage("general-commands.addon.enable.failed", "{name}", addonName);
                    e.printStackTrace();
                }
            }
        }
    }

    private File searchAddon(final String name) {
        final File addonsFolder = BUCore.getApi().getAddonManager().getAddonsFolder();

        for (File file : addonsFolder.listFiles()) {
            if (file.getName().toLowerCase().startsWith(name.toLowerCase() + ".")) {
                return file;
            }
        }
        return null;
    }

    @Override
    public List<String> getCompletions(User user, String[] args) {
        return BUCore.getApi().getAddonManager().getAddons().stream().map(addon -> addon.getDescription().getName()).collect(Collectors.toList());
    }
}
