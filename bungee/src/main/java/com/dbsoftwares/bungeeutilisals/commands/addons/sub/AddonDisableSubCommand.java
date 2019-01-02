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
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;

import java.util.List;
import java.util.stream.Collectors;

public class AddonDisableSubCommand extends SubCommand {

    public AddonDisableSubCommand() {
        super("disable", 1);
    }

    @Override
    public String getUsage() {
        return "/addons disable (name)";
    }

    @Override
    public String getPermission() {
        return "bungeeutilisals.admin.addons.disable";
    }

    @Override
    public void onExecute(User user, String[] args) {
        final String addonName = args[0];
        if (BUCore.getApi().getAddonManager().isRegistered(addonName)) {
            try {
                BUCore.getApi().getAddonManager().disableAddon(addonName);
                user.sendLangMessage("general-commands.addon.disable.success", "{name}", addonName);
            } catch (Exception e) {
                user.sendLangMessage("general-commands.addon.disable.error", "{name}", addonName);
            }
        } else {
            user.sendLangMessage("general-commands.addon.notfound", "{name}", addonName);
        }
    }

    @Override
    public List<String> getCompletions(User user, String[] args) {
        return BUCore.getApi().getAddonManager().getAddons().stream().map(addon -> addon.getDescription().getName()).collect(Collectors.toList());
    }
}
