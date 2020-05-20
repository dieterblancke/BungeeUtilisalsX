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
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarColor;
import com.dbsoftwares.bungeeutilisals.api.bossbar.BarStyle;
import com.dbsoftwares.bungeeutilisals.api.bossbar.IBossBar;
import com.dbsoftwares.bungeeutilisals.api.bridge.BridgeType;
import com.dbsoftwares.bungeeutilisals.api.bridge.IBridgeManager;
import com.dbsoftwares.bungeeutilisals.api.chat.IChatManager;
import com.dbsoftwares.bungeeutilisals.api.data.StaffUser;
import com.dbsoftwares.bungeeutilisals.api.event.event.IEventLoader;
import com.dbsoftwares.bungeeutilisals.api.execution.SimpleExecutor;
import com.dbsoftwares.bungeeutilisals.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisals.api.other.hubbalancer.IHubBalancer;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.api.user.ConsoleUser;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisals.api.utils.player.IPlayerUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.text.LanguageUtils;
import com.dbsoftwares.bungeeutilisals.bossbar.BossBar;
import com.dbsoftwares.bungeeutilisals.bridging.BridgeManager;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.types.UserAction;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.types.UserActionType;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.util.BridgedUserMessage;
import com.dbsoftwares.bungeeutilisals.event.EventLoader;
import com.dbsoftwares.bungeeutilisals.hubbalancer.HubBalancer;
import com.dbsoftwares.bungeeutilisals.language.PluginLanguageManager;
import com.dbsoftwares.bungeeutilisals.manager.ChatManager;
import com.dbsoftwares.bungeeutilisals.punishments.PunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.utils.APIHandler;
import com.dbsoftwares.bungeeutilisals.utils.player.BungeePlayerUtils;
import com.dbsoftwares.bungeeutilisals.utils.player.RedisPlayerUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Getter
public class BUtilisalsAPI implements BUAPI
{

    private final BungeeUtilisals plugin;
    private ConsoleUser console;
    private List<User> users;
    private IChatManager chatManager;
    private IEventLoader eventLoader;
    private ILanguageManager languageManager;
    private SimpleExecutor simpleExecutor;
    private IPunishmentExecutor punishmentExecutor;
    private IPlayerUtils playerUtils;
    private IBridgeManager bridgeManager;
    private IHubBalancer hubBalancer;

    BUtilisalsAPI( BungeeUtilisals plugin )
    {
        APIHandler.registerProvider( this );

        this.plugin = plugin;
        this.console = new ConsoleUser();
        this.users = Collections.synchronizedList( Lists.newArrayList() );
        this.chatManager = new ChatManager();
        this.eventLoader = new EventLoader();
        this.languageManager = new PluginLanguageManager( plugin );
        this.simpleExecutor = new SimpleExecutor();
        this.punishmentExecutor = new PunishmentExecutor();
        this.bridgeManager = new BridgeManager();
        this.bridgeManager.setup();
        this.playerUtils = bridgeManager.useBungeeBridge() ? new RedisPlayerUtils() : new BungeePlayerUtils();

        if ( ConfigFiles.HUBBALANCER.isEnabled() )
        {
            this.hubBalancer = new HubBalancer();
        }
    }

    @Override
    public Collection<Announcer> getAnnouncers()
    {
        return Announcer.getAnnouncers().values();
    }

    @Override
    public AbstractStorageManager getStorageManager()
    {
        return AbstractStorageManager.getManager();
    }

    @Override
    public Optional<User> getUser( String name )
    {
        for ( User user : users )
        {
            if ( user.getName().equalsIgnoreCase( name ) )
            {
                return Optional.of( user );
            }
        }
        return Optional.ofNullable( null );
    }

    @Override
    public Optional<User> getUser( UUID uuid )
    {
        for ( User user : users )
        {
            if ( user.getUuid().equals( uuid ) )
            {
                return Optional.of( user );
            }
        }
        return Optional.ofNullable( null );
    }

    @Override
    public Optional<User> getUser( ProxiedPlayer player )
    {
        return getUser( player.getName() );
    }

    @Override
    public List<User> getUsers()
    {
        return users;
    }

    @Override
    public List<User> getUsers( String permission )
    {
        final List<User> result = Lists.newArrayList();

        for ( User user : users )
        {
            if ( user.getParent().hasPermission( permission ) )
            {
                result.add( user );
            }
        }
        return result;
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        plugin.getDatabaseManagement().getConnection();
        return plugin.getDatabaseManagement().getConnection();
    }

    @Override
    public void broadcast( String message )
    {
        users.forEach( user -> user.sendMessage( message ) );
        getConsole().sendMessage( message );
    }

    @Override
    public void broadcast( String message, String permission )
    {
        if ( bridgeManager.useBungeeBridge() )
        {
            final Map<String, Object> data = Maps.newHashMap();

            data.put( "PERMISSION", Collections.singletonList( permission ) );

            bridgeManager.getBungeeBridge().sendTargetedMessage(
                    BridgeType.BUNGEE_BUNGEE,
                    null,
                    Collections.singletonList( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) ),
                    "USER",
                    new UserAction(
                            null,
                            UserActionType.MESSAGE,
                            new BridgedUserMessage(
                                    false,
                                    message,
                                    data
                            )
                    )
            );
        }

        for ( User user : users )
        {
            if ( user.getParent().hasPermission( permission ) )
            {
                user.sendMessage( message );
            }
        }
        getConsole().sendMessage( message );
    }

    @Override
    public void announce( String prefix, String message )
    {
        if ( bridgeManager.useBungeeBridge() )
        {
            bridgeManager.getBungeeBridge().sendTargetedMessage(
                    BridgeType.BUNGEE_BUNGEE,
                    null,
                    Collections.singletonList( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) ),
                    "USER",
                    new UserAction(
                            null,
                            UserActionType.MESSAGE,
                            new BridgedUserMessage(
                                    false,
                                    prefix + message,
                                    Maps.newHashMap()
                            )
                    )
            );
        }

        users.forEach( user -> user.sendMessage( prefix, message ) );
        getConsole().sendMessage( prefix, message );
    }

    @Override
    public void announce( String prefix, String message, String permission )
    {
        if ( bridgeManager.useBungeeBridge() )
        {
            final Map<String, Object> data = Maps.newHashMap();

            data.put( "PERMISSION", Collections.singletonList( permission ) );

            bridgeManager.getBungeeBridge().sendTargetedMessage(
                    BridgeType.BUNGEE_BUNGEE,
                    null,
                    Collections.singletonList( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) ),
                    "USER",
                    new UserAction(
                            null,
                            UserActionType.MESSAGE,
                            new BridgedUserMessage(
                                    false,
                                    prefix + message,
                                    data
                            )
                    )
            );
        }

        for ( User user : users )
        {
            if ( user.getParent().hasPermission( permission ) )
            {
                user.sendMessage( prefix, message );
            }
        }
        getConsole().sendMessage( prefix, message );
    }

    @Override
    public void langBroadcast( String message, Object... placeholders )
    {
        if ( bridgeManager.useBungeeBridge() )
        {
            bridgeManager.getBungeeBridge().sendTargetedMessage(
                    BridgeType.BUNGEE_BUNGEE,
                    null,
                    Collections.singletonList( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) ),
                    "USER",
                    new UserAction(
                            null,
                            UserActionType.MESSAGE,
                            new BridgedUserMessage(
                                    true,
                                    message,
                                    Maps.newHashMap(),
                                    placeholders
                            )
                    )
            );
        }

        langBroadcast( languageManager, message, placeholders );
    }

    @Override
    public void langPermissionBroadcast( String message, String permission, Object... placeholders )
    {
        if ( bridgeManager.useBungeeBridge() )
        {
            final Map<String, Object> data = Maps.newHashMap();

            data.put( "PERMISSION", Collections.singletonList( permission ) );

            bridgeManager.getBungeeBridge().sendTargetedMessage(
                    BridgeType.BUNGEE_BUNGEE,
                    null,
                    Collections.singletonList( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) ),
                    "USER",
                    new UserAction(
                            null,
                            UserActionType.MESSAGE,
                            new BridgedUserMessage(
                                    true,
                                    message,
                                    data,
                                    placeholders
                            )
                    )
            );
        }

        langPermissionBroadcast( languageManager, message, permission, placeholders );
    }

    @Override
    public void langBroadcast( ILanguageManager manager, String message, Object... placeholders )
    {
        for ( User user : users )
        {
            LanguageUtils.sendLangMessage( manager, plugin.getDescription().getName(), user, message, placeholders );
        }
        LanguageUtils.sendLangMessage( manager, plugin.getDescription().getName(), getConsole(), message, placeholders );
    }

    @Override
    public void langPermissionBroadcast( ILanguageManager manager, String message, String permission, Object... placeholders )
    {
        for ( User user : users )
        {
            if ( user.getParent().hasPermission( permission ) || user.getParent().hasPermission( "bungeeutilisals.*" ) )
            {
                LanguageUtils.sendLangMessage( manager, plugin.getDescription().getName(), user, message, placeholders );
            }
        }
        LanguageUtils.sendLangMessage( manager, plugin.getDescription().getName(), getConsole(), message, placeholders );
    }

    @Override
    public IBossBar createBossBar()
    {
        return new BossBar();
    }

    @Override
    public IBossBar createBossBar( BarColor color, BarStyle style, float progress, BaseComponent[] message )
    {
        return createBossBar( UUID.randomUUID(), color, style, progress, message );
    }

    @Override
    public IBossBar createBossBar( UUID uuid, BarColor color, BarStyle style, float progress, BaseComponent[] message )
    {
        return new BossBar( uuid, color, style, progress, message );
    }

    @Override
    public List<StaffUser> getStaffMembers()
    {
        return BungeeUtilisals.getInstance().getStaffMembers();
    }
}
