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

package com.dbsoftwares.bungeeutilisals.storage.data.sql.dao.punishment;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.KickAndWarnDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

public class SQLKickAndWarnDao implements KickAndWarnDao {

    @Override
    public PunishmentInfo insertWarn(UUID uuid, String user, String ip, String reason, String server, String executedby) {
        try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     "INSERT INTO " + PunishmentType.WARN.getTable() + " (uuid, user, ip, reason, server, executed_by, date)" +
                             " VALUES (?, ?, ?, ?, ?, ?, ?);"
             )) {
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, user);
            pstmt.setString(3, ip);
            pstmt.setString(4, reason);
            pstmt.setString(5, server);
            pstmt.setString(6, executedby);
            pstmt.setTimestamp(7, new Timestamp(new Date().getTime()));

            pstmt.executeUpdate();
        } catch (SQLException e) {
            BUCore.logException(e);
        }
        return PunishmentDao.buildPunishmentInfo(PunishmentType.WARN, uuid, user, ip, reason, server, executedby, new Date(), -1, true, null);
    }

    @Override
    public PunishmentInfo insertKick(UUID uuid, String user, String ip, String reason, String server, String executedby) {
        try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     "INSERT INTO " + PunishmentType.KICK.getTable() + " (uuid, user, ip, reason, server, executed_by, date)" +
                             " VALUES (?, ?, ?, ?, ?, ?, ?);"
             )) {
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, user);
            pstmt.setString(3, ip);
            pstmt.setString(4, reason);
            pstmt.setString(5, server);
            pstmt.setString(6, executedby);
            pstmt.setTimestamp(7, new Timestamp(new Date().getTime()));

            pstmt.executeUpdate();
        } catch (SQLException e) {
            BUCore.logException(e);
        }
        return PunishmentDao.buildPunishmentInfo(PunishmentType.KICK, uuid, user, ip, reason, server, executedby, new Date(), -1, true, null);
    }
}
