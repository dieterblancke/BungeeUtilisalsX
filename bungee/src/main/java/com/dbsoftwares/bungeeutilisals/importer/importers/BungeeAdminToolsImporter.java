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

package com.dbsoftwares.bungeeutilisals.importer.importers;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.importer.Importer;
import com.dbsoftwares.bungeeutilisals.importer.ImporterCallback;
import com.dbsoftwares.bungeeutilisals.library.Library;
import com.dbsoftwares.bungeeutilisals.library.StandardLibrary;
import com.google.common.collect.Lists;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

public class BungeeAdminToolsImporter extends Importer {

    private Connection createConnection(final Map<String, String> properties) throws SQLException {
        String type = properties.getOrDefault("type", "mysql");

        if (properties.isEmpty()) {
            return BUCore.getApi().getStorageManager().getConnection();
        }

        if (type.equalsIgnoreCase("mysql")) {
            return DriverManager.getConnection(
                    "jdbc:mysql://" + properties.get("host") + ":"
                            + properties.get("port")
                            + "/" + properties.get("database"),
                    properties.get("username"),
                    properties.get("password")
            );
        } else {
            final Library sqlite = StandardLibrary.SQLITE.getLibrary();
            if (!sqlite.isPresent()) {
                sqlite.load();
            }
            return DriverManager.getConnection(
                    "jdbc:sqlite:" + properties.getOrDefault("path", "plugins/BungeeAdminTools/bat_database.db")
            );
        }
    }

    @Override
    protected void importData(final ImporterCallback<ImporterStatus> importerCallback, final Map<String, String> properties) {
        try (Connection connection = createConnection(properties); Statement stmt = connection.createStatement()) {
            final DatabaseMetaData databaseMetaData = connection.getMetaData();
            for (final String table : Lists.newArrayList("BAT_ban", "BAT_mute", "BAT_players")) {
                final ResultSet tables = databaseMetaData.getTables(null, null, table, null);
                if (!tables.next()) {
                    throw new IllegalArgumentException("Could not find table " + table + ", stopping importer ...");
                }
            }

            try (ResultSet counter = stmt.executeQuery(
                    "SELECT (SELECT COUNT(*) FROM BAT_ban) bans," +
                            " (SELECT COUNT(*) FROM BAT_mute) mutes," +
                            " (SELECT COUNT(*) FROM BAT_players) players;"
            )) {
                if (counter.next()) {
                    status = new ImporterStatus(
                            counter.getInt("bans") + counter.getInt("mutes") + counter.getInt("players")
                    );
                }
            }

            try (final ResultSet rs = stmt.executeQuery(
                    "SELECT * FROM BAT_ban;"
            )) {
                while (rs.next()) {
                    UUID uuid = readUUIDFromString(rs.getString("UUID"));
                    String ip = rs.getString("ban_ip");
                    String executedBy = rs.getString("ban_staff");
                    String reason = rs.getString("ban_reason");
                    boolean active = rs.getBoolean("ban_state");
                    long time = rs.getLong("ban_end");
                    String removedBy = rs.getString("ban_unbanstaff");

                    if (reason == null) {
                        reason = "";
                    }

                    PunishmentType type;
                    if (time == 0) {
                        if (ip == null) {
                            type = PunishmentType.BAN;
                        } else {
                            type = PunishmentType.IPBAN;
                        }
                    } else {
                        if (ip == null) {
                            type = PunishmentType.TEMPBAN;
                        } else {
                            type = PunishmentType.IPTEMPBAN;
                        }
                    }
                    String name = getName(connection, uuid);

                    if (ip == null) {
                        ip = getIP(connection, uuid);
                    }
                    BUCore.getApi().getStorageManager().getDao().getPunishmentDao().insertPunishment(
                            type,
                            uuid,
                            name,
                            ip,
                            reason,
                            time,
                            "UNKNOWN",
                            active,
                            executedBy,
                            removedBy
                    );

                    status.incrementConvertedEntries(1);
                    importerCallback.onStatusUpdate(status);
                }
            }

            try (final ResultSet rs = stmt.executeQuery(
                    "SELECT * FROM BAT_mute;"
            )) {
                while (rs.next()) {
                    UUID uuid = readUUIDFromString(rs.getString("UUID"));
                    String ip = rs.getString("mute_ip");
                    String executedBy = rs.getString("mute_staff");
                    String reason = rs.getString("mute_reason");
                    boolean active = rs.getBoolean("mute_state");
                    long time = rs.getLong("mute_end");
                    String removedBy = rs.getString("mute_unmutestaff");

                    if (reason == null) {
                        reason = "";
                    }

                    PunishmentType type;
                    if (time == 0) {
                        if (ip == null) {
                            type = PunishmentType.MUTE;
                        } else {
                            type = PunishmentType.IPMUTE;
                        }
                    } else {
                        if (ip == null) {
                            type = PunishmentType.TEMPMUTE;
                        } else {
                            type = PunishmentType.IPTEMPMUTE;
                        }
                    }
                    String name = getName(connection, uuid);

                    if (ip == null) {
                        ip = getIP(connection, uuid);
                    }

                    BUCore.getApi().getStorageManager().getDao().getPunishmentDao().insertPunishment(
                            type,
                            uuid,
                            name,
                            ip,
                            reason,
                            time,
                            "UNKNOWN",
                            active,
                            executedBy,
                            removedBy
                    );

                    status.incrementConvertedEntries(1);
                    importerCallback.onStatusUpdate(status);
                }
            }

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try (final ResultSet rs = stmt.executeQuery(
                    "SELECT * FROM BAT_players;"
            )) {
                while (rs.next()) {
                    String name = rs.getString("BAT_player");
                    UUID uuid = readUUIDFromString(rs.getString("UUID"));
                    String ip = rs.getString("lastip");
                    Date login;
                    Date logout;
                    try {
                        login = formatter.parse(rs.getString("firstlogin"));
                        logout = formatter.parse(rs.getString("lastlogin")); // not lastlogout but close
                    } catch (ParseException e) {
                        login = logout = new Date(System.currentTimeMillis());
                    }

                    BUCore.getApi().getStorageManager().getDao().getUserDao().createUser(
                            uuid,
                            name,
                            ip,
                            BUCore.getApi().getLanguageManager().getDefaultLanguage(),
                            login,
                            logout
                    );

                    status.incrementConvertedEntries(1);
                    importerCallback.onStatusUpdate(status);
                }
            }

            importerCallback.done(status, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String getName(Connection connection, UUID uuid) throws SQLException {
        String name = null;
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT BAT_player FROM BAT_players WHERE UUID = ?;")) {
            pstmt.setString(1, uuid.toString().replace("-", ""));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("BAT_player");
                }
            }
        }

        return name;
    }

    private String getIP(Connection connection, UUID uuid) throws SQLException {
        String name = null;
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT lastip FROM BAT_players WHERE UUID = ?;")) {
            pstmt.setString(1, uuid.toString().replace("-", ""));

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    name = rs.getString("lastip");
                }
            }
        }

        return name;
    }
}