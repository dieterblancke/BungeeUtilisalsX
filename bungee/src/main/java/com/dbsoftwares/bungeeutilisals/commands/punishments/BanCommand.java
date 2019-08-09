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
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishmentFinishEvent;
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

public class BanCommand extends BUCommand {

    public BanCommand() {
        super(
                "ban",
                Arrays.asList(FileLocation.PUNISHMENTS.getConfiguration().getString("commands.ban.aliases").split(", ")),
                FileLocation.PUNISHMENTS.getConfiguration().getString("commands.ban.permission")
        );
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return null;
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length < 2) {
            user.sendLangMessage("punishments.ban.usage");
            return;
        }
        final Dao dao = BUCore.getApi().getStorageManager().getDao();
        final String reason = Utils.formatList(Arrays.copyOfRange(args, 1, args.length), " ");

        if (!dao.getUserDao().exists(args[0])) {
            user.sendLangMessage("never-joined");
            return;
        }

        final UserStorage storage = dao.getUserDao().getUserData(args[0]);
        if (dao.getPunishmentDao().getBansDao().isBanned(storage.getUuid())) {
            user.sendLangMessage("punishments.ban.already-banned");
            return;
        }

        final UserPunishEvent event = new UserPunishEvent(PunishmentType.BAN, user, storage.getUuid(),
                storage.getUserName(), storage.getIp(), reason, user.getServerName(), null);
        BUCore.getApi().getEventLoader().launchEvent(event);

        if (event.isCancelled()) {
            user.sendLangMessage("punishments.cancelled");
            return;
        }
        final IPunishmentExecutor executor = BUCore.getApi().getPunishmentExecutor();

        final PunishmentInfo info = dao.getPunishmentDao().getBansDao().insertBan(
                storage.getUuid(), storage.getUserName(), storage.getIp(),
                reason, user.getServerName(), true, user.getName()
        );

        BUCore.getApi().getUser(storage.getUserName()).ifPresent(banned -> {
            String kick = null;
            if (BUCore.getApi().getPunishmentExecutor().isTemplateReason(reason)) {
                kick = Utils.formatList(BUCore.getApi().getPunishmentExecutor().searchTemplate(
                        banned.getLanguageConfig(), PunishmentType.BAN, reason
                ), "\n");
            }
            if (kick == null) {
                kick = Utils.formatList(banned.getLanguageConfig().getStringList("punishments.ban.kick"), "\n");
            }
            kick = executor.setPlaceHolders(kick, info);

            banned.kick(kick);
        });

        user.sendLangMessage("punishments.ban.executed", executor.getPlaceHolders(info).toArray(new Object[0]));
        BUCore.getApi().langPermissionBroadcast("punishments.ban.broadcast",
                FileLocation.PUNISHMENTS.getConfiguration().getString("commands.ban.broadcast"),
                executor.getPlaceHolders(info).toArray(new Object[]{}));

        BUCore.getApi().getEventLoader().launchEvent(new UserPunishmentFinishEvent(
                PunishmentType.BAN, user, storage.getUuid(),
                storage.getUserName(), storage.getIp(), reason, user.getServerName(), null
        ));
    }
}