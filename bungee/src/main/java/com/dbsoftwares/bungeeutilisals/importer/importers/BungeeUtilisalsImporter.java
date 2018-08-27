package com.dbsoftwares.bungeeutilisals.importer.importers;

import com.dbsoftwares.bungeeutilisals.importer.Importer;
import com.dbsoftwares.bungeeutilisals.importer.ImporterCallback;
import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.*;
import java.util.Map;

public class BungeeUtilisalsImporter extends Importer {

    private HikariDataSource dataSource;

    @Override
    protected void importData(final ImporterCallback<ImporterStatus> importerCallback, final Map<String, String> properties) {
        // TODO: setup datasource using property map | search tables & run through them.
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);

            final DatabaseMetaData databaseMetaData = connection.getMetaData();
            for (final String table : Lists.newArrayList("PlayerInfo", "Bans", "IPBans", "Mutes")) {
                final ResultSet tables = databaseMetaData.getTables(null, null, table, null);
                if (!tables.next()) {
                    throw new IllegalArgumentException("Could not find table " + table + ", stopping import ...");
                }
            }

            status = new ImporterStatus(0 /* Make counter for entries in all tables together */);

            // TODO: Import | MAKE METHOD FOR EACH TABLE (Bans, Mutes, IPBans)
            int uncommited = 0;
            try (PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM Bans;");
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {

                    uncommited++;

                    if (uncommited % 100 == 0) {
                        uncommited = 0;
                        connection.commit();
                        status.incrementConvertedEntries(uncommited);
                        importerCallback.onStatusUpdate(status);
                    }
                }
            }

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}