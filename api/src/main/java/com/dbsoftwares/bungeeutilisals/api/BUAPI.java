package com.dbsoftwares.bungeeutilisals.api;

/*
 * Created by DBSoftwares on 14 oktober 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.event.IEventLoader;
import com.dbsoftwares.bungeeutilisals.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisals.api.manager.IChatManager;
import com.dbsoftwares.bungeeutilisals.api.user.DatabaseUser;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.dbsoftwares.bungeeutilisals.api.user.UserCollection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import java.util.Optional;

public interface BUAPI {

    /**
     * @return The plugin instance of the BungeeUtilisals core.
     */
    Plugin getPlugin();

    /**
     * @return Returns the Prefix that BungeeUtilisals will be using.
     */
    String getPrefix();

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
}