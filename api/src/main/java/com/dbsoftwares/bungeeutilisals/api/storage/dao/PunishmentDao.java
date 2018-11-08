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

package com.dbsoftwares.bungeeutilisals.api.storage.dao;

import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;

import java.util.Date;
import java.util.UUID;

public interface PunishmentDao {

    long getPunishmentsSince(String identifier, PunishmentType type, Date date);

    PunishmentInfo insertPunishment(
            PunishmentType type, UUID uuid, String user, String ip, String reason,
            Long time, String server, Boolean active, String executedby
    );

    PunishmentInfo insertPunishment(
            PunishmentType type, UUID uuid, String user, String ip, String reason,
            Long time, String server, Boolean active, String executedby, String removedby
    );

    PunishmentInfo insertPunishment(
            PunishmentType type, UUID uuid, String user, String ip, String reason,
            Long time, String server, Boolean active, String executedby, Date date, String removedby
    );

    boolean isPunishmentPresent(PunishmentType type, UUID uuid, String IP, boolean checkActive);

    PunishmentInfo getPunishment(PunishmentType type, UUID uuid, String IP);

    void removePunishment(PunishmentType type, UUID uuid, String IP, String removedBy);
}
