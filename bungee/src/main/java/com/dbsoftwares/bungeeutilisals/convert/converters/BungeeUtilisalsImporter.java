package com.dbsoftwares.bungeeutilisals.convert.converters;

import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.convert.Importer;
import com.dbsoftwares.bungeeutilisals.convert.ImporterType;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class BungeeUtilisalsImporter extends Importer {

    private HikariDataSource dataSource;

    public BungeeUtilisalsImporter(final Map<String, String> properties) {
        super(ImporterType.BUNGEEUTILISALS, properties);

        dataSource = new HikariDataSource();
        dataSource.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        dataSource.addDataSourceProperty("serverName", properties.get("host"));
        dataSource.addDataSourceProperty("port", properties.get("port"));
        dataSource.addDataSourceProperty("databaseName", properties.get("database"));
        dataSource.addDataSourceProperty("user", properties.get("username"));
        dataSource.addDataSourceProperty("password", properties.get("password"));

        dataSource.setMaximumPoolSize(3);
        dataSource.setPoolName("BungeeUtilisals");
    }

    @Override
    public void startConverter() {
        // TODO: import
        try (Connection connection = dataSource.getConnection();
             PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM Bans;")) {

            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}