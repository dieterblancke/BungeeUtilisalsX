package com.dbsoftwares.bungeeutilisals.storage.hikari;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.zaxxer.hikari.HikariConfig;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;

public class MariaDBStorageManager extends HikariStorageManager {

    public MariaDBStorageManager(Plugin plugin) {
        super(plugin, StorageType.MARIADB, BungeeUtilisals.getInstance().getConfig(), getProperties());
    }

    private static HikariConfig getProperties() {
        HikariConfig config = new HikariConfig();
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("alwaysSendSetIsolation", "false");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("cacheCallableStmts", "true");
        return config;
    }

    @Override
    protected String getDataSourceClass() {
        return Utils.classFound("org.mariadb.jdbc.MariaDbDataSource") ? "org.mariadb.jdbc.MariaDbDataSource"
                : "org.mariadb.jdbc.MySQLDataSource";
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}