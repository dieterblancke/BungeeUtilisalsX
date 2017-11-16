package com.dbsoftwares.bungeeutilisals.bungee.api;

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.event.IEventLoader;
import com.dbsoftwares.bungeeutilisals.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisals.api.manager.IChatManager;
import com.dbsoftwares.bungeeutilisals.api.user.DatabaseUser;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.dbsoftwares.bungeeutilisals.api.user.UserCollection;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.api.language.LanguageManager;
import com.dbsoftwares.bungeeutilisals.bungee.event.EventLoader;
import com.dbsoftwares.bungeeutilisals.bungee.manager.ChatManager;
import com.dbsoftwares.bungeeutilisals.bungee.user.UserData;
import com.dbsoftwares.bungeeutilisals.bungee.user.UserList;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Optional;

public class BUtilisalsAPI implements BUAPI {

    private final BungeeUtilisals instance;
    private UserList users;
    private ChatManager chatManager;
    private EventLoader eventLoader;
    private LanguageManager languageManager;
    private UserData userdata;

    public BUtilisalsAPI(BungeeUtilisals instance) {
        this.instance = instance;
        this.users = new UserList();
        this.chatManager = new ChatManager();
        this.eventLoader = new EventLoader();
        this.languageManager = new LanguageManager(instance);
        this.userdata = new UserData();
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
}