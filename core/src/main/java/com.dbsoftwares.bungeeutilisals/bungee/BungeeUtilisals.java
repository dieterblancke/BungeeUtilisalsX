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
import com.dbsoftwares.bungeeutilisals.api.experimental.item.ItemMeta;
import com.dbsoftwares.bungeeutilisals.api.experimental.item.ItemStack;
import com.dbsoftwares.bungeeutilisals.api.experimental.item.Material;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.client.PacketPlayInCloseWindow;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.client.PacketPlayInWindowClick;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.server.PacketPlayOutCloseWindow;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.server.PacketPlayOutOpenWindow;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.server.PacketPlayOutSetSlot;
import com.dbsoftwares.bungeeutilisals.api.experimental.packets.server.PacketPlayOutWindowItems;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileUtils;
import com.dbsoftwares.bungeeutilisals.bungee.api.APIHandler;
import com.dbsoftwares.bungeeutilisals.bungee.api.BUtilisalsAPI;
import com.dbsoftwares.bungeeutilisals.bungee.executors.UserChatExecutor;
import com.dbsoftwares.bungeeutilisals.bungee.executors.UserExecutor;
import com.dbsoftwares.bungeeutilisals.bungee.experimental.inventory.BungeeInventory;
import com.dbsoftwares.bungeeutilisals.bungee.experimental.listeners.PacketInjectListener;
import com.dbsoftwares.bungeeutilisals.bungee.listeners.UserChatListener;
import com.dbsoftwares.bungeeutilisals.bungee.listeners.UserConnectionListener;
import com.dbsoftwares.bungeeutilisals.bungee.metrics.Metrics;
import com.dbsoftwares.bungeeutilisals.bungee.settings.FileLocations;
import com.dbsoftwares.bungeeutilisals.bungee.settings.Settings;
import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.pool.ProxyConnection;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.protocol.Protocol;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Logger;

public class BungeeUtilisals extends Plugin {

    @Getter private static final Logger log = Logger.getLogger("BungeeUtilisals");
    @Getter private static BungeeUtilisals instance;
    @Getter private static BUtilisalsAPI api;
    @Getter private HikariDataSource source;
    @Getter private Settings settings;
    @Getter private static Map<FileLocations, YamlConfiguration> configurations = Maps.newHashMap();

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
        createAndLoadFiles();

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

        if (configurations.get(FileLocations.CONFIG).getBoolean("experimental")) {
            registerExperimentalFeatures();
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


        api.getEventLoader().register(UserLoadEvent.class, event -> {
            User user = event.getUser();
            BungeeInventory inventory = new BungeeInventory();

            inventory.setItem(0, new ItemStack(Material.BED).setAmount(1).setData(1)
                    .setItemMeta(new ItemMeta().setDisplayName(Utils.c("&cColored Bed")).setLore(Utils.c("&cThis item is a colored bed! :O"))));
            inventory.setItem(1, new ItemStack(Material.BED).setAmount(1).setData(2)
                    .setItemMeta(new ItemMeta().setDisplayName(Utils.c("&cColored Bed")).setLore(Utils.c("&cThis item is a colored bed! :O"))));
            inventory.setItem(2, new ItemStack(Material.BED).setAmount(1).setData(3)
                    .setItemMeta(new ItemMeta().setDisplayName(Utils.c("&cColored Bed")).setLore(Utils.c("&cThis item is a colored bed! :O"))));
            inventory.setItem(3, new ItemStack(Material.BED).setAmount(1).setData(4)
                    .setItemMeta(new ItemMeta().setDisplayName(Utils.c("&cColored Bed")).setLore(Utils.c("&cThis item is a colored bed! :O"))));
            inventory.setItem(4, new ItemStack(Material.BED).setAmount(1).setData(14)
                    .setItemMeta(new ItemMeta().setDisplayName(Utils.c("&cColored Bed")).setLore(Utils.c("&cThis item is a colored bed! :O"))));

            user.experimental().openInventory(inventory);
        });
    }

    @Override
    public void onDisable() {
        source.close();
    }

    private void createAndLoadFiles() {
        for (FileLocations location : FileLocations.values()) {
            File file = new File(getDataFolder(), location.getPath());

            if (!file.exists()) {
                FileUtils.createDefaultFile(this, location.getPath(), file, true);
            }

            YamlConfiguration configuration = IConfiguration.loadConfiguration(YamlConfiguration.class, file);
            configurations.put(location, configuration);
        }
    }

    private void loadMySQL() {
        source = new HikariDataSource();
        YamlConfiguration configuration = configurations.get(FileLocations.MYSQL);

        source.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        source.addDataSourceProperty("serverName", configuration.getString("hostname"));
        source.addDataSourceProperty("port", configuration.getInteger("port"));
        source.addDataSourceProperty("databaseName", configuration.getString("database"));
        source.addDataSourceProperty("user", configuration.getString("username"));
        source.addDataSourceProperty("password", configuration.getString("password"));

        source.setPoolName("BungeeUtilisals");
        source.setMaximumPoolSize(6);
        source.setMinimumIdle(2);
        source.setConnectionTimeout(2000);
        source.setLeakDetectionThreshold(4000);

        try (ProxyConnection connection = (ProxyConnection) source.getConnection()) {

            // TODO: Create default tables.

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void registerExperimentalFeatures() {
        Utils.registerPacket(Protocol.GAME.TO_SERVER, 47, 0x07, PacketPlayInWindowClick.class);
        Utils.registerPacket(Protocol.GAME.TO_SERVER, 47, 0x08, PacketPlayInCloseWindow.class);


        Utils.registerPacket(Protocol.GAME.TO_CLIENT, 47, 0x12, PacketPlayOutCloseWindow.class);
        Utils.registerPacket(Protocol.GAME.TO_CLIENT, 47, 0x13, PacketPlayOutOpenWindow.class);
        Utils.registerPacket(Protocol.GAME.TO_CLIENT, 47, 0x14, PacketPlayOutWindowItems.class);
        Utils.registerPacket(Protocol.GAME.TO_CLIENT, 47, 0x16, PacketPlayOutSetSlot.class);

        ProxyServer.getInstance().getPluginManager().registerListener(this, new PacketInjectListener());
    }
}