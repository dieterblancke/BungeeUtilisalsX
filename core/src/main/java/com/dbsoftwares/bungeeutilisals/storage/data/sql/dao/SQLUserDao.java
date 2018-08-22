package com.dbsoftwares.bungeeutilisals.storage.data.sql.dao;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
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

/*
 * Created by DBSoftwares on 10 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class SQLUserDao implements UserDao {

    private final static String INSERT_USER = "INSERT INTO {users-table} " +
            "(uuid, username, ip, language) VALUES (?, ?, ?, ?);";

    private final static String SELECT_USER = "SELECT %s FROM {users-table} WHERE %s;";
    private final static String UPDATE_USER = "UPDATE {users-table} " +
            "SET username = ?, ip = ?, language = ?, lastlogout = ? " +
            "WHERE uuid = ?;";

    private final static String UPDATE_USER_COLUMN = "UPDATE {users-table} SET %s = ? WHERE uuid = ?;";

    @Override
    public void createUser(UUID uuid, String username, String ip, Language language) {
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(format(INSERT_USER))) {
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, username);
            pstmt.setString(3, ip);
            pstmt.setString(4, language.getName());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateUser(UUID uuid, String name, String ip, Language language, Date logout) {
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(format(UPDATE_USER))) {
            pstmt.setString(1, name);
            pstmt.setString(2, ip);
            pstmt.setString(3, language.getName());
            pstmt.setDate(4, new java.sql.Date(logout.getTime()));
            pstmt.setString(5, uuid.toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
                storage.setUuid(uuid);
                storage.setUserName(rs.getString("username"));
                storage.setIp(rs.getString("ip"));
                storage.setLanguage(BUCore.getApi().getLanguageManager().getLangOrDefault(rs.getString("language")));
                storage.setFirstLogin(rs.getDate("firstlogin"));
                storage.setLastLogout(rs.getDate("lastlogout"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
                storage.setUuid(UUID.fromString(rs.getString("uuid")));
                storage.setUserName(name);
                storage.setIp(rs.getString("ip"));
                storage.setLanguage(BUCore.getApi().getLanguageManager().getLangOrDefault(rs.getString("language")));
                storage.setFirstLogin(rs.getDate("firstlogin"));
                storage.setLastLogout(rs.getDate("lastlogout"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    @Override
    public void setLogout(UUID uuid, Date logout) {
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(format(UPDATE_USER_COLUMN, "lastlogout"))) {
            pstmt.setDate(1, new java.sql.Date(logout.getTime()));
            pstmt.setString(2, uuid.toString());

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
