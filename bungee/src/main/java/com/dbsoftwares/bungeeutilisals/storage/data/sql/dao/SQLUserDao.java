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
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.UserDao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.google.common.collect.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SQLUserDao implements UserDao {

    private static final String INSERT_USER = "INSERT INTO {users-table} " +
            "(uuid, username, ip, language, firstlogin, lastlogout) VALUES (?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE username = ?;";

    private static final String SQLITE_INSERT_USER = "INSERT INTO {users-table} " +
            "(uuid, username, ip, language, firstlogin, lastlogout) VALUES (?, ?, ?, ?, ?, ?) ON CONFLICT(uuid) DO UPDATE SET username = ?;";

    private static final String SELECT_USER = "SELECT %s FROM {users-table} WHERE %s;";
    private static final String UPDATE_USER = "UPDATE {users-table} " +
            "SET username = ?, ip = ?, language = ?, lastlogout = ? " +
            "WHERE uuid = ?;";

    private static final String UPDATE_USER_COLUMN = "UPDATE {users-table} SET %s = ? WHERE uuid = ?;";
    private static final String ERROR_STRING = "An error occured: ";

    @Override
    public void createUser(UUID uuid, String username, String ip, Language language) {
        Date date = new Date(System.currentTimeMillis());

        createUser(uuid, username, ip, language, date, date);
    }

    @Override
    public void createUser(UUID uuid, String username, String ip, Language language, Date login, Date logout) {
        final String statement =
                BungeeUtilisals.getInstance().getDatabaseManagement().getType().equals(AbstractStorageManager.StorageType.SQLITE)
                ? SQLITE_INSERT_USER : INSERT_USER;

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(format(statement))) {
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, username);
            pstmt.setString(3, ip);
            pstmt.setString(4, language.getName());
            pstmt.setString(5, Dao.formatDate(login));
            pstmt.setString(6, Dao.formatDate(logout));
            pstmt.setString(7, username);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
    }

    @Override
    public void updateUser(UUID uuid, String name, String ip, Language language, Date logout) {
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(format(UPDATE_USER))) {
            pstmt.setString(1, name);
            pstmt.setString(2, ip);
            pstmt.setString(3, language.getName());
            pstmt.setString(4, Dao.formatDate(logout));
            pstmt.setString(5, uuid.toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
    }

    @Override
    public boolean exists(String name) {
        boolean present = false;
        String statement = format(SELECT_USER, "id", name.contains(".") ? "ip = ?" : "username = ?");

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(statement)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                present = rs.next();
            }
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }

        return present;
    }

    @Override
    public boolean exists(UUID uuid) {
        boolean present = false;
        String statement = format(SELECT_USER, "id", "uuid = ?");

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(statement)) {
            pstmt.setString(1, uuid.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                present = rs.next();
            }
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }

        return present;
    }

    @Override
    public UserStorage getUserData(UUID uuid) {
        UserStorage storage = new UserStorage();
        String statement = format(SELECT_USER, "*", "uuid = ?");

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(statement)) {
            pstmt.setString(1, uuid.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    storage.setUuid(uuid);
                    storage.setUserName(rs.getString("username"));
                    storage.setIp(rs.getString("ip"));
                    storage.setLanguage(BUCore.getApi().getLanguageManager().getLangOrDefault(rs.getString("language")));
                    storage.setFirstLogin(rs.getDate("firstlogin"));
                    storage.setLastLogout(rs.getDate("lastlogout"));
                }
            }
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
        return storage;
    }

    @Override
    public UserStorage getUserData(String name) {
        UserStorage storage = new UserStorage();
        String statement = format(SELECT_USER, "*", "username = ?");

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(statement)) {
            pstmt.setString(1, name);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    storage.setUuid(UUID.fromString(rs.getString("uuid")));
                    storage.setUserName(name);
                    storage.setIp(rs.getString("ip"));
                    storage.setLanguage(BUCore.getApi().getLanguageManager().getLangOrDefault(rs.getString("language")));
                    storage.setFirstLogin(rs.getDate("firstlogin"));
                    storage.setLastLogout(rs.getDate("lastlogout"));
                }
            }
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
        return storage;
    }

    @Override
    public List<String> getUsersOnIP(String ip) {
        List<String> users = Lists.newArrayList();
        String statement = format(SELECT_USER, "username", "ip = ?");

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(statement)) {
            pstmt.setString(1, ip);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    users.add(rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
        return users;
    }

    @Override
    public Language getLanguage(UUID uuid) {
        Language language = null;
        String statement = format(SELECT_USER, "language", "uuid = ?");

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(statement)) {
            pstmt.setString(1, uuid.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    language = BUCore.getApi().getLanguageManager().getLangOrDefault(rs.getString("language"));
                }
            }
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
        return language;
    }

    @Override
    public void setName(UUID uuid, String name) {
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(format(UPDATE_USER_COLUMN, "username"))) {
            pstmt.setString(1, name);
            pstmt.setString(2, uuid.toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
    }

    @Override
    public void setIP(UUID uuid, String ip) {
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(format(UPDATE_USER_COLUMN, "ip"))) {
            pstmt.setString(1, ip);
            pstmt.setString(2, uuid.toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
    }

    @Override
    public void setLanguage(UUID uuid, Language language) {
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(format(UPDATE_USER_COLUMN, "language"))) {
            pstmt.setString(1, language.getName());
            pstmt.setString(2, uuid.toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
    }

    @Override
    public void setLogout(UUID uuid, Date logout) {
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(format(UPDATE_USER_COLUMN, "lastlogout"))) {
            pstmt.setString(1, Dao.formatDate(logout));
            pstmt.setString(2, uuid.toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
    }

    private String format(String line) {
        return PlaceHolderAPI.formatMessage(line);
    }

    private String format(String line, Object... replacements) {
        return PlaceHolderAPI.formatMessage(String.format(line, replacements));
    }
}
