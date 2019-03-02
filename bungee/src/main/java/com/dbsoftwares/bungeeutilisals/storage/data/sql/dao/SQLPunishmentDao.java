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

package com.dbsoftwares.bungeeutilisals.storage.data.sql.dao;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.BansDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.KickAndWarnDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.MutesDao;
import com.dbsoftwares.bungeeutilisals.storage.data.sql.dao.punishment.SQLBansDao;
import com.dbsoftwares.bungeeutilisals.storage.data.sql.dao.punishment.SQLKickAndWarnDao;
import com.dbsoftwares.bungeeutilisals.storage.data.sql.dao.punishment.SQLMutesDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class SQLPunishmentDao implements PunishmentDao {

    private final BansDao bansDao;
    private final MutesDao mutesDao;
    private final KickAndWarnDao kickAndWarnDao;

    public SQLPunishmentDao() {
        this.bansDao = new SQLBansDao();
        this.mutesDao = new SQLMutesDao();
        this.kickAndWarnDao = new SQLKickAndWarnDao();
    }

    @Override
    public BansDao getBansDao() {
        return bansDao;
    }

    @Override
    public MutesDao getMutesDao() {
        return mutesDao;
    }

    @Override
    public KickAndWarnDao getKickAndWarnDao() {
        return kickAndWarnDao;
    }

    @Override
    public long getPunishmentsSince(PunishmentType type, UUID uuid, Date date) {
        int count = 0;

        if (type.isActivatable()) {
            try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(
                         "SELECT COUNT(id) FROM " + type.getTable() + " WHERE uuid = ? AND date >= ? AND type = ?;"
                 )) {
                pstmt.setString(1, uuid.toString());
                pstmt.setString(2, Dao.formatDate(date));
                pstmt.setString(3, type.toString());

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                }
            } catch (SQLException e) {
                BUCore.logException(e);
            }
        } else {
            try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
                 PreparedStatement pstmt = connection.prepareStatement(
                         "SELECT COUNT(id) FROM " + type.getTable() + " WHERE uuid = ? AND date >= ?;"
                 )) {
                pstmt.setString(1, uuid.toString());
                pstmt.setString(2, Dao.formatDate(date));

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt(1);
                    }
                }
            } catch (SQLException e) {
                BUCore.logException(e);
            }
        }

        return count;
    }

    @Override
    public long getIPPunishmentsSince(PunishmentType type, String ip, Date date) {
        int count = 0;

        try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     "SELECT COUNT(id) FROM " + type.getTable() + " WHERE ip = ? AND date >= ? AND type = ?;"
             )) {
            pstmt.setString(1, ip);
            pstmt.setString(2, Dao.formatDate(date));
            pstmt.setString(3, type.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            BUCore.logException(e);
        }
        return count;
    }
}
