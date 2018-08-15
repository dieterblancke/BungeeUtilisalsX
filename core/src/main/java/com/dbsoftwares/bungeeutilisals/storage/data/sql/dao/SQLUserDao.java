package com.dbsoftwares.bungeeutilisals.storage.data.sql.dao;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.UserDao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.google.common.collect.Lists;

import java.sql.*;
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

    @Override
    public void createUser(String uuid, String username, String ip, String language) {
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(format(INSERT_USER))) {
            pstmt.setString(1, uuid);
            pstmt.setString(2, username);
            pstmt.setString(3, ip);
            pstmt.setString(4, language);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateUser(String uuid, String name, String ip, String language) {
        StringBuilder statement = new StringBuilder(
                "UPDATE " + PlaceHolderAPI.formatMessage("{users-table}") + " SET "
        );

        if (name != null) {
            statement.append(" username = '").append(name).append("', ");
        }
        if (ip != null) {
            statement.append(" ip = '").append(ip).append("', ");
        }
        if (language != null) {
            statement.append(" language = '").append(language).append("', ");
        }
        statement.delete(statement.length() - 2, statement.length());
        statement.append(" WHERE uuid = '").append(uuid).append("';");

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection();
             Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(statement.toString());
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

    private String format(String line) {
        return PlaceHolderAPI.formatMessage(line);
    }

    private String format(String line, Object... replacements) {
        return String.format(PlaceHolderAPI.formatMessage(line), replacements);
    }
}
