package com.dbsoftwares.bungeeutilisals.bungee.storage;

/*
 * Created by DBSoftwares on 18/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SQLStatements {

    /* SQL INSERT STATEMENTS */
    private final static String INSERT_INTO_USERS = "INSERT INTO {users-table} (uuid, username, ip, language) VALUES ('%s', '%s', '%s', '%s');";
    private final static String INSERT_INTO_BANS = "INSERT INTO {bans-table} (uuid, user, ip, reason, server, active, executed_by) VALUES ('%s', '%s', '%s', '%s', '%s', %s, '%s');";
    private final static String INSERT_INTO_IPBANS = "INSERT INTO {ipbans-table} (uuid, user, ip, reason, server, active, executed_by) VALUES ('%s', '%s', '%s', '%s', '%s', %s, '%s');";

    /* SQL SELECT STATEMENT */
    private final static String SELECT = "SELECT %s FROM {table} WHERE %s;";

    private final static String SELECT_FROM_USERS = SELECT.replace("{table}", "{users-table}");
    private final static String SELECT_FROM_BANS = SELECT.replace("{table}", "{bans-table}");
    private final static String SELECT_FROM_IPBANS = SELECT.replace("{table}", "{ipbans-table}");


    /* INSERTION STATEMENTS */
    public static void insertIntoUsers(String uuid, String username, String ip, String language) {
        String statement = String.format(PlaceHolderAPI.formatMessage(INSERT_INTO_USERS), uuid, username, ip, language);

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertIntoBans(String uuid, String user, String ip, String reason, String server, Boolean active, String executedby) {
        int activeNumber = active ? 1 : 0;

        String statement = String.format(PlaceHolderAPI.formatMessage(INSERT_INTO_BANS), uuid, server, ip, reason, server, activeNumber, executedby);
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertIntoIPBans(String uuid, String user, String ip, String reason, String server, Boolean active, String executedby) {
        int activeNumber = active ? 1 : 0;

        String statement = String.format(PlaceHolderAPI.formatMessage(INSERT_INTO_IPBANS), uuid, server, ip, reason, server, activeNumber, executedby);
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /* UPDATE STATEMENTS */
    public static void updateUser(String identifier, String name, String ip, String language) {
        StringBuilder statement = new StringBuilder("UPDATE " + PlaceHolderAPI.formatMessage("{users-table}") + " SET ");

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
        statement.append(" WHERE uuid = '").append(identifier).append("';");

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* SELECTION STATEMENTS */
    public static boolean isUserPresent(String name) {
        boolean present = false;
        String statement = String.format(PlaceHolderAPI.formatMessage(SELECT_FROM_USERS), "id", "username = '" + name + "'");

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            present = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return present;
    }

    public static boolean isUserPresent(UUID uuid) {
        boolean present = false;
        String statement = String.format(PlaceHolderAPI.formatMessage(SELECT_FROM_USERS), "id", "uuid = '" + uuid + "'");

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            present = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return present;
    }

    public static UserStorage getUser(UUID uuid) {
        UserStorage storage = new UserStorage();
        String statement = String.format(PlaceHolderAPI.formatMessage(SELECT_FROM_USERS), "*", "uuid = '" + uuid + "'");

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            if (resultSet.next()) {
                storage.setUuid(uuid);
                storage.setUserName(resultSet.getString("username"));
                storage.setIp(resultSet.getString("ip"));
                storage.setLanguage(getLanguageOrDefault(resultSet.getString("language")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return storage;
    }

    public static UserStorage getUser(String name) {
        UserStorage storage = new UserStorage();
        String statement = String.format(PlaceHolderAPI.formatMessage(SELECT_FROM_USERS), "*", "username = '" + name + "'");

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            if (resultSet.next()) {
                storage.setUuid(UUID.fromString(resultSet.getString("uuid")));
                storage.setUserName(resultSet.getString("username"));
                storage.setIp(resultSet.getString("ip"));
                storage.setLanguage(getLanguageOrDefault(resultSet.getString("language")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return storage;
    }

    public static boolean isBanPresent(UUID uuid, boolean checkActive) {
        boolean present = false;
        String statement = String.format(PlaceHolderAPI.formatMessage(SELECT_FROM_BANS), "id", "uuid = '" + uuid + "'" + (checkActive ? " AND active = 1" : ""));

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            present = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return present;
    }


    public static boolean isIPBanPresent(String ip, boolean checkActive) {
        boolean present = false;
        String statement = String.format(PlaceHolderAPI.formatMessage(SELECT_FROM_IPBANS), "id", "ip = '" + ip + "'" + (checkActive ? " AND active = 1" : ""));

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            present = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return present;
    }

    /* UTILITY METHODS */
    private static Language getLanguageOrDefault(String language) {
        return BUCore.getApi().getLanguageManager().getLanguage(language).orElse(BUCore.getApi().getLanguageManager().getDefaultLanguage());
    }
}