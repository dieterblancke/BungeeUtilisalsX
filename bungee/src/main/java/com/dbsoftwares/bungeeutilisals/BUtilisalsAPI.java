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

import com.dbsoftwares.bungeeutilisals.addon.AddonManager;
import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.addon.IAddonManager;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.api.chat.IChatManager;
import com.dbsoftwares.bungeeutilisals.api.event.event.IEventLoader;
import com.dbsoftwares.bungeeutilisals.api.execution.SimpleExecutor;
import com.dbsoftwares.bungeeutilisals.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.api.user.ConsoleUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.UserCollection;
import com.dbsoftwares.bungeeutilisals.api.utils.LanguageUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.player.IPlayerUtils;
import com.dbsoftwares.bungeeutilisals.event.EventLoader;
import com.dbsoftwares.bungeeutilisals.language.PluginLanguageManager;
import com.dbsoftwares.bungeeutilisals.manager.ChatManager;
import com.dbsoftwares.bungeeutilisals.punishments.PunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.user.UserList;
import com.dbsoftwares.bungeeutilisals.utils.APIHandler;
import com.dbsoftwares.bungeeutilisals.utils.player.BungeePlayerUtils;
import com.dbsoftwares.bungeeutilisals.utils.player.RedisPlayerUtils;
import com.dbsoftwares.bungeeutilisals.utils.redis.Channels;
import com.dbsoftwares.bungeeutilisals.utils.redis.channeldata.APIAnnouncement;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Optional;

public class BUtilisalsAPI implements BUAPI {

    private final BungeeUtilisals instance;
    private ConsoleUser console;
    private UserCollection users;
    private IChatManager chatManager;
    private IEventLoader eventLoader;
    private ILanguageManager languageManager;
    private SimpleExecutor simpleExecutor;
    private IPunishmentExecutor punishmentExecutor;
    private IPlayerUtils playerUtils;
    private IAddonManager addonManager;

    public BUtilisalsAPI(BungeeUtilisals instance) {
        APIHandler.registerProvider(this);

        this.instance = instance;
        this.console = new ConsoleUser();
        this.users = new UserList();
        this.chatManager = new ChatManager();
        this.eventLoader = new EventLoader();
        this.languageManager = new PluginLanguageManager(instance);
        this.simpleExecutor = new SimpleExecutor();
        this.punishmentExecutor = new PunishmentExecutor();
        this.playerUtils = getConfig(FileLocation.CONFIG).getBoolean("redis") ? new RedisPlayerUtils() : new BungeePlayerUtils();
        this.addonManager = new AddonManager();
    }

    @Override
    public Collection<Announcer> getAnnouncers() {
        return Announcer.getAnnouncers().values();
    }

    @Override
    public IPlayerUtils getPlayerUtils() {
        return playerUtils;
    }

    @Override
    public AbstractStorageManager getStorageManager() {
        return BungeeUtilisals.getInstance().getDatabaseManagement();
    }

    @Override
    public IAddonManager getAddonManager() {
        return addonManager;
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
    public IChatManager getChatManager() {
        return chatManager;
    }

    @Override
    public SimpleExecutor getSimpleExecutor() {
        return simpleExecutor;
    }

    @Override
    public IConfiguration getConfig(FileLocation location) {
        return location.getConfiguration();
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
        if (BungeeUtilisals.getInstance().getConfig().getBoolean("redis")) {
            BungeeUtilisals.getInstance().getRedisMessenger().sendChannelMessage(
                    Channels.API_BROADCAST, new APIAnnouncement(true, null, null, message, null, false)
            );
            return;
        }
        users.forEach(user -> user.sendMessage(message));
        getConsole().sendMessage(message);
    }

    @Override
    public void broadcast(String message, String permission) {
        if (BungeeUtilisals.getInstance().getConfig().getBoolean("redis")) {
            BungeeUtilisals.getInstance().getRedisMessenger().sendChannelMessage(
                    Channels.API_BROADCAST, new APIAnnouncement(true, null, null, message, permission, false)
            );
            return;
        }
        users.stream().filter(user -> user.getParent().hasPermission(permission)).forEach(user -> user.sendMessage(message));
        getConsole().sendMessage(message);
    }

    @Override
    public void announce(String prefix, String message) {
        if (BungeeUtilisals.getInstance().getConfig().getBoolean("redis")) {
            BungeeUtilisals.getInstance().getRedisMessenger().sendChannelMessage(
                    Channels.API_BROADCAST, new APIAnnouncement(true, null, prefix, message, null, false)
            );
            return;
        }
        users.forEach(user -> user.sendMessage(prefix, message));
        getConsole().sendMessage(prefix, message);
    }

    @Override
    public void announce(String prefix, String message, String permission) {
        if (BungeeUtilisals.getInstance().getConfig().getBoolean("redis")) {
            BungeeUtilisals.getInstance().getRedisMessenger().sendChannelMessage(
                    Channels.API_BROADCAST, new APIAnnouncement(true, null, prefix, message, permission, false)
            );
            return;
        }
        users.stream().filter(user -> user.getParent().hasPermission(permission)).forEach(user -> user.sendMessage(prefix, message));
        getConsole().sendMessage(prefix, message);
    }

    @Override
    public void langBroadcast(String message, Object... placeholders) {
        langBroadcast(languageManager, message, placeholders);
    }

    @Override
    public void langPermissionBroadcast(String message, String permission, Object... placeholders) {
        langPermissionBroadcast(languageManager, message, permission, placeholders);
    }

    @Override
    public void langBroadcast(ILanguageManager manager, String message, Object... placeholders) {
        if (BungeeUtilisals.getInstance().getConfig().getBoolean("redis")) {
            BungeeUtilisals.getInstance().getRedisMessenger().sendChannelMessage(
                    Channels.API_BROADCAST, new APIAnnouncement(manager instanceof PluginLanguageManager, instance.getDescription().getName(), null, message, null, true, placeholders)
            );
            return;
        }
        users.forEach(user -> LanguageUtils.sendLangMessage(manager, instance.getDescription().getName(), user, message, placeholders));
        LanguageUtils.sendLangMessage(manager, instance.getDescription().getName(), getConsole(), message, placeholders);
    }

    @Override
    public void langPermissionBroadcast(ILanguageManager manager, String message, String permission, Object... placeholders) {
        if (BungeeUtilisals.getInstance().getConfig().getBoolean("redis")) {
            BungeeUtilisals.getInstance().getRedisMessenger().sendChannelMessage(
                    Channels.API_BROADCAST, new APIAnnouncement(manager instanceof PluginLanguageManager, instance.getDescription().getName(), null, message, permission, true, placeholders)
            );
            return;
        }
        users.stream().filter(user -> user.getParent().hasPermission(permission)).forEach(user -> LanguageUtils.sendLangMessage(manager, instance.getDescription().getName(), user, message, placeholders));
        LanguageUtils.sendLangMessage(manager, instance.getDescription().getName(), getConsole(), message, placeholders);
    }

    @Override
    public void pluginLangBroadcast(ILanguageManager manager, String plugin, String message, Object... placeholders) {
        if (BungeeUtilisals.getInstance().getConfig().getBoolean("redis")) {
            BungeeUtilisals.getInstance().getRedisMessenger().sendChannelMessage(
                    Channels.API_BROADCAST, new APIAnnouncement(manager instanceof PluginLanguageManager, plugin, null, message, null, true, placeholders)
            );
            return;
        }
        users.forEach(user -> LanguageUtils.sendLangMessage(manager, plugin, user, message, placeholders));
        LanguageUtils.sendLangMessage(manager, plugin, getConsole(), message, placeholders);
    }

    @Override
    public void pluginLangPermissionBroadcast(ILanguageManager manager, String plugin, String message, String permission, Object... placeholders) {
        if (BungeeUtilisals.getInstance().getConfig().getBoolean("redis")) {
            BungeeUtilisals.getInstance().getRedisMessenger().sendChannelMessage(
                    Channels.API_BROADCAST, new APIAnnouncement(manager instanceof PluginLanguageManager, plugin, null, message, permission, true, placeholders)
            );
            return;
        }
        users.stream().filter(user -> user.getParent().hasPermission(permission)).forEach(user -> LanguageUtils.sendLangMessage(manager, plugin, user, message, placeholders));
        LanguageUtils.sendLangMessage(manager, plugin, getConsole(), message, placeholders);
    }
}