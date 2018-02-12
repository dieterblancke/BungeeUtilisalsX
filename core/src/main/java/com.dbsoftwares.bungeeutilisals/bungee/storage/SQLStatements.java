package com.dbsoftwares.bungeeutilisals.bungee.storage;

/*
 * Created by DBSoftwares on 18/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

public class SQLStatements {

    /* SQL INSERT STATEMENTS */
    private final static String INSERT_INTO_USERS = "INSERT INTO {users-table} (uuid, username, ip, language) VALUES ('%s', '%s', '%s', '%s');";

    private final static String INSERT_INTO_BANS = "INSERT INTO {bans-table} (uuid, user, ip, reason, server, active, executed_by) VALUES ('%s', '%s', '%s', '%s', '%s', %s, '%s');";
    private final static String INSERT_INTO_IPBANS = "INSERT INTO {ipbans-table} (uuid, user, ip, reason, server, active, executed_by) VALUES ('%s', '%s', '%s', '%s', '%s', %s, '%s');";
    private final static String INSERT_INTO_TEMPBANS = "INSERT INTO {tempbans-table} (uuid, user, ip, time, reason, server, active, executed_by) VALUES ('%s', '%s', '%s', %s, '%s', '%s', %s, '%s');";
    private final static String INSERT_INTO_IPTEMPBANS = "INSERT INTO {iptempbans-table} (uuid, user, ip, time, reason, server, active, executed_by) VALUES ('%s', '%s', '%s', %s, '%s', '%s', %s, '%s');";

    private final static String INSERT_INTO_MUTES = "INSERT INTO {mutes-table} (uuid, user, ip, reason, server, active, executed_by) VALUES ('%s', '%s', '%s', '%s', '%s', %s, '%s');";
    private final static String INSERT_INTO_IPMUTES = "INSERT INTO {ipmutes-table} (uuid, user, ip, reason, server, active, executed_by) VALUES ('%s', '%s', '%s', '%s', '%s', %s, '%s');";
    private final static String INSERT_INTO_TEMPMUTES = "INSERT INTO {tempmutes-table} (uuid, user, ip, time, reason, server, active, executed_by) VALUES ('%s', '%s', '%s', %s, '%s', '%s', %s, '%s');";
    private final static String INSERT_INTO_IPTEMPMUTES = "INSERT INTO {iptempmutes-table} (uuid, user, ip, time, reason, server, active, executed_by) VALUES ('%s', '%s', '%s', %s, '%s', '%s', %s, '%s');";

    private final static String INSERT_INTO_WARNS = "INSERT INTO {warns-table} (uuid, user, ip, reason, server, executed_by) VALUES ('%s', '%s', '%s', '%s', '%s', '%s');";
    private final static String INSERT_INTO_KICKS = "INSERT INTO {kicks-table} (uuid, user, ip, reason, server, executed_by) VALUES ('%s', '%s', '%s', '%s', '%s', '%s');";

    /* SQL SELECT STATEMENT */
    private final static String SELECT = "SELECT %s FROM {table} WHERE %s;";

    private final static String SELECT_FROM_USERS = SELECT.replace("{table}", "{users-table}");

    private final static String SELECT_FROM_BANS = SELECT.replace("{table}", "{bans-table}");
    private final static String SELECT_FROM_IPBANS = SELECT.replace("{table}", "{ipbans-table}");
    private final static String SELECT_FROM_TEMPBANS = SELECT.replace("{table}", "{tempbans-table}");
    private final static String SELECT_FROM_IPTEMPBANS = SELECT.replace("{table}", "{iptempbans-table}");

    private final static String SELECT_FROM_MUTES = SELECT.replace("{table}", "{mutes-table}");
    private final static String SELECT_FROM_IPMUTES = SELECT.replace("{table}", "{ipmutes-table}");
    private final static String SELECT_FROM_TEMPMUTES = SELECT.replace("{table}", "{tempmutes-table}");
    private final static String SELECT_FROM_IPTEMPMUTES = SELECT.replace("{table}", "{iptempmutes-table}");

    private final static String SELECT_FROM_WARNS = SELECT.replace("{table}", "{warns-table}");
    private final static String SELECT_FROM_KICKS = SELECT.replace("{table}", "{kicks-table}");

    /* SQL DELETE STATEMENTS */
    private final static String UPDATE_BANS = "UPDATE {bans-table} SET active = 0 WHERE uuid = '%s' AND active = 1;";
    private final static String UPDATE_IPBANS = "UPDATE {ipbans-table} SET active = 0 WHERE ip = '%s' AND active = 1;";
    private final static String UPDATE_TEMPBANS = "UPDATE {tempbans-table} SET active = 0 WHERE uuid = '%s' AND active = 1;";
    private final static String UPDATE_IPTEMPBANS = "UPDATE {iptempbans-table} SET active = 0 WHERE ip = '%s' AND active = 1;";

    private final static String UPDATE_MUTES = "UPDATE {mutes-table} SET active = 0 WHERE uuid = '%s' AND active = 1;";
    private final static String UPDATE_IPMUTES = "UPDATE {ipmutes-table} SET active = 0 WHERE ip = '%s' AND active = 1;";
    private final static String UPDATE_TEMPMUTES = "UPDATE {tempmutes-table} SET active = 0 WHERE uuid = '%s' AND active = 1;";
    private final static String UPDATE_IPTEMPMUTES = "UPDATE {iptempmutes-table} SET active = 0 WHERE ip = '%s' AND active = 1;";

    private final static String UPDATE_WARNS = "UPDATE {warns-table} SET active = 0 WHERE uuid = '%s' AND active = 1;";
    private final static String UPDATE_KICKS = "UPDATE {kicks-table} SET active = 0 WHERE uuid = '%s' AND active = 1;";

    /* INSERTION STATEMENTS */
    public static void insertIntoUsers(String uuid, String username, String ip, String language) {
        String statement = format(INSERT_INTO_USERS, uuid, username, ip, language);

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static PunishmentInfo insertIntoBans(String uuid, String user, String ip, String reason, String server, Boolean active, String executedby) {
        int activeNumber = active ? 1 : 0;
        PunishmentInfo info = null;

        String statement = format(INSERT_INTO_BANS, uuid, user, ip, reason, server, activeNumber, executedby);
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);

            info = new PunishmentInfo();
            info.setUuid(UUID.fromString(uuid));
            info.setUser(user);
            info.setIP(ip);
            info.setReason(reason);
            info.setServer(server);
            info.setActive(active);
            info.setExecutedBy(executedby);
            info.setDate(new Date(System.currentTimeMillis()));
            info.setType(PunishmentType.BAN);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static PunishmentInfo insertIntoIPBans(String uuid, String user, String ip, String reason, String server, Boolean active, String executedby) {
        int activeNumber = active ? 1 : 0;
        PunishmentInfo info = null;

        String statement = format(INSERT_INTO_IPBANS, uuid, user, ip, reason, server, activeNumber, executedby);
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);

            info = new PunishmentInfo();
            info.setUuid(UUID.fromString(uuid));
            info.setUser(user);
            info.setIP(ip);
            info.setReason(reason);
            info.setServer(server);
            info.setActive(active);
            info.setExecutedBy(executedby);
            info.setDate(new Date(System.currentTimeMillis()));
            info.setType(PunishmentType.IPBAN);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static PunishmentInfo insertIntoTempBans(String uuid, String user, String ip, Long time, String reason, String server, Boolean active, String executedby) {
        int activeNumber = active ? 1 : 0;
        PunishmentInfo info = null;

        String statement = format(INSERT_INTO_TEMPBANS, uuid, user, ip, time, reason, server, activeNumber, executedby);
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);

            info = new PunishmentInfo();
            info.setUuid(UUID.fromString(uuid));
            info.setUser(user);
            info.setIP(ip);
            info.setReason(reason);
            info.setServer(server);
            info.setActive(active);
            info.setExecutedBy(executedby);
            info.setDate(new Date(System.currentTimeMillis()));
            info.setExpireTime(time);
            info.setType(PunishmentType.TEMPBAN);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static PunishmentInfo insertIntoIPTempBans(String uuid, String user, String ip, Long time, String reason, String server, Boolean active, String executedby) {
        int activeNumber = active ? 1 : 0;
        PunishmentInfo info = null;

        String statement = format(INSERT_INTO_TEMPBANS, uuid, user, ip, time, reason, server, activeNumber, executedby);
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);

            info = new PunishmentInfo();
            info.setUuid(UUID.fromString(uuid));
            info.setUser(user);
            info.setIP(ip);
            info.setReason(reason);
            info.setServer(server);
            info.setActive(active);
            info.setExecutedBy(executedby);
            info.setDate(new Date(System.currentTimeMillis()));
            info.setExpireTime(time);
            info.setType(PunishmentType.IPTEMPBAN);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static PunishmentInfo insertIntoMutes(String uuid, String user, String ip, String reason, String server, Boolean active, String executedby) {
        int activeNumber = active ? 1 : 0;
        PunishmentInfo info = null;

        String statement = format(INSERT_INTO_MUTES, uuid, user, ip, reason, server, activeNumber, executedby);
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);

            info = new PunishmentInfo();
            info.setUuid(UUID.fromString(uuid));
            info.setUser(user);
            info.setIP(ip);
            info.setReason(reason);
            info.setServer(server);
            info.setActive(active);
            info.setExecutedBy(executedby);
            info.setDate(new Date(System.currentTimeMillis()));
            info.setType(PunishmentType.MUTE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static PunishmentInfo insertIntoIPMutes(String uuid, String user, String ip, String reason, String server, Boolean active, String executedby) {
        int activeNumber = active ? 1 : 0;
        PunishmentInfo info = null;

        String statement = format(INSERT_INTO_IPMUTES, uuid, user, ip, reason, server, activeNumber, executedby);
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);

            info = new PunishmentInfo();
            info.setUuid(UUID.fromString(uuid));
            info.setUser(user);
            info.setIP(ip);
            info.setReason(reason);
            info.setServer(server);
            info.setActive(active);
            info.setExecutedBy(executedby);
            info.setDate(new Date(System.currentTimeMillis()));
            info.setType(PunishmentType.IPMUTE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static PunishmentInfo insertIntoTempMutes(String uuid, String user, String ip, Long time, String reason, String server, Boolean active, String executedby) {
        int activeNumber = active ? 1 : 0;
        PunishmentInfo info = null;

        String statement = format(INSERT_INTO_TEMPMUTES, uuid, user, ip, time, reason, server, activeNumber, executedby);
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);

            info = new PunishmentInfo();
            info.setUuid(UUID.fromString(uuid));
            info.setUser(user);
            info.setIP(ip);
            info.setReason(reason);
            info.setServer(server);
            info.setActive(active);
            info.setExecutedBy(executedby);
            info.setDate(new Date(System.currentTimeMillis()));
            info.setExpireTime(time);
            info.setType(PunishmentType.TEMPMUTE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static PunishmentInfo insertIntoIPTempMutes(String uuid, String user, String ip, Long time, String reason, String server, Boolean active, String executedby) {
        int activeNumber = active ? 1 : 0;
        PunishmentInfo info = null;

        String statement = format(INSERT_INTO_TEMPMUTES, uuid, user, ip, time, reason, server, activeNumber, executedby);
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);

            info = new PunishmentInfo();
            info.setUuid(UUID.fromString(uuid));
            info.setUser(user);
            info.setIP(ip);
            info.setReason(reason);
            info.setServer(server);
            info.setActive(active);
            info.setExecutedBy(executedby);
            info.setDate(new Date(System.currentTimeMillis()));
            info.setExpireTime(time);
            info.setType(PunishmentType.IPTEMPMUTE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static PunishmentInfo insertIntoWarns(String uuid, String user, String ip, String reason, String server, String executedby) {
        PunishmentInfo info = null;

        String statement = format(INSERT_INTO_WARNS, uuid, user, ip, reason, server, executedby);
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);

            info = new PunishmentInfo();
            info.setUuid(UUID.fromString(uuid));
            info.setUser(user);
            info.setIP(ip);
            info.setReason(reason);
            info.setServer(server);
            info.setExecutedBy(executedby);
            info.setDate(new Date(System.currentTimeMillis()));
            info.setType(PunishmentType.WARN);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return info;
    }

    public static PunishmentInfo insertIntoKicks(String uuid, String user, String ip, String reason, String server, String executedby) {
        PunishmentInfo info = null;

        String statement = format(INSERT_INTO_KICKS, uuid, user, ip, reason, server, executedby);
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);

            info = new PunishmentInfo();
            info.setUuid(UUID.fromString(uuid));
            info.setUser(user);
            info.setIP(ip);
            info.setReason(reason);
            info.setServer(server);
            info.setExecutedBy(executedby);
            info.setDate(new Date(System.currentTimeMillis()));
            info.setType(PunishmentType.KICK);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return info;
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
        String statement = format(SELECT_FROM_USERS, "id", (name.contains(".") ? "ip = '" : "username = '") + name + "'");

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
        String statement = format(SELECT_FROM_USERS, "id", "uuid = '" + uuid + "'");

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            present = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return present;
    }

    public static boolean isBanPresent(UUID uuid, boolean checkActive) {
        boolean present = false;
        String statement = format(SELECT_FROM_BANS, "id", "uuid = '" + uuid + "'" + (checkActive ? " AND active = 1" : ""));

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
        String statement = format(SELECT_FROM_IPBANS, "id", "ip = '" + ip + "'" + (checkActive ? " AND active = 1" : ""));

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            present = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return present;
    }

    public static boolean isTempBanPresent(UUID uuid, boolean checkActive) {
        boolean present = false;
        String statement = format(SELECT_FROM_TEMPBANS, "id", "uuid = '" + uuid + "'" + (checkActive ? " AND active = 1" : ""));

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            present = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return present;
    }

    public static boolean isIPTempBanPresent(String IP, boolean checkActive) {
        boolean present = false;
        String statement = format(SELECT_FROM_IPTEMPBANS, "id", "ip = '" + IP + "'" + (checkActive ? " AND active = 1" : ""));

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            present = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return present;
    }

    public static boolean isMutePresent(UUID uuid, boolean checkActive) {
        boolean present = false;
        String statement = format(SELECT_FROM_MUTES, "id", "uuid = '" + uuid + "'" + (checkActive ? " AND active = 1" : ""));

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            present = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return present;
    }

    public static boolean isIPMutePresent(String ip, boolean checkActive) {
        boolean present = false;
        String statement = format(SELECT_FROM_IPMUTES, "id", "ip = '" + ip + "'" + (checkActive ? " AND active = 1" : ""));

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            present = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return present;
    }

    public static boolean isTempMutePresent(UUID uuid, boolean checkActive) {
        boolean present = false;
        String statement = format(SELECT_FROM_TEMPMUTES, "id", "uuid = '" + uuid + "'" + (checkActive ? " AND active = 1" : ""));

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            present = resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return present;
    }

    public static boolean isIPTempMutePresent(String IP, boolean checkActive) {
        boolean present = false;
        String statement = format(SELECT_FROM_IPTEMPMUTES, "id", "ip = '" + IP + "'" + (checkActive ? " AND active = 1" : ""));

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
        String statement = format(SELECT_FROM_USERS, "*", "uuid = '" + uuid + "'");

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
        String statement = format(SELECT_FROM_USERS, "*", (name.contains(".") ? "ip = '" : "username = '") + name + "'");

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

    public static PunishmentInfo getBan(UUID uuid) {
        PunishmentInfo info = new PunishmentInfo();
        info.setType(PunishmentType.BAN);

        String statement = format(SELECT_FROM_BANS, "*", "uuid = '" + uuid + "' AND active = 1");
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            if (resultSet.next()) {
                info.setId(resultSet.getInt("id"));
                info.setUuid(UUID.fromString(resultSet.getString("uuid")));
                info.setUser(resultSet.getString("user"));
                info.setIP(resultSet.getString("ip"));
                info.setReason(resultSet.getString("reason"));
                info.setServer(resultSet.getString("server"));
                info.setDate(resultSet.getTimestamp("date"));
                info.setActive(resultSet.getBoolean("active"));
                info.setExecutedBy(resultSet.getString("executed_by"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return info;
    }

    public static PunishmentInfo getIPBan(String IP) {
        PunishmentInfo info = new PunishmentInfo();
        info.setType(PunishmentType.IPBAN);

        String statement = format(SELECT_FROM_IPBANS, "*", "ip = '" + IP + "' AND active = 1");
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            if (resultSet.next()) {
                info.setId(resultSet.getInt("id"));
                info.setUuid(UUID.fromString(resultSet.getString("uuid")));
                info.setUser(resultSet.getString("user"));
                info.setIP(resultSet.getString("ip"));
                info.setReason(resultSet.getString("reason"));
                info.setServer(resultSet.getString("server"));
                info.setDate(resultSet.getTimestamp("date"));
                info.setActive(resultSet.getBoolean("active"));
                info.setExecutedBy(resultSet.getString("executed_by"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return info;
    }

    public static PunishmentInfo getTempBan(UUID uuid) {
        PunishmentInfo info = new PunishmentInfo();
        info.setType(PunishmentType.TEMPBAN);

        String statement = format(SELECT_FROM_TEMPBANS, "*", "uuid = '" + uuid + "' AND active = 1");
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            if (resultSet.next()) {
                info.setId(resultSet.getInt("id"));
                info.setUuid(UUID.fromString(resultSet.getString("uuid")));
                info.setUser(resultSet.getString("user"));
                info.setIP(resultSet.getString("ip"));
                info.setExpireTime(resultSet.getLong("time"));
                info.setReason(resultSet.getString("reason"));
                info.setServer(resultSet.getString("server"));
                info.setDate(resultSet.getTimestamp("date"));
                info.setActive(resultSet.getBoolean("active"));
                info.setExecutedBy(resultSet.getString("executed_by"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return info;
    }

    public static PunishmentInfo getIPTempBan(String IP) {
        PunishmentInfo info = new PunishmentInfo();
        info.setType(PunishmentType.IPTEMPBAN);

        String statement = format(SELECT_FROM_IPTEMPBANS, "*", "ip = '" + IP + "' AND active = 1");
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            if (resultSet.next()) {
                info.setId(resultSet.getInt("id"));
                info.setUuid(UUID.fromString(resultSet.getString("uuid")));
                info.setUser(resultSet.getString("user"));
                info.setIP(resultSet.getString("ip"));
                info.setExpireTime(resultSet.getLong("time"));
                info.setReason(resultSet.getString("reason"));
                info.setServer(resultSet.getString("server"));
                info.setDate(resultSet.getTimestamp("date"));
                info.setActive(resultSet.getBoolean("active"));
                info.setExecutedBy(resultSet.getString("executed_by"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return info;
    }

    public static PunishmentInfo getMute(UUID uuid) {
        PunishmentInfo info = new PunishmentInfo();
        info.setType(PunishmentType.MUTE);

        String statement = format(SELECT_FROM_MUTES, "*", "uuid = '" + uuid + "' AND active = 1");
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            if (resultSet.next()) {
                info.setId(resultSet.getInt("id"));
                info.setUuid(UUID.fromString(resultSet.getString("uuid")));
                info.setUser(resultSet.getString("user"));
                info.setIP(resultSet.getString("ip"));
                info.setReason(resultSet.getString("reason"));
                info.setServer(resultSet.getString("server"));
                info.setDate(resultSet.getTimestamp("date"));
                info.setActive(resultSet.getBoolean("active"));
                info.setExecutedBy(resultSet.getString("executed_by"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return info;
    }

    public static PunishmentInfo getIPMute(String IP) {
        PunishmentInfo info = new PunishmentInfo();
        info.setType(PunishmentType.IPMUTE);

        String statement = format(SELECT_FROM_IPMUTES, "*", "ip = '" + IP + "' AND active = 1");
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            if (resultSet.next()) {
                info.setId(resultSet.getInt("id"));
                info.setUuid(UUID.fromString(resultSet.getString("uuid")));
                info.setUser(resultSet.getString("user"));
                info.setIP(resultSet.getString("ip"));
                info.setReason(resultSet.getString("reason"));
                info.setServer(resultSet.getString("server"));
                info.setDate(resultSet.getTimestamp("date"));
                info.setActive(resultSet.getBoolean("active"));
                info.setExecutedBy(resultSet.getString("executed_by"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return info;
    }

    public static PunishmentInfo getTempMute(UUID uuid) {
        PunishmentInfo info = new PunishmentInfo();
        info.setType(PunishmentType.TEMPMUTE);

        String statement = format(SELECT_FROM_TEMPMUTES, "*", "uuid = '" + uuid + "' AND active = 1");
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            if (resultSet.next()) {
                info.setId(resultSet.getInt("id"));
                info.setUuid(UUID.fromString(resultSet.getString("uuid")));
                info.setUser(resultSet.getString("user"));
                info.setIP(resultSet.getString("ip"));
                info.setExpireTime(resultSet.getLong("time"));
                info.setReason(resultSet.getString("reason"));
                info.setServer(resultSet.getString("server"));
                info.setDate(resultSet.getTimestamp("date"));
                info.setActive(resultSet.getBoolean("active"));
                info.setExecutedBy(resultSet.getString("executed_by"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return info;
    }

    public static PunishmentInfo getIPTempMute(String IP) {
        PunishmentInfo info = new PunishmentInfo();
        info.setType(PunishmentType.IPTEMPMUTE);

        String statement = format(SELECT_FROM_IPTEMPMUTES, "*", "ip = '" + IP + "' AND active = 1");
        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            ResultSet resultSet = connection.createStatement().executeQuery(statement);

            if (resultSet.next()) {
                info.setId(resultSet.getInt("id"));
                info.setUuid(UUID.fromString(resultSet.getString("uuid")));
                info.setUser(resultSet.getString("user"));
                info.setIP(resultSet.getString("ip"));
                info.setExpireTime(resultSet.getLong("time"));
                info.setReason(resultSet.getString("reason"));
                info.setServer(resultSet.getString("server"));
                info.setDate(resultSet.getTimestamp("date"));
                info.setActive(resultSet.getBoolean("active"));
                info.setExecutedBy(resultSet.getString("executed_by"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return info;
    }

    public static void removeBan(UUID uuid) {
        String statement = format(UPDATE_BANS, uuid.toString());

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeIPBan(String IP) {
        String statement = format(UPDATE_IPBANS, IP);

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeTempBan(UUID uuid) {
        String statement = format(UPDATE_TEMPBANS, uuid.toString());

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeIPTempBan(String IP) {
        String statement = format(UPDATE_TEMPBANS, IP);

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeMute(UUID uuid) {
        String statement = format(UPDATE_MUTES, uuid.toString());

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeIPMute(String IP) {
        String statement = format(UPDATE_IPMUTES, IP);

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeTempMute(UUID uuid) {
        String statement = format(UPDATE_TEMPMUTES, uuid.toString());

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void removeIPTempMute(String IP) {
        String statement = format(UPDATE_TEMPMUTES, IP);

        try (Connection connection = BungeeUtilisals.getInstance().getDatabaseManagement().getConnection()) {
            connection.createStatement().executeUpdate(statement);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /* UTILITY METHODS */
    private static Language getLanguageOrDefault(String language) {
        return BUCore.getApi().getLanguageManager().getLanguage(language).orElse(BUCore.getApi().getLanguageManager().getDefaultLanguage());
    }

    private static String format(String line, Object... replacements) {
        return String.format(PlaceHolderAPI.formatMessage(line), replacements);
    }
}