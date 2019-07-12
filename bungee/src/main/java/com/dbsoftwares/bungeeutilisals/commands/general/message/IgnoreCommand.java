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

package com.dbsoftwares.bungeeutilisals.commands.general.message;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.UserDao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;

public class IgnoreCommand extends BUCommand {

    public IgnoreCommand() {
        super(
                "ignore",
                Arrays.asList(FileLocation.GENERALCOMMANDS.getConfiguration().getString("ignore.aliases").split(", ")),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString("ignore.permission")
        );
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length == 0) {
            user.sendLangMessage("general-commands.ignore.usage");
            return;
        }
        final String action = args[0];

        if (action.equalsIgnoreCase("add") || action.equalsIgnoreCase("remove")) {
            if (args.length != 2) {
                user.sendLangMessage("general-commands.ignore.usage");
                return;
            }
            final String name = args[1];

            if (user.getName().equalsIgnoreCase(name)) {
                user.sendLangMessage("general-commands.ignore.self-ignore");
                return;
            }

            final UserDao dao = BUCore.getApi().getStorageManager().getDao().getUserDao();

            if (!dao.exists(name)) {
                user.sendLangMessage("never-joined");
                return;
            }

            final UserStorage storage = dao.getUserData(name);

            if (action.equalsIgnoreCase("remove")) {
                if (user.getStorage().getIgnoredUsers().stream().noneMatch(ignored -> ignored.equalsIgnoreCase(name))) {
                    user.sendLangMessage("general-commands.ignore.remove.not-ignored", "{user}", name);
                    return;
                }

                BUCore.getApi().getStorageManager().getDao().getUserDao().unignoreUser(user.getUuid(), storage.getUuid());
                user.getStorage().getIgnoredUsers().removeIf(ignored -> ignored.equalsIgnoreCase(name));

                user.sendLangMessage("general-commands.ignore.remove.unignored", "{user}", name);
            } else {
                if (user.getStorage().getIgnoredUsers().stream().anyMatch(ignored -> ignored.equalsIgnoreCase(name))) {
                    user.sendLangMessage("general-commands.ignore.add.already-ignored", "{user}", name);
                    return;
                }

                BUCore.getApi().getStorageManager().getDao().getUserDao().ignoreUser(user.getUuid(), storage.getUuid());
                user.getStorage().getIgnoredUsers().add(storage.getUserName());

                user.sendLangMessage("general-commands.ignore.add.ignored", "{user}", name);
            }
        } else if (action.equalsIgnoreCase("list")) {
            user.sendLangMessage(
                    "general-commands.ignore.list.message",
                    "{ignoredusers}",
                    String.join(
                            user.getLanguageConfig().getString("general-commands.ignore.list.separator"),
                            user.getStorage().getIgnoredUsers()
                    )
            );
        } else {
            user.sendLangMessage("general-commands.ignore.usage");
        }
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return ImmutableList.of();
    }
}
