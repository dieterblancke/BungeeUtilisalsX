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

            if (counter.next()) {
                status = new ImporterStatus(
                        counter.getInt("bans") + counter.getInt("ipbans") + counter.getInt("mutes") + counter.getInt("players")
                );
            }

            try (ResultSet rs = stmt.executeQuery(
                    "SELECT BannedBy executed_by, Banned user, BanTime time, Reason reason FROM Bans;"
            )) {
                while (rs.next()) {
                    try {
                        importPunishment(PunishmentType.BAN, connection, rs);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }

                    status.incrementConvertedEntries(1);
                    importerCallback.onStatusUpdate(status);
                }
            }
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT MutedBy executed_by, Muted user, MuteTime time, Reason reason FROM Mutes;"
            )) {
                while (rs.next()) {
                    try {
                        importPunishment(PunishmentType.MUTE, connection, rs);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }

                    status.incrementConvertedEntries(1);
                    importerCallback.onStatusUpdate(status);
                }
            }
            try (ResultSet rs = stmt.executeQuery(
                    "SELECT BannedBy executed_by, Banned user, '-1' time, Reason reason FROM IPBans;"
            )) {
                int i = 0;
                while (rs.next()) {
                    i++;

                    if (i >= 10) { // TODO: remove
                        break;
                    }
                    try {
                        importPunishment(PunishmentType.IPBAN, connection, rs);
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
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
                            name = nameCache.get(uuid);
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

            importerCallback.done(status, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void importPunishment(final PunishmentType type, final Connection connection, final ResultSet rs) throws ExecutionException, SQLException {
        final String executedBy = rs.getString("executed_by");
        final String user = rs.getString("user");
        final Long time = rs.getLong("time");
        final String reason = rs.getString("reason");

        final String id = user.contains(".") ? getIdOnIP(connection, user) : user;

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

        System.out.println(type + " " + executedBy + " " + user + " " + time + " " + reason
                + " " + id + " " + usingUUID + " " + uuid + " " + name + " " + IP);

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