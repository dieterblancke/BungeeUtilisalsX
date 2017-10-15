package com.dbsoftwares.bungeeutilisals.bungee;

/*
 * Created by DBSoftwares on 19 augustus 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.bungee.addons.AddonManager;
import com.dbsoftwares.bungeeutilisals.bungee.configuration.MainConfig;
import com.dbsoftwares.bungeeutilisals.bungee.metrics.Metrics;
import com.dbsoftwares.bungeeutilisals.bungee.task.TaskScheduler;
import com.dbsoftwares.bungeeutilisals.bungee.user.BUser;
import com.dbsoftwares.bungeeutilisals.universal.configs.MySQLConfig;
import com.dbsoftwares.bungeeutilisals.universal.enums.UserType;
import com.google.common.collect.Lists;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;
import java.util.Iterator;
import java.util.List;

public class BungeeUtilisals extends Plugin {

    @Getter private static BungeeUtilisals instance;
    @Getter private TaskScheduler scheduler;
    @Getter private AddonManager addonManager;
    @Getter private MainConfig config;
    @Getter private HikariDataSource source;

    private List<BUser> users = Lists.newArrayList();

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

        // Initialize Scheduler object
        scheduler = new TaskScheduler();

        // Loading addons
        addonManager = new AddonManager();

        // Initializing console user
        users.add(new BUser(UserType.CONSOLE, "CONSOLE"));
    }

    @Override
    public void onDisable() {

    }

    // Using iterator in order to remove users easily without throwing ConcurrentModificationException.
    public Iterator<BUser> getUsers() {
        return users.iterator();
    }
}