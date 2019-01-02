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

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisals.api.utils.Validate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class SQLPunishmentDao implements PunishmentDao {

    private static final String SELECT = "SELECT %s FROM %s WHERE %s;";
    private static final String UPDATE = "UPDATE %s SET %s WHERE %s;";

    @Override
    public long getPunishmentsSince(final String identifier, final PunishmentType type, final Date date) {
        long amount = 0;

        String statement = format(
                SELECT,
                "COUNT(*) count",
                type.getTablePlaceHolder(),
                (type.isIP() ? "ip" : "uuid") + " = ? AND date >= ?"
        );

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(statement)) {
            pstmt.setString(1, identifier);
            pstmt.setDate(2, new java.sql.Date(date.getTime()));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    amount = rs.getLong("count");
                }
            }
        } catch (SQLException e) {
            BUCore.logException(e);
        }

        return amount;
    }

    @Override
    public PunishmentInfo insertPunishment(final PunishmentType type, final UUID uuid, final String user,
                                           final String ip, final String reason, final Long time, final String server,
                                           final Boolean active, final String executedby) {
        return insertPunishment(type, uuid, user, ip, reason, time, server, active, executedby, null);
    }

    @Override
    public PunishmentInfo insertPunishment(final PunishmentType type, final UUID uuid, final String user,
                                           final String ip, final String reason, final Long time, final String server,
                                           final Boolean active, final String executedby, final String removedby) {
        return insertPunishment(type, uuid, user, ip, reason, time, server, active, executedby, new Date(System.currentTimeMillis()), removedby);
    }

    @Override
    public PunishmentInfo insertPunishment(final PunishmentType type, final UUID uuid, final String user, final String ip,
                                           final String reason, final Long time, final String server,
                                           final Boolean active, final String executedby, final Date date,
                                           final String removedby) {
        String sql = PlaceHolderAPI.formatMessage("INSERT INTO " + type.getTablePlaceHolder() + " ");

        if (type.isActivatable()) {
            if (type.isTemporary()) {
                sql += "(uuid, user, ip, time, reason, server, active, executed_by, removed_by) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

                try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, uuid.toString());
                    preparedStatement.setString(2, user);
                    preparedStatement.setString(3, ip);
                    preparedStatement.setLong(4, time);
                    preparedStatement.setString(5, reason);
                    preparedStatement.setString(6, server);
                    preparedStatement.setBoolean(7, active);
                    preparedStatement.setString(8, executedby);
                    preparedStatement.setString(9, removedby);

                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    BUCore.logException(e);
                }
            } else {
                sql += "(uuid, user, ip, reason, server, active, executed_by, removed_by) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

                try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
                     PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.setString(1, uuid.toString());
                    preparedStatement.setString(2, user);
                    preparedStatement.setString(3, ip);
                    preparedStatement.setString(4, reason);
                    preparedStatement.setString(5, server);
                    preparedStatement.setBoolean(6, active);
                    preparedStatement.setString(7, executedby);
                    preparedStatement.setString(8, removedby);

                    preparedStatement.executeUpdate();
                } catch (SQLException e) {
                    BUCore.logException(e);
                }
            }
        } else {
            sql += "(uuid, user, ip, reason, server, executed_by) "
                    + "VALUES (?, ?, ?, ?, ?, ?);";

            try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                preparedStatement.setString(1, uuid.toString());
                preparedStatement.setString(2, user);
                preparedStatement.setString(3, ip);
                preparedStatement.setString(4, reason);
                preparedStatement.setString(5, server);
                preparedStatement.setString(7, executedby);

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                BUCore.logException(e);
            }
        }

        PunishmentInfo info = new PunishmentInfo();

        info.setUuid(uuid);
        info.setUser(user);
        info.setIP(ip);
        info.setReason(reason);
        info.setServer(server);
        info.setExecutedBy(executedby);
        info.setDate(date);
        info.setType(type);

        Validate.ifNotNull(time, info::setExpireTime);
        Validate.ifNotNull(active, info::setActive);
        Validate.ifNotNull(removedby, info::setRemovedBy);

        return info;
    }

    @Override
    public boolean isPunishmentPresent(final PunishmentType type, final UUID uuid, final String ip, final boolean checkActive) {
        boolean present = false;
        String statement = format(
                SELECT,
                "id",
                type.getTablePlaceHolder(),
                (type.isIP() ? "ip = ?" : "uuid = ?") + (checkActive ? " AND active = 1" : "")
        );

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(statement)) {
            pstmt.setString(1, type.isIP() ? ip : uuid.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                present = rs.next();
            }
        } catch (SQLException e) {
            BUCore.logException(e);
        }

        return present;
    }

    @Override
    public PunishmentInfo getPunishment(final PunishmentType type, final UUID uuid, final String ip) {
        PunishmentInfo info = new PunishmentInfo();
        info.setType(PunishmentType.BAN);

        String statement = format(
                SELECT,
                "*",
                type.getTablePlaceHolder(),
                (type.isIP() ? "ip" : "uuid") + " = ? AND active = ?"
        );
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(statement)) {
            pstmt.setString(1, type.isIP() ? ip : uuid.toString());
            pstmt.setBoolean(2, true);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    info.setId(rs.getInt("id"));
                    info.setUuid(UUID.fromString(rs.getString("uuid")));
                    info.setUser(rs.getString("user"));
                    info.setIP(rs.getString("ip"));
                    info.setReason(rs.getString("reason"));
                    info.setServer(rs.getString("server"));
                    info.setDate(rs.getTimestamp("date"));
                    info.setExecutedBy(rs.getString("executed_by"));

                    if (type.isActivatable()) {
                        info.setActive(rs.getBoolean("active"));
                        info.setRemovedBy(rs.getString("removed_by"));
                    }
                    if (type.isTemporary()) {
                        info.setExpireTime(rs.getLong("time"));
                    }
                }
            }
        } catch (SQLException e) {
            BUCore.logException(e);
        }

        return info;
    }

    @Override
    public void removePunishment(final PunishmentType type, final UUID uuid, final String ip, final String removedBy) {
        String statement = format(
                UPDATE,
                type.getTablePlaceHolder(),
                "active = ?, removed_by = ?",
                (type.isIP() ? "ip" : "uuid") + " = ? AND active = ?"
        );

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(statement)) {
            pstmt.setBoolean(1, false);
            pstmt.setString(2, removedBy);
            pstmt.setString(3, type.isIP() ? ip : uuid.toString());
            pstmt.setBoolean(4, true);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            BUCore.logException(e);
        }
    }

    private String format(final String line, final Object... replacements) {
        return PlaceHolderAPI.formatMessage(String.format(line, replacements));
    }
}
