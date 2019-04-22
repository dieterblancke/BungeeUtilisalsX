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
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;

import java.util.Arrays;
import java.util.List;

public class IPTempBanCommand extends BUCommand {

    public IPTempBanCommand() {
        super("iptempban", Arrays.asList(FileLocation.PUNISHMENTS.getConfiguration()
                        .getString("commands.iptempban.aliases").split(", ")),
                FileLocation.PUNISHMENTS.getConfiguration().getString("commands.iptempban.permission"));
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return null;
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length < 3) {
            user.sendLangMessage("punishments.iptempban.usage");
            return;
        }
        final Dao dao = BUCore.getApi().getStorageManager().getDao();
        final String timeFormat = args[1];
        final String reason = Utils.formatList(Arrays.copyOfRange(args, 2, args.length), " ");
        final long time = Utils.parseDateDiff(timeFormat);

        if (time == 0L) {
            user.sendLangMessage("punishments.iptempban.non-valid");
            return;
        }
        if (!dao.getUserDao().exists(args[0])) {
            user.sendLangMessage("never-joined");
            return;
        }
        final UserStorage storage = dao.getUserDao().getUserData(args[0]);
        if (dao.getPunishmentDao().getBansDao().isIPBanned(storage.getIp())) {
            user.sendLangMessage("punishments.iptempban.already-banned");
            return;
        }
        final UserPunishEvent event = new UserPunishEvent(
                PunishmentType.IPTEMPBAN, user, storage.getUuid(),
                storage.getUserName(), storage.getIp(), reason, user.getServerName(), time
        );
        BUCore.getApi().getEventLoader().launchEvent(event);

        if (event.isCancelled()) {
            user.sendLangMessage("punishments.cancelled");
            return;
        }
        final IPunishmentExecutor executor = BUCore.getApi().getPunishmentExecutor();
        final PunishmentInfo info = dao.getPunishmentDao().getBansDao().insertTempIPBan(
                storage.getUuid(), storage.getUserName(), storage.getIp(),
                reason, user.getServerName(), true, user.getName(), time
        );

        BUCore.getApi().getUser(storage.getUserName()).ifPresent(banned -> {
            String kick = Utils.formatList(banned.getLanguageConfig().getStringList("punishments.iptempban.kick"), "\n");
            kick = executor.setPlaceHolders(kick, info);

            banned.kick(kick);
        });

        user.sendLangMessage("punishments.iptempban.executed", executor.getPlaceHolders(info).toArray(new Object[0]));
        BUCore.getApi().langPermissionBroadcast("punishments.iptempban.broadcast",
                FileLocation.PUNISHMENTS.getConfiguration().getString("commands.iptempban.broadcast"),
                executor.getPlaceHolders(info).toArray(new Object[]{}));
    }
}