package com.dbsoftwares.bungeeutilisals.importer.importers;

import com.dbsoftwares.bungeeutilisals.importer.Importer;
import com.dbsoftwares.bungeeutilisals.importer.ImporterCallback;
import com.dbsoftwares.bungeeutilisals.library.Library;
import com.google.common.collect.Lists;

import java.sql.*;
import java.util.Map;

public class BungeeAdminToolsImporter extends Importer {

    private Connection createConnection(final Map<String, String> properties) throws SQLException {
        String type = properties.getOrDefault("type", "mysql");

        if (type.equalsIgnoreCase("mysql")) {
            return DriverManager.getConnection(
                    "jdbc:mysql://" + properties.get("host") + ":"
                            + properties.get("port")
                            + "/" + properties.get("database"),
                    properties.get("username"),
                    properties.get("password")
            );
        } else {
            if (!Library.SQLITE.isPresent()) {
                Library.SQLITE.load();
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
            for (final String table : Lists.newArrayList("PlayerInfo", "Bans", "IPBans", "Mutes")) { // TODO
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


            importerCallback.done(status, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}