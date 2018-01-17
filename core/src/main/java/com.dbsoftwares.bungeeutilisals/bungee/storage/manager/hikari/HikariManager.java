package com.dbsoftwares.bungeeutilisals.bungee.storage.manager.hikari;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.configuration.yaml.YamlConfiguration;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractManager;
import com.dbsoftwares.bungeeutilisals.api.storage.StorageType;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.api.plugin.Plugin;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class HikariManager extends AbstractManager {

    protected HikariConfig config;
    protected HikariDataSource dataSource;

    @SuppressWarnings("deprecation")
    public HikariManager(Plugin plugin, StorageType type, YamlConfiguration configuration) {
        super(plugin, type);
        config = new HikariConfig();
        config.setDataSourceClassName(getDataSourceClass());
        config.addDataSourceProperty("serverName", configuration.getString("storage.hostname"));
        config.addDataSourceProperty("port", configuration.getInteger("storage.port"));
        config.addDataSourceProperty("databaseName", configuration.getString("storage.database"));
        config.setUsername(configuration.getString("storage.username"));
        config.setPassword(configuration.getString("storage.password"));

        config.setMaximumPoolSize(configuration.getInteger("storage.pool.max-pool-size"));
        config.setMinimumIdle(configuration.getInteger("storage.pool.min-idle"));
        config.setMaxLifetime(configuration.getInteger("storage.pool.max-lifetime") * 1000);
        config.setConnectionTimeout(configuration.getInteger("storage.pool.connection-timeout") * 1000);

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
        config.setLeakDetectionThreshold(10000);
        config.setConnectionTestQuery("/* BungeeUtilisals ping */ SELECT 1;");

        try {
            config.setInitializationFailTimeout(-1);
        } catch (NoSuchMethodError e) {
            config.setInitializationFailFast(false);
        }
        dataSource = new HikariDataSource(config);
    }

    protected abstract String getDataSourceClass();

    @Override
    public void close() {
        dataSource.close();
    }
}
