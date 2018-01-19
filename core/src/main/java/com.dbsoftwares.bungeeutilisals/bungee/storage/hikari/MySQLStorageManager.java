package com.dbsoftwares.bungeeutilisals.bungee.storage.hikari;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.zaxxer.hikari.HikariConfig;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLStorageManager extends HikariStorageManager {

    public MySQLStorageManager(Plugin plugin) {
        super(plugin, StorageType.MYSQL, BungeeUtilisals.getInstance().getConfig(), getProperties());
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
        return "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}