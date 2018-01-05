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
import com.dbsoftwares.bungeeutilisals.api.experimental.event.PacketReceiveEvent;
import com.dbsoftwares.bungeeutilisals.api.experimental.event.PacketUpdateEvent;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.tools.ILoggers;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocations;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileUtils;
import com.dbsoftwares.bungeeutilisals.bungee.api.BUtilisalsAPI;
import com.dbsoftwares.bungeeutilisals.bungee.api.placeholder.DefaultPlaceHolders;
import com.dbsoftwares.bungeeutilisals.bungee.executors.UserChatExecutor;
import com.dbsoftwares.bungeeutilisals.bungee.executors.UserExecutor;
import com.dbsoftwares.bungeeutilisals.bungee.experimental.executors.PacketUpdateExecutor;
import com.dbsoftwares.bungeeutilisals.bungee.experimental.listeners.SimplePacketListener;
import com.dbsoftwares.bungeeutilisals.bungee.listeners.UserChatListener;
import com.dbsoftwares.bungeeutilisals.bungee.listeners.UserConnectionListener;
import com.dbsoftwares.bungeeutilisals.bungee.metrics.Metrics;
import com.dbsoftwares.bungeeutilisals.bungee.settings.Settings;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

public class BungeeUtilisals extends Plugin {

    @Getter private static final Logger log = Logger.getLogger("BungeeUtilisals");
    @Getter private static BungeeUtilisals instance;
    @Getter private static BUtilisalsAPI api;
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
        createAndLoadFiles();

        // Loading default PlaceHolders. Must be done BEFORE API (and database) loads.
        PlaceHolderAPI.loadPlaceHolderPack(new DefaultPlaceHolders());

        // Initializing API
        api = new BUtilisalsAPI(this);

        // Initialize metric system
        new Metrics(this);

        // Creating datafolder
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        // Registering experimental features
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
    }

    @Override
    public void onDisable() {
        api.getDatabaseManager().close();

        api.getLoggers().ifPresent(loggers -> loggers.getLoggers().forEach(ILoggers.Logger::shutdown));
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