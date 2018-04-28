package com.dbsoftwares.bungeeutilisals.api;

/*
 * Created by DBSoftwares on 14 oktober 2017
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.bossbar.BarColor;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarStyle;
import com.dbsoftwares.bungeeutilisals.api.bossbar.IBossBar;
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
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

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
     * @param location The Configuration location you want to request.
     * @return A YamlConfiguration instance from the requested file location.
     */
    IConfiguration getConfig(FileLocation location);

    /**
     * @return A new ProxyConnection instance.
     * @throws SQLException When an error occurs trying to setup the connection.
     */
    Connection getConnection() throws SQLException;

    /**
     * @return The BungeeUtilisals punishment API.
     */
    IPunishmentExecutor getPunishmentExecutor();

    /**
     * @return ConsoleUser instance.
     */
    ConsoleUser getConsole();

    /**
     * Broadcasts a message with the BungeeUtilisals prefix.
     *
     * @param message The message to be broadcasted.
     */
    void broadcast(String message);

    /**
     * Broadcastas a message with the BungeeUtilisals prefix to the people with the given permission.
     *
     * @param message    The message to be broadcasted.
     * @param permission The permission the user must have to receive the message.
     */
    void broadcast(String message, String permission);

    /**
     * Broadcastas a message with a given prefix to the people with the given permission.
     *
     * @param prefix  The prefix you want.
     * @param message The message to be broadcasted.
     */
    void announce(String prefix, String message);

    /**
     * Broadcastas a message with a given prefix to the people with the given permission.
     *
     * @param prefix     The prefix you want.
     * @param message    The message to be broadcasted.
     * @param permission The permission the user must have to receive the message.
     */
    void announce(String prefix, String message, String permission);

    /**
     * Broadcasts a message with the BungeeUtilisals prefix.
     *
     * @param message      The location (in the languages file) of the message to be broadcasted.
     * @param placeholders PlaceHolders + their replacements
     */
    void langBroadcast(String message, Object... placeholders);

    /**
     * Broadcastas a message with the BungeeUtilisals prefix to the people with the given permission.
     *
     * @param message      The location (in the languages file) of the message to be broadcasted.
     * @param permission   The permission the user must have to receive the message.
     * @param placeholders PlaceHolders + their replacements
     */
    void langBroadcast(String message, String permission, Object... placeholders);

    /**
     * @return a new BossBar instance.
     */
    IBossBar createBossBar();

    /**
     * Deprecated, please use {@link #createBossBar(BarColor, BarStyle, float, BaseComponent[])} instead.
     *
     * @param color    Color of the BossBar.
     * @param style    Amount of divisions in the BossBar.
     * @param progress Progress of the BossBar, between 0.0 and 1.0.
     * @param message  The display message of the BossBar (String)
     * @return a new BossBar instance.
     */
    @Deprecated
    IBossBar createBossBar(BarColor color, BarStyle style, float progress, String message);

    /**
     * Deprecated, please use {@link #createBossBar(UUID, BarColor, BarStyle, float, BaseComponent[])} instead.
     *
     * @param uuid     UUID for the BossBar, should be unique!
     * @param color    Color of the BossBar.
     * @param style    Amount of divisions in the BossBar.
     * @param progress Progress of the BossBar, between 0.0 and 1.0.
     * @param message  The display message of the BossBar (String)
     * @return a new BossBar instance.
     */
    @Deprecated
    IBossBar createBossBar(UUID uuid, BarColor color, BarStyle style, float progress, String message);

    /**
     * @param color    Color of the BossBar.
     * @param style    Amount of divisions in the BossBar.
     * @param progress Progress of the BossBar, between 0.0 and 1.0.
     * @param message  The display message of the BossBar (BaseComponent)
     * @return a new BossBar instance.
     */
    IBossBar createBossBar(BarColor color, BarStyle style, float progress, BaseComponent[] message);

    /**
     * @param uuid     UUID for the BossBar, should be unique!
     * @param color    Color of the BossBar.
     * @param style    Amount of divisions in the BossBar.
     * @param progress Progress of the BossBar, between 0.0 and 1.0.
     * @param message  The display message of the BossBar (BaseComponent)
     * @return a new BossBar instance.
     */
    IBossBar createBossBar(UUID uuid, BarColor color, BarStyle style, float progress, BaseComponent[] message);
}