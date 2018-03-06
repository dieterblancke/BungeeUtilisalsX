package com.dbsoftwares.bungeeutilisals.bungee;

/*
 * Created by DBSoftwares on 19 augustus 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.configuration.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatPreExecuteEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.interfaces.IEventLoader;
import com.dbsoftwares.bungeeutilisals.api.experimental.event.PacketReceiveEvent;
import com.dbsoftwares.bungeeutilisals.api.experimental.event.PacketUpdateEvent;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager.StorageType;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileUtils;
import com.dbsoftwares.bungeeutilisals.bungee.api.BUtilisalsAPI;
import com.dbsoftwares.bungeeutilisals.bungee.api.configuration.yaml.YamlConfiguration;
import com.dbsoftwares.bungeeutilisals.bungee.api.placeholder.DefaultPlaceHolders;
import com.dbsoftwares.bungeeutilisals.bungee.commands.PluginCommand;
import com.dbsoftwares.bungeeutilisals.bungee.commands.punishments.*;
import com.dbsoftwares.bungeeutilisals.bungee.executors.UserChatExecutor;
import com.dbsoftwares.bungeeutilisals.bungee.executors.UserExecutor;
import com.dbsoftwares.bungeeutilisals.bungee.experimental.executors.PacketUpdateExecutor;
import com.dbsoftwares.bungeeutilisals.bungee.experimental.listeners.SimplePacketListener;
import com.dbsoftwares.bungeeutilisals.bungee.library.Library;
import com.dbsoftwares.bungeeutilisals.bungee.library.classloader.LibraryClassLoader;
import com.dbsoftwares.bungeeutilisals.bungee.listeners.PunishmentListener;
import com.dbsoftwares.bungeeutilisals.bungee.listeners.UserChatListener;
import com.dbsoftwares.bungeeutilisals.bungee.listeners.UserConnectionListener;
import com.dbsoftwares.bungeeutilisals.bungee.metrics.Metrics;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.sql.SQLException;
import java.util.Map;

public class BungeeUtilisals extends Plugin {

    @Getter
    private static BungeeUtilisals instance;
    @Getter
    private static BUtilisalsAPI api;
    @Getter
    private static Map<FileLocation, IConfiguration> configurations = Maps.newHashMap();
    @Getter
    private LibraryClassLoader libraryClassLoader;
    @Getter
    private AbstractStorageManager databaseManagement;

    public static IConfiguration getConfiguration(FileLocation location) {
        return configurations.get(location);
    }

    public static void log(String message) {
        System.out.println("[BungeeUtilisals] " + message);
    }

    @Override
    public void onEnable() {
        // Setting instance
        instance = this;

        // Creating datafolder if not exists.
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        // Loading setting files ...
        createAndLoadFiles();

        // Loading default PlaceHolders. Must be done BEFORE API / database loads.
        PlaceHolderAPI.loadPlaceHolderPack(new DefaultPlaceHolders());

        // Loading libraries
        loadLibraries();

        // Loading database
        loadDatabase();

        // Initializing API
        api = new BUtilisalsAPI(this);

        // Initialize metric system
        new Metrics(this);

        // Creating datafolder
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        // Registering experimental features
        if (configurations.get(FileLocation.CONFIG).getBoolean("experimental")) {
            registerExperimentalFeatures();
        }

        // Register executors & listeners
        ProxyServer.getInstance().getPluginManager().registerListener(this, new UserConnectionListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new UserChatListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PunishmentListener());

        IEventLoader loader = api.getEventLoader();

        UserExecutor userExecutor = new UserExecutor();
        loader.register(UserLoadEvent.class, userExecutor::onLoad);
        loader.register(UserUnloadEvent.class, userExecutor::onUnload);

        UserChatExecutor userChatExecutor = new UserChatExecutor(api.getChatManager());
        loader.register(UserChatEvent.class, userChatExecutor::onSwearChat);
        loader.register(UserChatEvent.class, userChatExecutor::onUnicodeSymbol);
        loader.register(UserChatPreExecuteEvent.class, userChatExecutor::onUnicodeReplace);
        loader.register(UserChatEvent.class, userChatExecutor::onCapsChat);
        loader.register(UserChatEvent.class, userChatExecutor::onSpamChat);
        loader.register(UserChatEvent.class, userChatExecutor::onAdChat);

        new PluginCommand();
        if (getConfiguration(FileLocation.PUNISHMENTS_CONFIG).getBoolean("enabled")) {
            new BanCommand();
            new IPBanCommand();
            new TempBanCommand();
            new IPTempBanCommand();
            new MuteCommand();
            new IPMuteCommand();
            new TempMuteCommand();
            new IPTempMuteCommand();
        }
    }

    @Override
    public void onDisable() {
        try {
            databaseManagement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadDatabase() {
        StorageType type;
        try {
            type = StorageType.valueOf(getConfig().getString("storage.type").toUpperCase());
        } catch (IllegalArgumentException e) {
            type = StorageType.MYSQL;
        }
        try {
            databaseManagement = type.getManager().getConstructor(Plugin.class).newInstance(this);
            databaseManagement.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadLibraries() {
        log("Loading libraries ...");
        libraryClassLoader = new LibraryClassLoader(this);

        for (Library library : Library.values()) {
            if (!library.isPresent()) {
                library.load();
            }
        }
        log("Libraries have been loaded.");
    }

    public IConfiguration getConfig() {
        return configurations.get(FileLocation.CONFIG);
    }

    private void createAndLoadFiles() {
        for (FileLocation location : FileLocation.values()) {
            File file = new File(getDataFolder(), location.getPath());

            if (!file.exists()) {
                FileUtils.createDefaultFile(this, location.getPath(), file, true);
            }

            YamlConfiguration configuration = (YamlConfiguration) IConfiguration.loadYamlConfiguration(file);
            configurations.put(location, configuration);
        }
    }

    private void registerExperimentalFeatures() {
        /*  EXAMPLES:

            Utils.registerPacket(Protocol.GAME.TO_SERVER, 47, 0x07, PacketPlayInWindowClick.class);
            Utils.registerPacket(Protocol.GAME.TO_SERVER, 47, 0x08, PacketPlayInCloseWindow.class);

            Utils.registerPacket(Protocol.GAME.TO_CLIENT, 47, 0x12, PacketPlayOutCloseWindow.class);
            Utils.registerPacket(Protocol.GAME.TO_CLIENT, 47, 0x13, PacketPlayOutOpenWindow.class);
            Utils.registerPacket(Protocol.GAME.TO_CLIENT, 47, 0x14, PacketPlayOutWindowItems.class);
            Utils.registerPacket(Protocol.GAME.TO_CLIENT, 47, 0x16, PacketPlayOutSetSlot.class);
        */

        ProxyServer.getInstance().getPluginManager().registerListener(this, new SimplePacketListener());

        PacketUpdateExecutor packetUpdateExecutor = new PacketUpdateExecutor();
        api.getEventLoader().register(PacketUpdateEvent.class, packetUpdateExecutor::onPacketUpdate);
        api.getEventLoader().register(PacketReceiveEvent.class, packetUpdateExecutor::onPacketReceive);
    }
}