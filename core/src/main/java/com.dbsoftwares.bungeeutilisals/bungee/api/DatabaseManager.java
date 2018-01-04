package com.dbsoftwares.bungeeutilisals.bungee.api;

/*
 * Created by DBSoftwares on 04/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.configuration.yaml.YamlConfiguration;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocations;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseManager extends HikariDataSource {

    public DatabaseManager(BUAPI api) {
        YamlConfiguration configuration = api.getConfig(FileLocations.MYSQL);

        setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        addDataSourceProperty("serverName", configuration.getString("hostname"));
        addDataSourceProperty("port", configuration.getInteger("port"));
        addDataSourceProperty("databaseName", configuration.getString("database"));
        addDataSourceProperty("user", configuration.getString("username"));
        addDataSourceProperty("password", configuration.getString("password"));

        addDataSourceProperty("cachePrepStmts", "true");
        addDataSourceProperty("alwaysSendSetIsolation", "false");
        addDataSourceProperty("cacheServerConfiguration", "true");
        addDataSourceProperty("elideSetAutoCommits", "true");
        addDataSourceProperty("useLocalSessionState", "true");
        addDataSourceProperty("useServerPrepStmts", "true");
        addDataSourceProperty("prepStmtCacheSize", "250");
        addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        addDataSourceProperty("cacheCallableStmts", "true");

        setPoolName("BungeeUtilisals");
        setMaximumPoolSize(configuration.getInteger("max-pool-size"));
        setMinimumIdle(2);
        setIdleTimeout(10000);
        setConnectionTimeout(5000);
        setLeakDetectionThreshold(10000);

        setConnectionTestQuery("/* BungeeUtilisals ping */ SELECT 1;");

        try {
            // don't perform any initial connection validation - we subsequently call #getConnection
            // to setup the schema anyways
            setInitializationFailTimeout(-1);
        } catch (NoSuchMethodError e) {
            // noinspection deprecation
            setInitializationFailFast(false);
        }

        // TODO: Default table creation.
    }
}