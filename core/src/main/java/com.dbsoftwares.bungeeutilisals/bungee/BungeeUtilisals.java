package com.dbsoftwares.bungeeutilisals.bungee;

/*
 * Created by DBSoftwares on 19 augustus 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.event.events.UserChatEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.UserLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisals.api.json.IJsonConfiguration;
import com.dbsoftwares.bungeeutilisals.api.json.JsonConfiguration;
import com.dbsoftwares.bungeeutilisals.api.settings.SettingManager;
import com.dbsoftwares.bungeeutilisals.bungee.api.APIHandler;
import com.dbsoftwares.bungeeutilisals.bungee.api.BUtilisalsAPI;
import com.dbsoftwares.bungeeutilisals.bungee.executors.UserChatExecutor;
import com.dbsoftwares.bungeeutilisals.bungee.executors.UserExecutor;
import com.dbsoftwares.bungeeutilisals.bungee.listeners.UserChatListener;
import com.dbsoftwares.bungeeutilisals.bungee.listeners.UserConnectionListener;
import com.dbsoftwares.bungeeutilisals.bungee.metrics.Metrics;
import com.dbsoftwares.bungeeutilisals.bungee.settings.MySQLSettings;
import com.dbsoftwares.bungeeutilisals.bungee.settings.chat.SwearSettings;
import com.dbsoftwares.bungeeutilisals.bungee.settings.chat.UTFSettings;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class BungeeUtilisals extends Plugin {

    @Getter private static final Logger log = Logger.getLogger("BungeeUtilisals");
    @Getter private static BungeeUtilisals instance;
    @Getter private static BUtilisalsAPI api;
    @Getter private HikariDataSource source;

    @Override
    public void onEnable() {
        // Setting instance
        instance = this;

        // Creating datafolder if not exists.
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        try {
            InputStream input = getResourceAsStream("test.json");
            JsonConfiguration def = IJsonConfiguration.loadConfiguration(input);
            JsonConfiguration configuration = IJsonConfiguration.loadConfiguration(new File(getDataFolder(), "test.json"));

            configuration.copyDefaults(def);
            configuration.save();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Loading setting files ...
        MySQLSettings mysql = new MySQLSettings();
        SettingManager.loadSettings(mysql);
        SettingManager.loadSettings(new SwearSettings());
        SettingManager.loadSettings(new UTFSettings());

        // Initializing API
        api = new BUtilisalsAPI(this);
        APIHandler.registerProvider(api);

        // Initialize metric system
        new Metrics(this);

        // Creating datafolder
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        // Initializing database
        HikariDataSource source = new HikariDataSource();
        source.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        source.addDataSourceProperty("serverName", mysql.getHost());
        source.addDataSourceProperty("port", mysql.getPort());
        source.addDataSourceProperty("databaseName", mysql.getDatabase());
        source.addDataSourceProperty("user", mysql.getUsername());
        source.addDataSourceProperty("password", mysql.getPassword());

        source.setPoolName("BungeeUtilisals");
        source.setMaximumPoolSize(6);
        source.setMinimumIdle(2);
        source.setConnectionTimeout(2000);
        source.setLeakDetectionThreshold(4000);

        // Register executors & listeners
        ProxyServer.getInstance().getPluginManager().registerListener(this, new UserConnectionListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new UserChatListener());

        UserExecutor userExecutor = new UserExecutor();
        api.getEventLoader().register(UserLoadEvent.class, userExecutor::onLoad);
        api.getEventLoader().register(UserUnloadEvent.class, userExecutor::onUnload);

        UserChatExecutor userChatExecutor = new UserChatExecutor(api.getChatManager());
        api.getEventLoader().register(UserChatEvent.class, userChatExecutor::onUnicodeReplace);
        api.getEventLoader().register(UserChatEvent.class, userChatExecutor::onSwearChat);
    }

    @Override
    public void onDisable() {
        source.close();
    }
}