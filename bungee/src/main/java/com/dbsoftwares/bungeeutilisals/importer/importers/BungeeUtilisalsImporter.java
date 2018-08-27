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

    private Connection createConnection(final Map<String, String> properties) throws SQLException {
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

            final ResultSet counter = stmt.executeQuery(
                    "SELECT (SELECT COUNT(*) FROM Bans) bans," +
                            " (SELECT COUNT(*) FROM IPBans) ipbans," +
                            " (SELECT COUNT(*) FROM Mutes) mutes," +
                            " (SELECT COUNT(*) FROM PlayerInfo) players;");

            status = new ImporterStatus(
                    counter.getInt("bans") + counter.getInt("ipbans") + counter.getInt("mutes") + counter.getInt("players")
            );

            try (ResultSet rs = stmt.executeQuery(
                    "SELECT BannedBy executed_by, Banned user, BanTime time, Reason reason FROM Bans " +
                            "UNION SELECT MutedBy executed_by, Muted user, MuteTime time, Reason reason FROM Mutes " +
                            "UNION SELECT BannedBy executed_by, Banned user, '-1' time, Reason reason FROM IPBans;"
            )) {
                while (rs.next()) {
                    String table = rs.getMetaData().getTableName(0);
                    String executedBy = rs.getString("executed_by");
                    String user = rs.getString("user");
                    Long time = rs.getLong("time");
                    String reason = rs.getString("reason");

                    String id = user.contains(".") ? getIdOnIP(connection, user) : user;

                    boolean usingUUID = id.contains("-");

                    String uuid = usingUUID ? user : null;
                    String name = usingUUID ? null : user;
                    try {
                        if (uuid == null) {
                            uuid = uuidCache.get(name);
                        } else {
                            name = nameCache.get(name);
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        continue;
                    }
                    String IP = user.contains(".") ? user : getIP(connection, user);

                    if (table.equalsIgnoreCase("Bans")) {
                        BUCore.getApi().getStorageManager().getDao().getPunishmentDao().insertPunishment(
                                time == -1 ? PunishmentType.BAN : PunishmentType.TEMPBAN,
                                UUID.fromString(uuid),
                                name,
                                IP,
                                reason,
                                time,
                                "UNKNOWN",
                                true,
                                executedBy
                        );
                    } else if (table.equalsIgnoreCase("IPBans")) {
                        BUCore.getApi().getStorageManager().getDao().getPunishmentDao().insertPunishment(
                                PunishmentType.IPBAN,
                                UUID.fromString(uuid),
                                name,
                                IP,
                                reason,
                                time,
                                "UNKNOWN",
                                true,
                                executedBy
                        );
                    } else {
                        BUCore.getApi().getStorageManager().getDao().getPunishmentDao().insertPunishment(
                                time == -1 ? PunishmentType.MUTE : PunishmentType.TEMPMUTE,
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
                    status.incrementConvertedEntries(1);
                    importerCallback.onStatusUpdate(status);
                }
            }

            try (ResultSet rs = stmt.executeQuery(
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
                            name = nameCache.get(name);
                        }
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                        continue;
                    }

                    BUCore.getApi().getStorageManager().getDao().getUserDao().createUser(
                            UUID.fromString(uuid),
                            name,
                            IP,
                            BUCore.getApi().getLanguageManager().getDefaultLanguage()
                    );

                    status.incrementConvertedEntries(1);
                    importerCallback.onStatusUpdate(status);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
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