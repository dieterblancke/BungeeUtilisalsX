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

package com.dbsoftwares.bungeeutilisals.commands.punishments;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.MathUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;

import java.util.Arrays;
import java.util.List;

public class PunishmentDataCommand extends BUCommand {

    public PunishmentDataCommand() {
        super("punishmentdata", Arrays.asList(FileLocation.PUNISHMENTS.getConfiguration()
                        .getString("commands.punishmentdata.aliases").split(", ")),
                FileLocation.PUNISHMENTS.getConfiguration().getString("commands.punishmentdata.permission")
        );
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return null;
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length < 2) {
            user.sendLangMessage("punishments.punishmentdata.usage");
            return;
        }

        final Dao dao = BUCore.getApi().getStorageManager().getDao();
        final PunishmentType type = Utils.valueOfOr(args[0].toUpperCase(), PunishmentType.BAN);
        final String id = args[1];

        if (BUCore.getApi().getStorageManager().getType().toString().contains("SQL") && !MathUtils.isInteger(id)) {
            user.sendLangMessage("no-number");
            return;
        }

        PunishmentInfo info = null;
        switch (type) {
            case BAN:
            case TEMPBAN:
            case IPBAN:
            case IPTEMPBAN:
                info = BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().getById(id);
                break;
            case MUTE:
            case TEMPMUTE:
            case IPMUTE:
            case IPTEMPMUTE:
                info = BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().getById(id);
                break;
            case KICK:
                info = BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getKickAndWarnDao().getKickById(id);
                break;
            case WARN:
                info = BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getKickAndWarnDao().getWarnById(id);
                break;
        }

        if (info == null) {
            user.sendLangMessage(
                    "punishments.punishmentdata.not-found",
                    "{type}", type.toString().toLowerCase(),
                    "{id}", id
            );
        } else {
            user.sendLangMessage(
                    "punishments.punishmentdata.found",
                    BUCore.getApi().getPunishmentExecutor().getPlaceHolders(info).toArray(new Object[0])
            );
        }
    }
}
