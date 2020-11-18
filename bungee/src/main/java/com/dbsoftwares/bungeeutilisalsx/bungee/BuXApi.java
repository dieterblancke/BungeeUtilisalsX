package com.dbsoftwares.bungeeutilisalsx.bungee;

import com.dbsoftwares.bungeeutilisalsx.bungee.bossbar.BossBar;
import com.dbsoftwares.bungeeutilisalsx.bungee.user.ConsoleUser;
import com.dbsoftwares.bungeeutilisalsx.bungee.utils.LanguageUtils;
import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.IBuXApi;
import com.dbsoftwares.bungeeutilisalsx.common.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisalsx.common.api.bossbar.BarColor;
import com.dbsoftwares.bungeeutilisalsx.common.api.bossbar.BarStyle;
import com.dbsoftwares.bungeeutilisalsx.common.api.bossbar.IBossBar;
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.BridgeType;
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.IBridgeManager;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.event.IEventLoader;
import com.dbsoftwares.bungeeutilisalsx.common.api.hubbalancer.IHubBalancer;
import com.dbsoftwares.bungeeutilisalsx.common.api.language.ILanguageManager;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.other.StaffUser;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.player.IPlayerUtils;
import com.dbsoftwares.bungeeutilisalsx.common.bridge.types.UserAction;
import com.dbsoftwares.bungeeutilisalsx.common.bridge.types.UserActionType;
import com.dbsoftwares.bungeeutilisalsx.common.bridge.util.BridgedUserMessage;
import com.dbsoftwares.bungeeutilisalsx.common.manager.ChatManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Logger;

@Getter
@RequiredArgsConstructor
public class BuXApi implements IBuXApi
{

    private final IBridgeManager bridgeManager;
    private final ILanguageManager languageManager;
    private final IEventLoader eventLoader;
    private final IHubBalancer hubBalancer;
    private final IPunishmentExecutor punishmentExecutor;
    private final IPlayerUtils playerUtils;
    private final ChatManager chatManager;

    private final User consoleUser = new ConsoleUser();

    private final List<User> users = Collections.synchronizedList( Lists.newArrayList() );

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
            if ( user.hasPermission( permission ) )
            {
                result.add( user );
            }
        }
        return result;
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return BuX.getInstance().getAbstractStorageManager().getConnection();
    }

    @Override
    public void broadcast( String message )
    {
        users.forEach( user -> user.sendMessage( message ) );
        getConsoleUser().sendMessage( message );
    }

    @Override
    public void broadcast( String message, String permission )
    {
        if ( bridgeManager.useBridging() )
        {
            final Map<String, Object> data = Maps.newHashMap();

            data.put( "PERMISSION", Lists.newArrayList( permission ) );

            bridgeManager.getBridge().sendTargetedMessage(
                    BridgeType.BUNGEE_BUNGEE,
                    null,
                    Lists.newArrayList( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) ),
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
            if ( user.hasPermission( permission ) )
            {
                user.sendMessage( message );
            }
        }
        getConsoleUser().sendMessage( message );
    }

    @Override
    public void announce( String prefix, String message )
    {
        if ( bridgeManager.useBridging() )
        {
            bridgeManager.getBridge().sendTargetedMessage(
                    BridgeType.BUNGEE_BUNGEE,
                    null,
                    Lists.newArrayList( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) ),
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
        getConsoleUser().sendMessage( prefix, message );
    }

    @Override
    public void announce( String prefix, String message, String permission )
    {
        if ( bridgeManager.useBridging() )
        {
            final Map<String, Object> data = Maps.newHashMap();

            data.put( "PERMISSION", Lists.newArrayList( permission ) );

            bridgeManager.getBridge().sendTargetedMessage(
                    BridgeType.BUNGEE_BUNGEE,
                    null,
                    Lists.newArrayList( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) ),
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
            if ( user.hasPermission( permission ) )
            {
                user.sendMessage( prefix, message );
            }
        }
        getConsoleUser().sendMessage( prefix, message );
    }

    @Override
    public void langBroadcast( String message, Object... placeholders )
    {
        if ( bridgeManager.useBridging() )
        {
            bridgeManager.getBridge().sendTargetedMessage(
                    BridgeType.BUNGEE_BUNGEE,
                    null,
                    Lists.newArrayList( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) ),
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
        if ( bridgeManager.useBridging() )
        {
            final Map<String, Object> data = Maps.newHashMap();

            data.put( "PERMISSION", Lists.newArrayList( permission ) );

            bridgeManager.getBridge().sendTargetedMessage(
                    BridgeType.BUNGEE_BUNGEE,
                    null,
                    Lists.newArrayList( ConfigFiles.CONFIG.getConfig().getString( "bridging.name" ) ),
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
            LanguageUtils.sendLangMessage( manager, BuX.getInstance().getName(), user, message, placeholders );
        }
        LanguageUtils.sendLangMessage( manager, BuX.getInstance().getName(), getConsoleUser(), message, placeholders );
    }

    @Override
    public void langPermissionBroadcast( ILanguageManager manager, String message, String permission, Object... placeholders )
    {
        for ( User user : users )
        {
            if ( user.hasPermission( permission ) || user.hasPermission( "bungeeutilisals.*" ) )
            {
                LanguageUtils.sendLangMessage( manager, BuX.getInstance().getName(), user, message, placeholders );
            }
        }
        LanguageUtils.sendLangMessage( manager, BuX.getInstance().getName(), getConsoleUser(), message, placeholders );
    }

    @Override
    public Collection<Announcer> getAnnouncers()
    {
        return null;
    }

    @Override
    public AbstractStorageManager getStorageManager()
    {
        return BuX.getInstance().getAbstractStorageManager();
    }

    @Override
    public IBossBar createBossBar()
    {
        return new BossBar();
    }

    @Override
    public IBossBar createBossBar( final BarColor color,
                                   final BarStyle style,
                                   final float progress,
                                   final BaseComponent[] message )
    {
        return createBossBar( UUID.randomUUID(), color, style, progress, message );
    }

    @Override
    public IBossBar createBossBar( final UUID uuid,
                                   final BarColor color,
                                   final BarStyle style,
                                   final float progress,
                                   final BaseComponent[] message )
    {
        return new BossBar( uuid, color, style, progress, message );
    }

    @Override
    public List<StaffUser> getStaffMembers()
    {
        return BuX.getInstance().getStaffMembers();
    }
}
