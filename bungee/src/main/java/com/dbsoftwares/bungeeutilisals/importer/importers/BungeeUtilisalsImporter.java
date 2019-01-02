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
import com.google.common.collect.Lists;

import java.sql.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class BungeeUtilisalsImporter extends Importer {

    private static final String ERROR_STRING = "An error occured: ";

    private Connection createConnection(final Map<String, String> properties) throws SQLException {
        if (properties.isEmpty()) {
            return BUCore.getApi().getStorageManager().getConnection();
        }
        return DriverManager.getConnection(
                "jdbc:mysql://" + properties.get("host") + ":"
                        + properties.get("port")
                        + "/" + properties.get("database"),
                properties.get("username"),
                properties.get("password")
        );
    }

    @Override
    protected void importData(final ImporterCallback<ImporterStatus> importerCallback, final Map<String, String> properties) {
        try (Connection connection = createConnection(properties); Statement stmt = connection.createStatement()) {
            final DatabaseMetaData databaseMetaData = connection.getMetaData();
            for (final String table : Lists.newArrayList("PlayerInfo", "Bans", "IPBans", "Mutes")) {
                final ResultSet tables = databaseMetaData.getTables(null, null, table, null);
                if (!tables.next()) {
                    throw new IllegalArgumentException("Could not find table " + table + ", stopping importer ...");
                }
            }

            try (ResultSet counter = stmt.executeQuery(
                    "SELECT (SELECT COUNT(*) FROM Bans) bans," +
                            " (SELECT COUNT(*) FROM IPBans) ipbans," +
                            " (SELECT COUNT(*) FROM Mutes) mutes," +
                            " (SELECT COUNT(*) FROM PlayerInfo) players;"
            )) {
                if (counter.next()) {
                    status = new ImporterStatus(
                            counter.getInt("bans") + counter.getInt("ipbans") + counter.getInt("mutes") + counter.getInt("players")
                    );
                }
            }

            try (final ResultSet rs = stmt.executeQuery(
                    "SELECT BannedBy executed_by, Banned user, BanTime time, Reason reason FROM Bans;"
            )) {
                while (rs.next()) {
                    try {
                        importPunishment(PunishmentType.BAN, connection, rs);
                    } catch (Exception e) {
                        BUCore.getLogger().error(ERROR_STRING, e);
                        continue;
                    }

                    status.incrementConvertedEntries(1);
                    importerCallback.onStatusUpdate(status);
                }
            }
            try (final ResultSet rs = stmt.executeQuery(
                    "SELECT MutedBy executed_by, Muted user, MuteTime time, Reason reason FROM Mutes;"
            )) {
                while (rs.next()) {
                    try {
                        importPunishment(PunishmentType.MUTE, connection, rs);
                    } catch (Exception e) {
                        BUCore.getLogger().error(ERROR_STRING, e);
                        continue;
                    }

                    status.incrementConvertedEntries(1);
                    importerCallback.onStatusUpdate(status);
                }
            }
            try (final ResultSet rs = stmt.executeQuery(
                    "SELECT BannedBy executed_by, Banned user, '-1' time, Reason reason FROM IPBans;"
            )) {
                while (rs.next()) {
                    try {
                        importPunishment(PunishmentType.IPBAN, connection, rs);
                    } catch (Exception e) {
                        BUCore.getLogger().error(ERROR_STRING, e);
                        continue;
                    }

                    status.incrementConvertedEntries(1);
                    importerCallback.onStatusUpdate(status);
                }
            }

            try (final ResultSet rs = stmt.executeQuery(
                    "SELECT Player, IP FROM PlayerInfo;"
            )) {
                while (rs.next()) {
                    String user = rs.getString("Player");
                    String IP = rs.getString("IP");

                    boolean usingUUID = user.contains("-");

                    String uuid = usingUUID ? user : null;
                    String name = usingUUID ? null : user;

                    try {
                        if (uuid == null) {
                            uuid = uuidCache.get(name);
                        } else {
                            name = nameCache.get(uuid);
                        }
                    } catch (Exception e) {
                        BUCore.getLogger().error(ERROR_STRING, e);
                        continue;
                    }

                    BUCore.getApi().getStorageManager().getDao().getUserDao().createUser(
                            readUUIDFromString(uuid),
                            name,
                            IP,
                            BUCore.getApi().getLanguageManager().getDefaultLanguage()
                    );

                    status.incrementConvertedEntries(1);
                    importerCallback.onStatusUpdate(status);
                }
            }

            importerCallback.done(status, null);
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
    }

    private void importPunishment(final PunishmentType type, final Connection connection, final ResultSet rs) throws ExecutionException, SQLException {
        final String executedBy = rs.getString("executed_by");
        final String user = rs.getString("user");
        final long time = rs.getLong("time");
        final String reason = rs.getString("reason");

        final String id = user.contains(".") ? getIdOnIP(connection, user) : user;

        if (id == null) {
            return;
        }

        final boolean usingUUID = id.contains("-");
        String uuid = usingUUID ? id : null;
        String name = usingUUID ? null : id;

        if (uuid == null) {
            uuid = uuidCache.get(name);
        } else {
            name = nameCache.get(uuid);
        }
        final String IP = user.contains(".") ? user : getIP(connection, user);

        final PunishmentType punishmentType;
        if (type.equals(PunishmentType.BAN)) {
            punishmentType = time == -1 ? PunishmentType.BAN : PunishmentType.TEMPBAN;
        } else if (type.equals(PunishmentType.MUTE)) {
            punishmentType = time == -1 ? PunishmentType.MUTE : PunishmentType.TEMPMUTE;
        } else {
            punishmentType = type;
        }

        BUCore.getApi().getStorageManager().getDao().getPunishmentDao().insertPunishment(
                punishmentType,
                UUID.fromString(uuid),
                name,
                IP,
                reason,
                time,
                "UNKNOWN",
                true,
                executedBy
        );
    }

    private String getIP(final Connection connection, final String id) throws SQLException {
        String IP = null;
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT IP FROM PlayerInfo WHERE Player = ?")) {
            pstmt.setString(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    IP = rs.getString("IP");
                }
            }
        }

        return IP;
    }

    private String getIdOnIP(final Connection connection, final String ip) throws SQLException {
        String id = null;
        try (PreparedStatement pstmt = connection.prepareStatement("SELECT Player FROM PlayerInfo WHERE IP = ?")) {
            pstmt.setString(1, ip);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    id = rs.getString("Player");
                }
            }
        }

        return id;
    }
}