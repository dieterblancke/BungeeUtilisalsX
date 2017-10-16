package com.dbsoftwares.bungeeutilisals.bungee;

/*
 * Created by DBSoftwares on 19 augustus 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.bungee.config.MySQLConfig;
import com.dbsoftwares.bungeeutilisals.bungee.config.MainConfig;
import com.dbsoftwares.bungeeutilisals.bungee.metrics.Metrics;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeUtilisals extends Plugin {

    @Getter private static BungeeUtilisals instance;
    @Getter private MainConfig config;
    @Getter private HikariDataSource source;

    @Override
    public void onEnable() {
        // Setting instance
        instance = this;

        // Initialize metric system
        new Metrics(this);

        // Creating datafolder
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        // Initialize Configurations
        config = new MainConfig();
        config.loadDefaults();

        MySQLConfig mysql = new MySQLConfig(getDataFolder());
        mysql.loadDefaults();

        // Initializing database
        HikariDataSource source = new HikariDataSource();
        source.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        source.addDataSourceProperty("serverName", mysql.hostname);
        source.addDataSourceProperty("port", mysql.port);
        source.addDataSourceProperty("databaseName", mysql.database);
        source.addDataSourceProperty("user", mysql.username);
        source.addDataSourceProperty("password", mysql.password);

        source.setPoolName("BungeeUtilisals");
        source.setMaximumPoolSize(6);
        source.setMinimumIdle(2);
        source.setConnectionTimeout(2000);
        source.setLeakDetectionThreshold(4000);
    }

    @Override
    public void onDisable() {

    }
}