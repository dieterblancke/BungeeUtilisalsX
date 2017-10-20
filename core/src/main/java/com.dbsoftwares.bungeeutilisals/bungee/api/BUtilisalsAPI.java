package com.dbsoftwares.bungeeutilisals.bungee.api;

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.configuration.MainConfig;
import com.dbsoftwares.bungeeutilisals.api.event.IEventLoader;
import com.dbsoftwares.bungeeutilisals.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisals.api.user.DatabaseUser;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.dbsoftwares.bungeeutilisals.api.user.UserCollection;
import com.dbsoftwares.bungeeutilisals.api.user.UserList;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import java.util.Optional;

public class BUtilisalsAPI implements BUAPI {

    private final BungeeUtilisals instance;
    private MainConfig config;

    private UserList users;

    public BUtilisalsAPI(BungeeUtilisals instance) {
        this.instance = instance;
        this.config = new MainConfig();
        this.config.loadDefaults();

        this.users = new UserList();
    }

    @Override
    public Plugin getPlugin() {
        return instance;
    }

    @Override
    public String getPrefix() {
        return Utils.c(config.prefix);
    }

    @Override
    public ILanguageManager getLanguageManager() {
        return null;
    }

    @Override
    public IEventLoader getEventLoader() {
        return null;
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
        users.stream().filter(user -> user.getPlayer().hasPermission(permission)).forEach(list::add);
        return list;
    }

    @Override
    public UserCollection newUserCollection() {
        return new UserList();
    }

    @Override
    public DatabaseUser getUserData() {
        return null;
    }

    @Override
    public MainConfig getMainConfig() {
        return config;
    }
}