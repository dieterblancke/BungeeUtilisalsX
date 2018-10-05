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

package com.dbsoftwares.bungeeutilisals.listeners;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.UUID;

public class PunishmentListener implements Listener {

    @EventHandler
    public void onLogin(LoginEvent event) {
        PendingConnection connection = event.getConnection();
        UUID uuid = connection.getUniqueId();
        String IP = Utils.getIP(connection.getAddress());

        BUAPI api = BUCore.getApi();
        PunishmentInfo info = null;
        IConfiguration language = api.getLanguageManager().getConfig(BungeeUtilisals.getInstance().getDescription().getName(), api.getLanguageManager().getDefaultLanguage());
        PunishmentDao dao = BUCore.getApi().getStorageManager().getDao().getPunishmentDao();

        if (dao.isPunishmentPresent(PunishmentType.BAN, uuid, null, true)) {
            info = dao.getPunishment(PunishmentType.BAN, uuid, null);
        } else if (dao.isPunishmentPresent(PunishmentType.IPBAN, null, IP, true)) {
            info = dao.getPunishment(PunishmentType.IPBAN, null, IP);
        } else if (dao.isPunishmentPresent(PunishmentType.TEMPBAN, uuid, null, true)) {
            info = dao.getPunishment(PunishmentType.TEMPBAN, uuid, null);
        } else if (dao.isPunishmentPresent(PunishmentType.IPTEMPBAN, null, IP, true)) {
            info = dao.getPunishment(PunishmentType.IPTEMPBAN, null, IP);
        }
        if (info != null) { // active punishment found
            if (info.isTemporary()) {
                if (info.getExpireTime() <= System.currentTimeMillis()) {
                    if (info.getType().equals(PunishmentType.TEMPBAN)) {
                        dao.removePunishment(PunishmentType.TEMPBAN, uuid, null, "CONSOLE");
                    } else {
                        dao.removePunishment(PunishmentType.IPTEMPBAN, null, IP, "CONSOLE");
                    }
                    return;
                }
            }
            String kick = Utils.formatList(language.getStringList("punishments." + info.getType().toString().toLowerCase() + ".kick"), "\n");
            kick = api.getPunishmentExecutor().setPlaceHolders(kick, info);

            event.setCancelled(true);
            event.setCancelReason(Utils.format(kick));
        }
    }
}