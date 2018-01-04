package com.dbsoftwares.bungeeutilisals.api;

/*
 * Created by DBSoftwares on 14 oktober 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

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
import com.zaxxer.hikari.pool.ProxyConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.SQLException;
import java.util.Optional;

public interface BUAPI {

    /**
     * @return The plugin instance of the BungeeUtilisals core.
     */
    Plugin getPlugin();

    /**
     * @return The language manager of BungeeUtilisals.
     */
    ILanguageManager getLanguageManager();

    /**
     * @return The CentrixCore EventLoader allowing you to register EventHandlers.
     */
    IEventLoader getEventLoader();

    /**
     * @param name The user name you want to select on.
     * @return Empty optional if user is not present, User inside if present.
     */
    Optional<User> getUser(String name);

    /**
     * @param player The player you want to select on.
     * @return Empty optional if user is not present, User inside if present.
     */
    Optional<User> getUser(ProxiedPlayer player);

    /**
     * @return A custom ConcurrentList containing all online Users.
     */
    UserCollection getUsers();

    /**
     * @param permission The permission the users must have.
     * @return A custom ConcurrentList containing all online users WITH the given permission.
     */
    UserCollection getUsers(String permission);

    /**
     * @return A new UserCollection instance.
     */
    UserCollection newUserCollection();

    /**
     * @return DatabaseUser class providing utilities to get and set user data from and into the database.
     */
    DatabaseUser getUserData();

    /**
     * @return The BungeeUtilisals chat utility class.
     */
    IChatManager getChatManager();

    /**
     * @return A SimpleExecutor instance.
     */
    SimpleExecutor getSimpleExecutor();

    /**
     * @return A IDebugger instance.
     */
    IDebugger getDebugger();

    /**
     * @return An optional containing a ILoggers instance. Not present if logging is disabled in the config.
     */
    Optional<ILoggers> getLoggers();

    /**
     * @param location The Configuration location you want to request.
     * @return A YamlConfiguration instance from the requested file location.
     */
    YamlConfiguration getConfig(FileLocations location);

    /**
     * @return A new ProxyConnection instance.
     */
    ProxyConnection getConnection() throws SQLException;
}