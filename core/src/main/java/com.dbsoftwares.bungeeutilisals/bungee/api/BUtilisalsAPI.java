package com.dbsoftwares.bungeeutilisals.bungee.api;

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.configuration.yaml.YamlConfiguration;
import com.dbsoftwares.bungeeutilisals.api.event.IEventLoader;
import com.dbsoftwares.bungeeutilisals.api.execution.SimpleExecutor;
import com.dbsoftwares.bungeeutilisals.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisals.api.manager.IChatManager;
import com.dbsoftwares.bungeeutilisals.api.tools.IDebugger;
import com.dbsoftwares.bungeeutilisals.api.tools.ILoggers;
import com.dbsoftwares.bungeeutilisals.api.user.DatabaseUser;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.dbsoftwares.bungeeutilisals.api.user.UserCollection;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocations;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.api.language.LanguageManager;
import com.dbsoftwares.bungeeutilisals.bungee.api.tools.Debugger;
import com.dbsoftwares.bungeeutilisals.bungee.api.tools.Loggers;
import com.dbsoftwares.bungeeutilisals.bungee.event.EventLoader;
import com.dbsoftwares.bungeeutilisals.bungee.manager.ChatManager;
import com.dbsoftwares.bungeeutilisals.bungee.user.UserData;
import com.dbsoftwares.bungeeutilisals.bungee.user.UserList;
import com.zaxxer.hikari.pool.ProxyConnection;
import lombok.Getter;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.SQLException;
import java.util.Optional;

public class BUtilisalsAPI implements BUAPI {

    private final BungeeUtilisals instance;
    @Getter
    private DatabaseManager databaseManager;
    private UserList users;
    private ChatManager chatManager;
    private EventLoader eventLoader;
    private LanguageManager languageManager;
    private UserData userdata;
    private SimpleExecutor simpleExecutor;
    private Debugger debugger;
    private Loggers loggers;

    public BUtilisalsAPI(BungeeUtilisals instance) {
        this.instance = instance;
        this.users = new UserList();
        this.databaseManager = new DatabaseManager(this);
        this.chatManager = new ChatManager();
        this.eventLoader = new EventLoader();
        this.languageManager = new LanguageManager(instance);
        this.userdata = new UserData();
        this.simpleExecutor = new SimpleExecutor();
        this.debugger = new Debugger();

        if (getConfig(FileLocations.CONFIG).getBoolean("logging.enabled")) {
            this.loggers = new Loggers(instance);
        }
    }

    @Override
    public Plugin getPlugin() {
        return instance;
    }

    @Override
    public ILanguageManager getLanguageManager() {
        return languageManager;
    }

    @Override
    public IEventLoader getEventLoader() {
        return eventLoader;
    }

    @Override
    public Optional<User> getUser(String name) {
        return users.fromName(name);
    }

    @Override
    public Optional<User> getUser(ProxiedPlayer player) {
        return users.fromPlayer(player);
    }

    @Override
    public UserCollection getUsers() {
        return users;
    }

    @Override
    public UserCollection getUsers(String permission) {
        UserList list = new UserList();
        users.stream().filter(user -> user.getParent().hasPermission(permission)).forEach(list::add);
        return list;
    }

    @Override
    public UserCollection newUserCollection() {
        return new UserList();
    }

    @Override
    public DatabaseUser getUserData() {
        return userdata;
    }

    @Override
    public IChatManager getChatManager() {
        return chatManager;
    }

    @Override
    public SimpleExecutor getSimpleExecutor() {
        return simpleExecutor;
    }

    @Override
    public IDebugger getDebugger() {
        return debugger;
    }

    @Override
    public Optional<ILoggers> getLoggers() {
        return Optional.ofNullable(loggers);
    }

    @Override
    public YamlConfiguration getConfig(FileLocations location) {
        return BungeeUtilisals.getConfigurations().get(location);
    }

    @Override
    public ProxyConnection getConnection() throws SQLException {
        return (ProxyConnection) databaseManager.getConnection();
    }
}