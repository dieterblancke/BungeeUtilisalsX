package com.dbsoftwares.bungeeutilisals.bungee.api;

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.configuration.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.event.event.IEventLoader;
import com.dbsoftwares.bungeeutilisals.api.execution.SimpleExecutor;
import com.dbsoftwares.bungeeutilisals.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisals.api.manager.IChatManager;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.tools.IDebugger;
import com.dbsoftwares.bungeeutilisals.api.user.ConsoleUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.DatabaseUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.UserCollection;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.api.language.LanguageManager;
import com.dbsoftwares.bungeeutilisals.bungee.api.tools.Debugger;
import com.dbsoftwares.bungeeutilisals.bungee.event.EventLoader;
import com.dbsoftwares.bungeeutilisals.bungee.manager.ChatManager;
import com.dbsoftwares.bungeeutilisals.bungee.punishments.PunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.bungee.user.UserData;
import com.dbsoftwares.bungeeutilisals.bungee.user.UserList;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class BUtilisalsAPI implements BUAPI {

    private final BungeeUtilisals instance;
    private ConsoleUser console;
    private UserList users;
    private ChatManager chatManager;
    private EventLoader eventLoader;
    private LanguageManager languageManager;
    private UserData userdata;
    private SimpleExecutor simpleExecutor;
    private Debugger debugger;
    private PunishmentExecutor punishmentExecutor;

    public BUtilisalsAPI(BungeeUtilisals instance) {
        APIHandler.registerProvider(this);

        this.instance = instance;
        this.console = new ConsoleUser();
        this.users = new UserList();
        this.chatManager = new ChatManager();
        this.eventLoader = new EventLoader();
        this.languageManager = new LanguageManager(instance);
        this.userdata = new UserData();
        this.simpleExecutor = new SimpleExecutor();
        this.debugger = new Debugger();
        this.punishmentExecutor = new PunishmentExecutor();
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
    public IConfiguration getConfig(FileLocation location) {
        return BungeeUtilisals.getConfigurations().get(location);
    }

    @Override
    public Connection getConnection() throws SQLException {
        return instance.getDatabaseManagement().getConnection();
    }

    @Override
    public IPunishmentExecutor getPunishmentExecutor() {
        return punishmentExecutor;
    }

    @Override
    public ConsoleUser getConsole() {
        return console;
    }

    @Override
    public void broadcast(String message) {
        users.forEach(user -> user.sendMessage(message));
        getConsole().sendMessage(message);
    }

    @Override
    public void broadcast(String message, String permission) {
        users.stream().filter(user -> user.getParent().hasPermission(permission)).forEach(user -> user.sendMessage(message));
        getConsole().sendMessage(message);
    }

    @Override
    public void announce(String prefix, String message) {
        users.forEach(user -> user.sendMessage(prefix, message));
        getConsole().sendMessage(prefix, message);
    }

    @Override
    public void announce(String prefix, String message, String permission) {
        users.stream().filter(user -> user.getParent().hasPermission(permission)).forEach(user -> user.sendMessage(prefix, message));
        getConsole().sendMessage(prefix, message);
    }

    @Override
    public void langBroadcast(String message, Object... placeholders) {
        users.forEach(user -> user.sendLangMessage(message, placeholders));
        getConsole().sendLangMessage(message, placeholders);
    }

    @Override
    public void langBroadcast(String message, String permission, Object... placeholders) {
        users.stream().filter(user -> user.getParent().hasPermission(permission)).forEach(user -> user.sendLangMessage(message, placeholders));
        getConsole().sendLangMessage(message, placeholders);
    }
}