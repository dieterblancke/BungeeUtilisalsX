package be.dieterblancke.bungeeutilisalsx.spigot;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.IBuXApi;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcer;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarColor;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarStyle;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.IBossBar;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.BridgeType;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.IBridgeManager;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.IEventLoader;
import be.dieterblancke.bungeeutilisalsx.common.api.hubbalancer.IHubBalancer;
import be.dieterblancke.bungeeutilisalsx.common.api.language.ILanguageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.IPunishmentExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.player.IPlayerUtils;
import be.dieterblancke.bungeeutilisalsx.common.bridge.types.UserAction;
import be.dieterblancke.bungeeutilisalsx.common.bridge.types.UserActionType;
import be.dieterblancke.bungeeutilisalsx.common.bridge.util.BridgedUserMessage;
import be.dieterblancke.bungeeutilisalsx.common.manager.ChatManager;
import be.dieterblancke.bungeeutilisalsx.spigot.user.ConsoleUser;
import be.dieterblancke.bungeeutilisalsx.spigot.bossbar.BossBar;
import be.dieterblancke.bungeeutilisalsx.spigot.utils.LanguageUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.BaseComponent;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Getter
@RequiredArgsConstructor
public class BuXApi implements IBuXApi
{

    private final IBridgeManager bridgeManager;
    private final ILanguageManager languageManager;
    private final IEventLoader eventLoader;

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
    public ChatManager getChatManager()
    {
        throw new UnsupportedOperationException( "The ChatManager is currently only supported in the proxy versions of BungeeUtilisalsX!" );
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return BuX.getInstance().getAbstractStorageManager().getConnection();
    }

    @Override
    public IPunishmentExecutor getPunishmentExecutor()
    {
        throw new UnsupportedOperationException( "The PunishmentExecutor is currently only supported in the proxy versions of BungeeUtilisalsX!" );
    }

    @Override
    public IHubBalancer getHubBalancer()
    {
        throw new UnsupportedOperationException( "The HubBalancer is currently only supported in the proxy versions of BungeeUtilisalsX!" );
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
    public IPlayerUtils getPlayerUtils()
    {
        throw new UnsupportedOperationException( "The PlayerUtils is currently only supported in the proxy versions of BungeeUtilisalsX!" );
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
