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
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentAction;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import net.md_5.bungee.api.ProxyServer;

import java.util.Date;
import java.util.List;

public class UserPunishExecutor implements EventExecutor {

    @Event
    public void updateMute(UserPunishEvent event) {
        if (event.isMute()) {
            event.getUser().ifPresent(user -> user.setMute(event.getInfo()));
        }
    }

    @Event
    public void executeActions(UserPunishEvent event) {
        if (!FileLocation.PUNISHMENTS.hasData(event.getType().toString())) {
            return;
        }
        List<PunishmentAction> actions = FileLocation.PUNISHMENTS.getData(event.getType().toString());

        for (PunishmentAction action : actions) {
            long amount;

            if (event.isUserPunishment()) {
                // uuid involved
                amount = BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getPunishmentsSince(
                        event.getUUID().toString(),
                        event.getType(),
                        new Date(System.currentTimeMillis() - action.getUnit().toMillis(action.getTime()))
                );
            } else {
                // IP involved
                amount = BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getPunishmentsSince(
                        event.getIp(),
                        event.getType(),
                        new Date(System.currentTimeMillis() - action.getUnit().toMillis(action.getTime()))
                );
            }

            if (amount >= action.getLimit()) {
                action.getActions().forEach(command -> ProxyServer.getInstance().getPluginManager().dispatchCommand(
                        ProxyServer.getInstance().getConsole(),
                        command.replace("%user%", event.getName())));
            }
        }
    }
}