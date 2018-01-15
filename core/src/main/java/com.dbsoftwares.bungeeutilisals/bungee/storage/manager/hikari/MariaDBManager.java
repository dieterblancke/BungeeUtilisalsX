package com.dbsoftwares.bungeeutilisals.bungee.storage.manager.hikari;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.configuration.yaml.YamlConfiguration;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class MariaDBManager extends HikariManager {

    public MariaDBManager(YamlConfiguration configuration) {
        config = new HikariConfig();
        config.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        config.addDataSourceProperty("serverName", configuration.getString("storage.mysql.hostname"));
        config.addDataSourceProperty("port", configuration.getInteger("storage.mysql.port"));
        config.addDataSourceProperty("databaseName", configuration.getString("storage.mysql.database"));
        config.setUsername(configuration.getString("storage.mysql.username"));
        config.setPassword(configuration.getString("storage.mysql.password"));

        config.setMaximumPoolSize(configuration.getInteger("storage.mysql.max-pool-size"));
        addConfigDefaults();

        dataSource = new HikariDataSource(config);
    }
}