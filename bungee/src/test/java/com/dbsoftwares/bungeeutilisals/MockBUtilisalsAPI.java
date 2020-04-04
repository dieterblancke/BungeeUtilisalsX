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

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.addon.IAddonManager;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarColor;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarStyle;
import com.dbsoftwares.bungeeutilisals.api.bossbar.IBossBar;
import com.dbsoftwares.bungeeutilisals.api.bridge.IBridgeManager;
import com.dbsoftwares.bungeeutilisals.api.chat.IChatManager;
import com.dbsoftwares.bungeeutilisals.api.event.event.IEventLoader;
import com.dbsoftwares.bungeeutilisals.api.execution.SimpleExecutor;
import com.dbsoftwares.bungeeutilisals.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.api.user.ConsoleUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.player.IPlayerUtils;
import com.dbsoftwares.bungeeutilisals.language.MockLanguageManager;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MockBUtilisalsAPI implements BUAPI
{

    @Override
    public Plugin getPlugin()
    {
        return null;
    }

    @Override
    public IBridgeManager getBridgeManager()
    {
        return null;
    }

    @Override
    public ILanguageManager getLanguageManager()
    {
        return new MockLanguageManager();
    }

    @Override
    public IEventLoader getEventLoader()
    {
        return null;
    }

    @Override
    public Optional<User> getUser( String name )
    {
        return Optional.empty();
    }

    @Override
    public Optional<User> getUser( UUID uuid )
    {
        return Optional.empty();
    }

    @Override
    public Optional<User> getUser( ProxiedPlayer player )
    {
        return Optional.empty();
    }

    @Override
    public List<User> getUsers()
    {
        return Lists.newArrayList();
    }

    @Override
    public List<User> getUsers( String permission )
    {
        return Lists.newArrayList();
    }

    @Override
    public IChatManager getChatManager()
    {
        return null;
    }

    @Override
    public SimpleExecutor getSimpleExecutor()
    {
        return new SimpleExecutor();
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return AbstractStorageManager.getManager().getConnection();
    }

    @Override
    public IPunishmentExecutor getPunishmentExecutor()
    {
        return null;
    }

    @Override
    public ConsoleUser getConsole()
    {
        return null;
    }

    @Override
    public void broadcast( String message )
    {

    }

    @Override
    public void broadcast( String message, String permission )
    {

    }

    @Override
    public void announce( String prefix, String message )
    {

    }

    @Override
    public void announce( String prefix, String message, String permission )
    {

    }

    @Override
    public void langBroadcast( String message, Object... placeholders )
    {

    }

    @Override
    public void langPermissionBroadcast( String message, String permission, Object... placeholders )
    {

    }

    @Override
    public void langBroadcast( ILanguageManager manager, String message, Object... placeholders )
    {

    }

    @Override
    public void langPermissionBroadcast( ILanguageManager manager, String message, String permission, Object... placeholders )
    {

    }

    @Override
    public void pluginLangBroadcast( ILanguageManager manager, String plugin, String message, Object... placeholders )
    {

    }

    @Override
    public void pluginLangPermissionBroadcast( ILanguageManager manager, String plugin, String message, String permission, Object... placeholders )
    {

    }

    @Override
    public Collection<Announcer> getAnnouncers()
    {
        return null;
    }

    @Override
    public IPlayerUtils getPlayerUtils()
    {
        return null;
    }

    @Override
    public AbstractStorageManager getStorageManager()
    {
        return AbstractStorageManager.getManager();
    }

    @Override
    public IAddonManager getAddonManager()
    {
        return null;
    }

    @Override
    public IBossBar createBossBar()
    {
        return null;
    }

    @Override
    public IBossBar createBossBar( BarColor color, BarStyle style, float progress, BaseComponent[] message )
    {
        return null;
    }

    @Override
    public IBossBar createBossBar( UUID uuid, BarColor color, BarStyle style, float progress, BaseComponent[] message )
    {
        return null;
    }
}