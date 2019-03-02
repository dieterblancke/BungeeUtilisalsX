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

package com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments;

import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;

import java.util.UUID;

public interface BansDao {

    boolean isBanned(UUID uuid);

    boolean isIPBanned(String ip);

    boolean isBanned(PunishmentType type, UUID uuid);

    boolean isIPBanned(PunishmentType type, String ip);

    PunishmentInfo insertBan(UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby);

    PunishmentInfo insertIPBan(UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby);

    PunishmentInfo insertTempBan(UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration);

    PunishmentInfo insertTempIPBan(UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration);

    PunishmentInfo getCurrentBan(UUID uuid);

    PunishmentInfo getCurrentIPBan(String ip);

    void removeCurrentBan(UUID uuid, String removedBy);

    void removeCurrentIPBan(String ip, String removedBy);
}