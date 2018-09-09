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

package com.dbsoftwares.bungeeutilisals.executors;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.event.Priority;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserCommandEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;

public class MuteCheckExecutor implements EventExecutor {

    @Event
    public void onCommand(UserCommandEvent event) {
        User user = event.getUser();

        if (!user.isMuted()) {
            return;
        }
        PunishmentInfo info = user.getMuteInfo();
        if (checkTemporaryMute(user, info)) {
            return;
        }

        if (FileLocation.PUNISHMENTS.getConfiguration().getStringList("blocked-mute-commands")
                .contains(event.getActualCommand().replaceFirst("/", ""))) {

            user.sendLangMessage("punishments." + info.getType().toString().toLowerCase() + ".onmute",
                    event.getApi().getPunishmentExecutor().getPlaceHolders(info).toArray(new Object[]{}));
            event.setCancelled(true);
        }
    }

    // high priority
    @Event(priority = Priority.HIGHEST)
    public void onChat(UserChatEvent event) {
        User user = event.getUser();

        if (!user.isMuted()) {
            return;
        }
        PunishmentInfo info = user.getMuteInfo();
        if (checkTemporaryMute(user, info)) {
            return;
        }

        user.sendLangMessage("punishments." + info.getType().toString().toLowerCase() + ".onmute",
                event.getApi().getPunishmentExecutor().getPlaceHolders(info).toArray(new Object[]{}));
        event.setCancelled(true);
    }

    private boolean checkTemporaryMute(User user, PunishmentInfo info) {
        if (info.isTemporary()) {
            if (info.getExpireTime() <= System.currentTimeMillis()) {
                if (info.getType().equals(PunishmentType.TEMPMUTE)) {
                    BUCore.getApi().getStorageManager().getDao().getPunishmentDao().removePunishment(
                            PunishmentType.TEMPMUTE, user.getParent().getUniqueId(), null
                    );
                } else {
                    BUCore.getApi().getStorageManager().getDao().getPunishmentDao().removePunishment(
                            PunishmentType.IPTEMPMUTE, null, user.getIP()
                    );
                }
                return true;
            }
        }
        return false;
    }
}