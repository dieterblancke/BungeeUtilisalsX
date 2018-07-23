package com.dbsoftwares.bungeeutilisals.storage.hikari;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.storage.data.SQLDataManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.api.plugin.Plugin;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class HikariStorageManager extends AbstractStorageManager {

    protected HikariConfig config;
    protected HikariDataSource dataSource;

    @SuppressWarnings("deprecation")
    public HikariStorageManager(Plugin plugin, StorageType type, IConfiguration configuration, HikariConfig cfg) {
        super(plugin, type, new SQLDataManager());
        config = cfg == null ? new HikariConfig() : cfg;
        config.setDataSourceClassName(getDataSourceClass());
        config.addDataSourceProperty("serverName", configuration.getString("storage.hostname"));
        config.addDataSourceProperty("port" + (type.equals(StorageType.POSTGRESQL) ? "Number" : ""),
                configuration.getInteger("storage.port"));
        config.addDataSourceProperty("databaseName", configuration.getString("storage.database"));
        config.addDataSourceProperty("user", configuration.getString("storage.username"));
        config.addDataSourceProperty("password", configuration.getString("storage.password"));
        config.addDataSourceProperty("useSSL", configuration.getBoolean("storage.useSSL"));

        config.setMaximumPoolSize(configuration.getInteger("storage.pool.max-pool-size"));
        config.setMinimumIdle(configuration.getInteger("storage.pool.min-idle"));
        config.setMaxLifetime(configuration.getInteger("storage.pool.max-lifetime") * 1000);
        config.setConnectionTimeout(configuration.getInteger("storage.pool.connection-timeout") * 1000);

        config.setPoolName("BungeeUtilisals");
        config.setLeakDetectionThreshold(10000);
        config.setConnectionTestQuery("/* BungeeUtilisals ping */ SELECT 1;");
        config.setInitializationFailTimeout(-1);

        dataSource = new HikariDataSource(config);
    }

    protected abstract String getDataSourceClass();

    @Override
    public void close() {
        dataSource.close();
    }
}
