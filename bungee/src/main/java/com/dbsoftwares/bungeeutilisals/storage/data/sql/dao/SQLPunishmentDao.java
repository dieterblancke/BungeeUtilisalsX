package com.dbsoftwares.bungeeutilisals.storage.data.sql.dao;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

/*
 * Created by DBSoftwares on 10 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class SQLPunishmentDao implements PunishmentDao {

    private final static String SELECT = "SELECT %s FROM %s WHERE %s;";
    private final static String UPDATE = "UPDATE %s SET %s WHERE %s;";

    @Override
    public long getPunishmentsSince(String identifier, PunishmentType type, Date date) {
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
            e.printStackTrace();
        }

        return amount;
    }

    @Override
    public PunishmentInfo insertPunishment(PunishmentType type, UUID uuid, String user,
                                           String ip, String reason, Long time, String server,
                                           Boolean active, String executedby) {
        return insertPunishment(type, uuid, user, ip, reason, time, server, active, executedby, null);
    }

    @Override
    public PunishmentInfo insertPunishment(PunishmentType type, UUID uuid, String user,
                                           String ip, String reason, Long time, String server,
                                           Boolean active, String executedby, String removedby) {
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
                    e.printStackTrace();
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
                    e.printStackTrace();
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
                e.printStackTrace();
            }
        }

        PunishmentInfo info = new PunishmentInfo();

        info.setUuid(uuid);
        info.setUser(user);
        info.setIP(ip);
        info.setReason(reason);
        info.setServer(server);
        info.setExecutedBy(executedby);
        info.setDate(new Date(System.currentTimeMillis()));
        info.setType(type);

        if (time != null) {
            info.setExpireTime(time);
        }
        if (active != null) {
            info.setActive(active);
        }
        if (removedby != null) {
            info.setRemovedBy(removedby);
        }

        return info;
    }

    @Override
    public boolean isPunishmentPresent(PunishmentType type, UUID uuid, String IP, boolean checkActive) {
        boolean present = false;
        String statement = format(
                SELECT,
                "id",
                type.getTablePlaceHolder(),
                (type.isIP() ? "ip = ?" : "uuid = ?") + (checkActive ? " AND active = 1" : "")
        );

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(statement)) {
            pstmt.setString(1, type.isIP() ? IP : uuid.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                present = rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return present;
    }

    @Override
    public PunishmentInfo getPunishment(PunishmentType type, UUID uuid, String IP) {
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
            pstmt.setString(1, type.isIP() ? IP : uuid.toString());
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
            e.printStackTrace();
        }

        return info;
    }

    @Override
    public void removePunishment(PunishmentType type, UUID uuid, String IP) {
        String statement = format(
                UPDATE,
                type.getTablePlaceHolder(),
                "active = ?",
                (type.isIP() ? "ip" : "uuid") + " = ? AND active = ?"
        );

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(statement)) {
            pstmt.setBoolean(1, true);
            pstmt.setString(2, type.isIP() ? IP : uuid.toString());
            pstmt.setBoolean(3, false);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String format(String line) {
        return PlaceHolderAPI.formatMessage(line);
    }

    private String format(String line, Object... replacements) {
        return PlaceHolderAPI.formatMessage(String.format(line, replacements));
    }
}
