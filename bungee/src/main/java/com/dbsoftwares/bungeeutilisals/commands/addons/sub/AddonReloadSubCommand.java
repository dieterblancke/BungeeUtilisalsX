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

package com.dbsoftwares.bungeeutilisals.commands.addons.sub;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;

import java.util.List;
import java.util.stream.Collectors;

public class AddonReloadSubCommand extends SubCommand {

    public AddonReloadSubCommand() {
        super("reload", 1);
    }

    @Override
    public String getUsage() {
        return "/addons reload (name)";
    }

    @Override
    public String getPermission() {
        return "bungeeutilisals.admin.addons.reload";
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args[0].equalsIgnoreCase("all")) {
            BUCore.getApi().getAddonManager().getAddons().forEach(a -> reloadAddon(user, a.getDescription().getName()));
            return;
        }
        reloadAddon(user, args[0]);
    }

    private void reloadAddon(User user, String addon) {
        if (BUCore.getApi().getAddonManager().isRegistered(addon)) {
            try {
                BUCore.getApi().getAddonManager().reloadAddon(addon);
                user.sendLangMessage("general-commands.addon.reload.success", "{name}", addon);
            } catch (Exception e) {
                BUCore.getLogger().error("An error occured: ", e);
                user.sendLangMessage("general-commands.addon.reload.error", "{name}", addon);
            }
        } else {
            user.sendLangMessage("general-commands.addon.notfound", "{name}", addon);
        }
    }

    @Override
    public List<String> getCompletions(User user, String[] args) {
        return BUCore.getApi().getAddonManager().getAddons().stream().map(addon -> addon.getDescription().getName()).collect(Collectors.toList());
    }
}
