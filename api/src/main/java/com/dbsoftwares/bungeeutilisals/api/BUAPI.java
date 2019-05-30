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

package com.dbsoftwares.bungeeutilisals.api;

import com.dbsoftwares.bungeeutilisals.api.addon.IAddonManager;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarColor;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarStyle;
import com.dbsoftwares.bungeeutilisals.api.bossbar.IBossBar;
import com.dbsoftwares.bungeeutilisals.api.chat.IChatManager;
import com.dbsoftwares.bungeeutilisals.api.event.event.IEventLoader;
import com.dbsoftwares.bungeeutilisals.api.execution.SimpleExecutor;
import com.dbsoftwares.bungeeutilisals.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.api.user.ConsoleUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.player.IPlayerUtils;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BUAPI {

    /**
     * @return The plugin instance of the BungeeUtilisals core.
     */
    Plugin getPlugin();

    /**
     * @return The language chat of BungeeUtilisals.
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
     * @param uuid The user uuid you want to select on.
     * @return Empty optional if user is not present, User inside if present.
     */
    Optional<User> getUser(UUID uuid);

    /**
     * @param player The player you want to select on.
     * @return Empty optional if user is not present, User inside if present.
     */
    Optional<User> getUser(ProxiedPlayer player);

    /**
     * @return A list containing all online Users.
     */
    List<User> getUsers();

    /**
     * @param permission The permission the users must have.
     * @return A list containing all online users WITH the given permission.
     */
    List<User> getUsers(String permission);

    /**
     * @return The BungeeUtilisals chat utility class.
     */
    IChatManager getChatManager();

    /**
     * @return A SimpleExecutor instance.
     */
    SimpleExecutor getSimpleExecutor();

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
     * Broadcasts a message with the BungeeUtilisals prefix to the people with the given permission.
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
    void langPermissionBroadcast(String message, String permission, Object... placeholders);

    /**
     * Broadcasts a message with the BungeeUtilisals prefix.
     *
     * @param manager      The languagemanager instance to be used.
     * @param message      The location (in the languages file) of the message to be broadcasted.
     * @param placeholders PlaceHolders + their replacements
     */
    void langBroadcast(ILanguageManager manager, String message, Object... placeholders);

    /**
     * Broadcastas a message with the BungeeUtilisals prefix to the people with the given permission.
     *
     * @param manager      The languagemanager instance to be used.
     * @param message      The location (in the languages file) of the message to be broadcasted.
     * @param permission   The permission the user must have to receive the message.
     * @param placeholders PlaceHolders + their replacements
     */
    void langPermissionBroadcast(ILanguageManager manager, String message, String permission, Object... placeholders);

    /**
     * Broadcasts a message with the BungeeUtilisals prefix.
     *
     * @param manager      The languagemanager instance to be used.
     * @param plugin       The plugin name to be used.
     * @param message      The location (in the languages file) of the message to be broadcasted.
     * @param placeholders PlaceHolders + their replacements
     */
    void pluginLangBroadcast(ILanguageManager manager, String plugin, String message, Object... placeholders);

    /**
     * Broadcastas a message with the BungeeUtilisals prefix to the people with the given permission.
     *
     * @param manager      The languagemanager instance to be used.
     * @param plugin       The plugin name to be used.
     * @param message      The location (in the languages file) of the message to be broadcasted.
     * @param permission   The permission the user must have to receive the message.
     * @param placeholders PlaceHolders + their replacements
     */
    void pluginLangPermissionBroadcast(ILanguageManager manager, String plugin, String message, String permission, Object... placeholders);


    /**
     * @return a list of all announcers.
     */
    Collection<Announcer> getAnnouncers();

    /**
     * @return an IPlayerUtils instance (BungeePlayerUtils or RedisPlayerUtils)
     */
    IPlayerUtils getPlayerUtils();

    /**
     * @return the storage chat used.
     */
    AbstractStorageManager getStorageManager();

    /**
     * @return the addon chat that is being used
     */
    IAddonManager getAddonManager();

    /**
     * @return a new BossBar instance.
     */
    IBossBar createBossBar();

    /**
     * @param color    Color of the BossBar.
     * @param style    Amount of divisions in the BossBar.
     * @param progress Progress of the BossBar, between 0.0 and 1.0.
     * @param message  The display message of the BossBar
     * @return a new BossBar instance.
     */
    IBossBar createBossBar(BarColor color, BarStyle style, float progress, BaseComponent[] message);

    /**
     * @param uuid     UUID for the BossBar, should be unique!
     * @param color    Color of the BossBar.
     * @param style    Amount of divisions in the BossBar.
     * @param progress Progress of the BossBar, between 0.0 and 1.0.
     * @param message  The display message of the BossBar
     * @return a new BossBar instance.
     */
    IBossBar createBossBar(UUID uuid, BarColor color, BarStyle style, float progress, BaseComponent[] message);
}