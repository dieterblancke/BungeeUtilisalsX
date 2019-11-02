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

package com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments;

import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;

import java.util.List;
import java.util.UUID;

public interface MutesDao
{

    boolean isMuted( UUID uuid );

    boolean isIPMuted( String ip );

    boolean isMuted( PunishmentType type, UUID uuid );

    boolean isIPMuted( PunishmentType type, String ip );

    PunishmentInfo insertMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby );

    PunishmentInfo insertIPMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby );

    PunishmentInfo insertTempMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration );

    PunishmentInfo insertTempIPMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration );

    PunishmentInfo getCurrentMute( UUID uuid );

    PunishmentInfo getCurrentIPMute( String ip );

    void removeCurrentMute( UUID uuid, String removedBy );

    void removeCurrentIPMute( String ip, String removedBy );

    List<PunishmentInfo> getMutes( final UUID uuid );

    List<PunishmentInfo> getIPMutes( final String ip );

    PunishmentInfo getById( final String id );
}
