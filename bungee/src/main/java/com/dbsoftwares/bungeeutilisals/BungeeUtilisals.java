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

import com.dbsoftwares.bungeeutilisalsx.common.announcers.*;
import com.dbsoftwares.bungeeutilisalsx.common.api.announcer.AnnouncementType;
import com.dbsoftwares.bungeeutilisalsx.common.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.api.data.StaffUser;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.event.EventHandler;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.event.IEventLoader;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.network.NetworkStaffJoinEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.network.NetworkStaffLeaveEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.punishment.UserPunishmentFinishEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.user.*;
import com.dbsoftwares.bungeeutilisalsx.common.api.language.Language;
import com.dbsoftwares.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisalsx.common.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisalsx.common.storage.AbstractStorageManager.StorageType;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.common.utils.reflection.JarClassLoader;
import com.dbsoftwares.bungeeutilisalsx.common.utils.reflection.ReflectionUtils;
import com.dbsoftwares.bungeeutilisals.executors.*;
import com.dbsoftwares.bungeeutilisalsx.common.library.Library;
import com.dbsoftwares.bungeeutilisalsx.common.library.StandardLibrary;
import com.dbsoftwares.bungeeutilisals.listeners.MotdPingListener;
import com.dbsoftwares.bungeeutilisals.listeners.PunishmentListener;
import com.dbsoftwares.bungeeutilisals.listeners.UserChatListener;
import com.dbsoftwares.bungeeutilisals.listeners.UserConnectionListener;
import com.dbsoftwares.bungeeutilisals.manager.CommandManager;
import com.dbsoftwares.bungeeutilisals.placeholders.DefaultPlaceHolders;
import com.dbsoftwares.bungeeutilisals.placeholders.InputPlaceHolders;
import com.dbsoftwares.bungeeutilisals.placeholders.UserPlaceHolderPack;
import com.dbsoftwares.bungeeutilisals.placeholders.javascript.JavaScriptPlaceHolder;
import com.dbsoftwares.bungeeutilisals.placeholders.javascript.Script;
import com.dbsoftwares.bungeeutilisals.runnables.UserMessageQueueRunnable;
import com.dbsoftwares.bungeeutilisals.updater.Updatable;
import com.dbsoftwares.bungeeutilisals.updater.Update;
import com.dbsoftwares.bungeeutilisals.updater.Updater;
import com.dbsoftwares.bungeeutilisals.utils.EncryptionUtils;
import com.dbsoftwares.configuration.api.FileStorageType;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Lists;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.List;

@Updatable(url = "https://api.dbsoftwares.eu/plugin/BungeeUtilisals/")
public class BungeeUtilisals extends Plugin
{

    @Getter
    private static BungeeUtilisals instance;

    @Getter
    private static BUtilisalsAPI api;

    @Getter
    private JarClassLoader jarClassLoader;

    @Getter
    private AbstractStorageManager databaseManagement;

    @Getter
    private CommandManager commandManager;

    @Getter
    private final List<Script> scripts = Lists.newArrayList();

    @Getter
    private final List<StaffUser> staffMembers = Lists.newArrayList();

    @Override
    public void onEnable()
    {
        // Register executors & listeners
        ProxyServer.getInstance().getPluginManager().registerListener( this, new UserConnectionListener() );
        ProxyServer.getInstance().getPluginManager().registerListener( this, new UserChatListener() );

        if ( ConfigFiles.MOTD.isEnabled() )
        {
            ProxyServer.getInstance().getPluginManager().registerListener( this, new MotdPingListener() );
        }

        final IEventLoader loader = api.getEventLoader();

        final UserExecutor userExecutor = new UserExecutor();
        loader.register( UserLoadEvent.class, userExecutor );
        loader.register( UserUnloadEvent.class, userExecutor );
        loader.register( UserChatEvent.class, new UserChatExecutor() );
        loader.register( UserChatEvent.class, new StaffCharChatExecutor() );

        final StaffNetworkExecutor staffNetworkExecutor = new StaffNetworkExecutor();
        loader.register( NetworkStaffJoinEvent.class, staffNetworkExecutor );
        loader.register( NetworkStaffLeaveEvent.class, staffNetworkExecutor );

        final SpyEventExecutor spyEventExecutor = new SpyEventExecutor();
        loader.register( UserPrivateMessageEvent.class, spyEventExecutor );
        loader.register( UserCommandEvent.class, spyEventExecutor );

        // Loading Punishment system
        if ( ConfigFiles.PUNISHMENTS.isEnabled() )
        {
            ProxyServer.getInstance().getPluginManager().registerListener( this, new PunishmentListener() );

            loader.register( UserPunishmentFinishEvent.class, new UserPunishExecutor() );

            MuteCheckExecutor muteCheckExecutor = new MuteCheckExecutor();
            loader.register( UserChatEvent.class, muteCheckExecutor );
            loader.register( UserCommandEvent.class, muteCheckExecutor );
        }

        if ( ConfigFiles.FRIENDS_CONFIG.isEnabled() )
        {
            final FriendsExecutor executor = new FriendsExecutor();
            loader.register( UserLoadEvent.class, executor );
            loader.register( UserUnloadEvent.class, executor );
            ProxyServer.getInstance().getPluginManager().registerListener( this, executor );
        }
    }
}