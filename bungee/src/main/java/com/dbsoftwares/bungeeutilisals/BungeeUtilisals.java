/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals;

import com.dbsoftwares.bungeeutilisals.announcers.ActionBarAnnouncer;
import com.dbsoftwares.bungeeutilisals.announcers.ChatAnnouncer;
import com.dbsoftwares.bungeeutilisals.announcers.TitleAnnouncer;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventHandler;
import com.dbsoftwares.bungeeutilisals.api.event.event.IEventLoader;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserCommandEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager.StorageType;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.reflection.JarClassLoader;
import com.dbsoftwares.bungeeutilisals.api.utils.reflection.ReflectionUtils;
import com.dbsoftwares.bungeeutilisals.commands.addons.AddonCommand;
import com.dbsoftwares.bungeeutilisals.commands.general.*;
import com.dbsoftwares.bungeeutilisals.commands.plugin.PluginCommand;
import com.dbsoftwares.bungeeutilisals.commands.punishments.*;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnbanCommand;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnbanIPCommand;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnmuteCommand;
import com.dbsoftwares.bungeeutilisals.commands.punishments.removal.UnmuteIPCommand;
import com.dbsoftwares.bungeeutilisals.executors.MuteCheckExecutor;
import com.dbsoftwares.bungeeutilisals.executors.UserChatExecutor;
import com.dbsoftwares.bungeeutilisals.executors.UserExecutor;
import com.dbsoftwares.bungeeutilisals.executors.UserPunishExecutor;
import com.dbsoftwares.bungeeutilisals.library.Library;
import com.dbsoftwares.bungeeutilisals.listeners.MotdPingListener;
import com.dbsoftwares.bungeeutilisals.listeners.PunishmentListener;
import com.dbsoftwares.bungeeutilisals.listeners.UserChatListener;
import com.dbsoftwares.bungeeutilisals.listeners.UserConnectionListener;
import com.dbsoftwares.bungeeutilisals.placeholders.DefaultPlaceHolders;
import com.dbsoftwares.bungeeutilisals.placeholders.InputPlaceHolders;
import com.dbsoftwares.bungeeutilisals.placeholders.javascript.JavaScriptPlaceHolder;
import com.dbsoftwares.bungeeutilisals.placeholders.javascript.Script;
import com.dbsoftwares.bungeeutilisals.updater.Updatable;
import com.dbsoftwares.bungeeutilisals.updater.Updater;
import com.dbsoftwares.bungeeutilisals.utils.MessageBuilder;
import com.dbsoftwares.bungeeutilisals.utils.TPSRunnable;
import com.dbsoftwares.bungeeutilisals.utils.redis.RedisMessenger;
import com.dbsoftwares.configuration.api.FileStorageType;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import org.bstats.bungeecord.Metrics;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

@Updatable(url = "https://api.dbsoftwares.eu/plugin/BungeeUtilisals/")
public class BungeeUtilisals extends Plugin {

    @Getter
    private static BungeeUtilisals instance;

    @Getter
    private static BUtilisalsAPI api;

    @Getter
    private JarClassLoader jarClassLoader;

    @Getter
    private AbstractStorageManager databaseManagement;

    @Getter
    private List<Command> generalCommands = Lists.newArrayList();

    @Getter
    private List<Command> customCommands = Lists.newArrayList();

    @Getter
    private RedisMessenger redisMessenger;

    @Getter
    private List<Script> scripts = Lists.newArrayList();

    @Override
    public void onEnable() {
        if (ReflectionUtils.getJavaVersion() < 8) {
            BUCore.log(Level.SEVERE, "You are running a Java version older then Java 8.");
            BUCore.log(Level.SEVERE, "Please upgrade to Java 8 (9 recommended).");
            BUCore.log(Level.SEVERE, "BungeeUtilisals is not able to start up on Java versions older then 8.");
            return;
        }

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
        PlaceHolderAPI.loadPlaceHolderPack(new InputPlaceHolders());
        new JavaScriptPlaceHolder().register();
        loadScripts();

        // Loading libraries
        loadLibraries();

        // Loading database
        loadDatabase();

        // Initializing API
        api = new BUtilisalsAPI(this);

        // Loading language chat
        api.getLanguageManager().addPlugin(getDescription().getName(), new File(getDataFolder(), "languages"), FileStorageType.YAML);
        api.getLanguageManager().loadLanguages(getDescription().getName());

        // Loading & enabling addons
        api.getAddonManager().findAddons(api.getAddonManager().getAddonsFolder());
        api.getAddonManager().loadAddons();
        api.getAddonManager().enableAddons();

        // Initialize metric system
        new Metrics(this);

        redisMessenger = getConfig().getBoolean("redis") ? new RedisMessenger() : null;
        if (redisMessenger != null) {
            ProxyServer.getInstance().getPluginManager().registerListener(this, redisMessenger);
        }

        // Register executors & listeners
        ProxyServer.getInstance().getPluginManager().registerListener(this, new UserConnectionListener());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new UserChatListener());

        if (FileLocation.MOTD.getConfiguration().getBoolean("enabled")) {
            ProxyServer.getInstance().getPluginManager().registerListener(this, new MotdPingListener());
        }

        final IEventLoader loader = api.getEventLoader();

        loader.register(UserLoadEvent.class, new UserExecutor());
        loader.register(UserUnloadEvent.class, new UserExecutor());
        loader.register(UserChatEvent.class, new UserChatExecutor(api.getChatManager()));

        // Loading Punishment system
        if (FileLocation.PUNISHMENTS.getConfiguration().getBoolean("enabled")) {
            loadPunishmentCommands();

            ProxyServer.getInstance().getPluginManager().registerListener(this, new PunishmentListener());

            loader.register(UserPunishEvent.class, new UserPunishExecutor());

            MuteCheckExecutor muteCheckExecutor = new MuteCheckExecutor();
            loader.register(UserChatEvent.class, muteCheckExecutor);
            loader.register(UserCommandEvent.class, muteCheckExecutor);
        }

        // Loading Announcers
        Announcer.registerAnnouncers(ActionBarAnnouncer.class, ChatAnnouncer.class, TitleAnnouncer.class);

        // Loading Custom Commands
        loadGeneralCommands();
        loadCustomCommands();

        ProxyServer.getInstance().getScheduler().schedule(this, new TPSRunnable(), 50, TimeUnit.MILLISECONDS);

        if (getConfig().getBoolean("updater.enabled")) {
            Updater.initialize(this);
        }
    }

    @Override
    public void onDisable() {
        api.getAddonManager().disableAddons();
        BUCore.getApi().getUsers().forEach(User::unload);
        try {
            databaseManagement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        scripts.forEach(Script::unload);
        api.getEventLoader().getHandlers().forEach(EventHandler::unregister);
        Updater.shutdownUpdaters();
    }

    public void reload() {
        generalCommands.forEach(Command::unload);
        generalCommands.clear();
        loadGeneralCommands();

        if (FileLocation.PUNISHMENTS.getConfiguration().getBoolean("enabled")) {
            loadPunishmentCommands();
        }
        loadCustomCommands();

        Announcer.getAnnouncers().values().forEach(Announcer::reload);

        for (Language language : BUCore.getApi().getLanguageManager().getLanguages()) {
            BUCore.getApi().getLanguageManager().reloadConfig(getDescription().getName(), language);
        }

        loadScripts();
    }

    private void loadScripts() {
        scripts.forEach(Script::unload);
        scripts.clear();
        final File scripts = new File(getDataFolder(), "scripts");

        if (!scripts.exists()) {
            scripts.mkdir();

            IConfiguration.createDefaultFile(getResourceAsStream("scripts/hello.js"), new File(scripts, "hello.js"));
            IConfiguration.createDefaultFile(getResourceAsStream("scripts/coins.js"), new File(scripts, "coins.js"));
        }

        for (final File file : scripts.listFiles()) {
            if (file.isDirectory()) {
                continue;
            }
            try {
                final String code = new String(Files.readAllBytes(file.toPath()));
                final Script script = new Script(file.getName(), code);

                this.scripts.add(script);
            } catch (IOException | ScriptException e) {
                BUCore.log("Could not load script " + file.getName());
                e.printStackTrace();
            }
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
        BUCore.log("Loading libraries ...");
        jarClassLoader = new JarClassLoader(this);

        for (Library library : Library.values()) {
            if (library.shouldBeLoaded() && !library.isPresent()) {
                library.load();
            }
        }
        BUCore.log("Libraries have been loaded.");
    }

    public IConfiguration getConfig() {
        return FileLocation.CONFIG.getConfiguration();
    }

    private void createAndLoadFiles() {
        for (FileLocation location : FileLocation.values()) {
            File file = new File(getDataFolder(), location.getPath());

            if (!file.exists()) {
                IConfiguration.createDefaultFile(getResourceAsStream(location.getPath()), file);
            }

            location.loadConfiguration(file);
            location.loadData();
        }
    }

    private void loadGeneralCommands() {
        new PluginCommand();
        new AddonCommand();

        loadGeneralCommand("glist", GListCommand.class);
        loadGeneralCommand("announce", AnnounceCommand.class);
        loadGeneralCommand("find", FindCommand.class);
        loadGeneralCommand("server", ServerCommand.class);
        loadGeneralCommand("clearchat", ClearChatCommand.class);
        loadGeneralCommand("chatlock", ChatLockCommand.class);
        loadGeneralCommand("glag", GLagCommand.class);
        loadGeneralCommand("staffchat", StaffChatCommand.class);
    }

    private void loadPunishmentCommands() {
        loadPunishmentCommand("ban", BanCommand.class);
        loadPunishmentCommand("ipban", IPBanCommand.class);
        loadPunishmentCommand("tempban", TempBanCommand.class);
        loadPunishmentCommand("iptempban", IPTempBanCommand.class);
        loadPunishmentCommand("mute", MuteCommand.class);
        loadPunishmentCommand("ipmute", IPMuteCommand.class);
        loadPunishmentCommand("tempmute", TempMuteCommand.class);
        loadPunishmentCommand("iptempmute", IPTempMuteCommand.class);

        loadPunishmentCommand("kick", KickCommand.class);
        loadPunishmentCommand("warn", WarnCommand.class);

        loadPunishmentCommand("unban", UnbanCommand.class);
        loadPunishmentCommand("unbanip", UnbanIPCommand.class);
        loadPunishmentCommand("unmute", UnmuteCommand.class);
        loadPunishmentCommand("unmuteip", UnmuteIPCommand.class);
    }

    private void loadCustomCommands() {
        customCommands.forEach(Command::unload);
        customCommands.clear();

        IConfiguration config = FileLocation.CUSTOMCOMMANDS.getConfiguration();

        for (ISection section : config.getSectionList("commands")) {
            String name = section.getString("name");
            List<String> aliases = section.exists("aliases") ? section.getStringList("aliases") : Lists.newArrayList();
            String permission = section.exists("permission") ? section.getString("permission") : null;
            List<String> commands = section.exists("execute") ? section.getStringList("execute") : Lists.newArrayList();

            Command command = new Command(name, aliases, permission) {

                @Override
                public void onExecute(User user, String[] args) {
                    List<TextComponent> components;

                    if (section.isList("messages")) {
                        components = MessageBuilder.buildMessage(user, section.getSectionList("messages"));
                    } else {
                        components = Lists.newArrayList(MessageBuilder.buildMessage(user, section.getSection("messages")));
                    }

                    components.forEach(user::sendMessage);
                    commands.forEach(command -> ProxyServer.getInstance().getPluginManager().dispatchCommand(
                            ProxyServer.getInstance().getConsole(),
                            PlaceHolderAPI.formatMessage(user, command)
                    ));
                }

                @Override
                public List<String> onTabComplete(User user, String[] args) {
                    return ImmutableList.of();
                }
            };

            customCommands.add(command);
        }
    }

    private void loadPunishmentCommand(String name, Class<? extends Command> clazz) {
        loadCommand("commands." + name + ".enabled", FileLocation.PUNISHMENTS.getConfiguration(), clazz);
    }

    private void loadGeneralCommand(String name, Class<? extends Command> clazz) {
        loadCommand(name + ".enabled", FileLocation.GENERALCOMMANDS.getConfiguration(), clazz);
    }

    private void loadCommand(String enabledPath, IConfiguration configuration, Class<? extends Command> clazz) {
        if (configuration.getBoolean(enabledPath)) {
            try {
                Command command = clazz.newInstance();

                generalCommands.add(command);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}