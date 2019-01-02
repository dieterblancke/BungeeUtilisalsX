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

package com.dbsoftwares.bungeeutilisals.commands.punishments;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class WarnCommand extends BUCommand {

    public WarnCommand() {
        super("warn", Arrays.asList(FileLocation.PUNISHMENTS.getConfiguration()
                        .getString("commands.warn.aliases").split(", ")),
                FileLocation.PUNISHMENTS.getConfiguration().getString("commands.warn.permission"));
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return null;
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length < 2) {
            user.sendLangMessage("punishments.warn.usage");
            return;
        }
        String reason = Utils.formatList(Arrays.copyOfRange(args, 1, args.length), " ");

        Optional<User> optionalUser = BUCore.getApi().getUser(args[0]);
        if (!optionalUser.isPresent()) {
            user.sendLangMessage("offline");
            return;
        }
        User target = optionalUser.get();
        UserStorage storage = target.getStorage();

        UserPunishEvent event = new UserPunishEvent(PunishmentType.WARN, user, storage.getUuid(),
                storage.getUserName(), storage.getIp(), reason, user.getServerName(), null);
        BUCore.getApi().getEventLoader().launchEvent(event);

        if (event.isCancelled()) {
            user.sendLangMessage("punishments.cancelled");
            return;
        }
        IPunishmentExecutor executor = BUCore.getApi().getPunishmentExecutor();

        PunishmentInfo info = BUCore.getApi().getStorageManager().getDao().getPunishmentDao().insertPunishment(
                PunishmentType.WARN, storage.getUuid(), storage.getUserName(), storage.getIp(),
                reason, 0L, user.getServerName(), true, user.getName()
        );

        target.sendLangMessage("punishments.warn.onwarn", executor.getPlaceHolders(info).toArray(new Object[]{}));
        user.sendLangMessage("punishments.warn.executed", executor.getPlaceHolders(info));

        BUCore.getApi().langPermissionBroadcast("punishments.warn.broadcast",
                FileLocation.PUNISHMENTS.getConfiguration().getString("commands.warn.broadcast"),
                executor.getPlaceHolders(info).toArray(new Object[]{}));
    }
}