package com.dbsoftwares.bungeeutilisals.bungee;

/*
 * Created by DBSoftwares on 19 augustus 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.configuration.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.configuration.yaml.YamlConfiguration;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatPreExecuteEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileUtils;
import com.dbsoftwares.bungeeutilisals.bungee.api.APIHandler;
import com.dbsoftwares.bungeeutilisals.bungee.api.BUtilisalsAPI;
import com.dbsoftwares.bungeeutilisals.bungee.executors.UserChatExecutor;
import com.dbsoftwares.bungeeutilisals.bungee.executors.UserExecutor;
import com.dbsoftwares.bungeeutilisals.bungee.listeners.UserChatListener;
import com.dbsoftwares.bungeeutilisals.bungee.listeners.UserConnectionListener;
import com.dbsoftwares.bungeeutilisals.bungee.metrics.Metrics;
import com.dbsoftwares.bungeeutilisals.bungee.settings.Settings;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.ProxyConnection;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class BungeeUtilisals extends Plugin {

    @Getter private static final Logger log = Logger.getLogger("BungeeUtilisals");
    @Getter private static BungeeUtilisals instance;
    @Getter private static BUtilisalsAPI api;
    @Getter private HikariDataSource source;
    @Getter private Settings settings;
    @Getter private Map<String, List<String>> aliases;

    @Override
    public void onEnable() {
        // Setting instance
        instance = this;

        // Creating datafolder if not exists.
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Loading setting files ...
        settings = new Settings();

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
        loadMySQL();

        if (settings.PUNISHMENT_ENABLED.get()) {
            // TODO: Make and initialize punishment system

        }

        try {
            File aliases = new File(getDataFolder(), "aliases.yml");
            FileUtils.createDefaultFile(this, "aliases.yml", aliases, true);

            YamlConfiguration configuration = IConfiguration.loadConfiguration(YamlConfiguration.class, aliases);
            for (String key : configuration.getKeys("commands")) {
                this.aliases.put(key, configuration.getStringList("commands." + key));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Register executors & listeners
        ProxyServer.getInstance().getPluginManager().registerListener(this, new UserConnectionListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new UserChatListener());

        UserExecutor userExecutor = new UserExecutor();
        api.getEventLoader().register(UserLoadEvent.class, userExecutor::onLoad);
        api.getEventLoader().register(UserUnloadEvent.class, userExecutor::onUnload);

        UserChatExecutor userChatExecutor = new UserChatExecutor(api.getChatManager());
        api.getEventLoader().register(UserChatEvent.class, userChatExecutor::onSwearChat);
        api.getEventLoader().register(UserChatEvent.class, userChatExecutor::onUnicodeSymbol);
        api.getEventLoader().register(UserChatPreExecuteEvent.class, userChatExecutor::onUnicodeReplace);
    }

    @Override
    public void onDisable() {
        source.close();
    }

    private void loadMySQL() {
        source = new HikariDataSource();
        source.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        source.addDataSourceProperty("serverName", settings.MYSQL_HOST.get());
        source.addDataSourceProperty("port", settings.MYSQL_PORT.get());
        source.addDataSourceProperty("databaseName", settings.MYSQL_DATABASE.get());
        source.addDataSourceProperty("user", settings.MYSQL_USERNAME.get());
        source.addDataSourceProperty("password", settings.MYSQL_PASSWORD.get());

        source.setPoolName("BungeeUtilisals");
        source.setMaximumPoolSize(6);
        source.setMinimumIdle(2);
        source.setConnectionTimeout(2000);
        source.setLeakDetectionThreshold(4000);

        try (ProxyConnection connection = (ProxyConnection) source.getConnection()) {
            PreparedStatement statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + settings.PUNISHMENT_TABLE.get() + "(id INT NOT NULL AUTO_INCREMENT, " +
                            "uuid VARCHAR(64) NOT NULL, name VARCHAR(45) NOT NULL, ip VARCHAR(32) NOT NULL, type VARCHAR(16) NOT NULL, " +
                            "date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP, data JSON NOT NULL, active TINYINT NOT NULL DEFAULT 1, " +
                            "removed_by VARCHAR(45) NOT NULL, removed_at DATE NOT NULL);"
            );

            statement.executeUpdate();
            statement.close();

            statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS " + settings.PUNISHMENT_SOFT_TABLE.get() + "(id INT NOT NULL AUTO_INCREMENT, " +
                            "uuid VARCHAR(64) NOT NULL, name VARCHAR(45) NOT NULL, type VARCHAR(16) NOT NULL, " +
                            "date DATE NOT NULL DEFAULT CURRENT_TIMESTAMP, data JSON NOT NULL;"
            );

            statement.executeUpdate();
            statement.close();


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}