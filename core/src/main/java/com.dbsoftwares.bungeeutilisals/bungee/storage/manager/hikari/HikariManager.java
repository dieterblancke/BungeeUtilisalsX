package com.dbsoftwares.bungeeutilisals.bungee.storage.manager.hikari;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.bungee.storage.manager.AbstractManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Connection;
import java.sql.SQLException;

@EqualsAndHashCode(callSuper = true)
@Data
public class HikariManager extends AbstractManager {

    protected HikariConfig config;
    protected HikariDataSource dataSource;

    protected void addConfigDefaults() {
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("alwaysSendSetIsolation", "false");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("cacheCallableStmts", "true");

        config.setPoolName("BungeeUtilisals");
        config.setMinimumIdle(2);
        config.setIdleTimeout(10000);
        config.setConnectionTimeout(5000);
        config.setLeakDetectionThreshold(10000);

        config.setConnectionTestQuery("/* BungeeUtilisals ping */ SELECT 1;");

        try {
            config.setInitializationFailTimeout(-1);
        } catch (NoSuchMethodError e) {
            config.setInitializationFailFast(false);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void close() {
        dataSource.close();
    }
}
