package be.dieterblancke.bungeeutilisalsx.spigot;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.IBuXApi;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcer;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarColor;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarStyle;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.IBossBar;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.IBridgeManager;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.IEventLoader;
import be.dieterblancke.bungeeutilisalsx.common.api.hubbalancer.IHubBalancer;
import be.dieterblancke.bungeeutilisalsx.common.api.language.ILanguageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.player.IPlayerUtils;
import be.dieterblancke.bungeeutilisalsx.common.chat.ChatProtections;
import be.dieterblancke.bungeeutilisalsx.spigot.bossbar.BossBar;
import be.dieterblancke.bungeeutilisalsx.spigot.user.ConsoleUser;
import be.dieterblancke.bungeeutilisalsx.spigot.utils.LanguageUtils;
import com.google.common.collect.Lists;
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

    private final ILanguageManager languageManager;
    private final IEventLoader eventLoader;

    private final User consoleUser = new ConsoleUser();
    private final List<User> users = Collections.synchronizedList( Lists.newArrayList() );

    @Override
    public IBridgeManager getBridgeManager()
    {
        throw new UnsupportedOperationException( "The BridgeManager is currently only supported in the proxy versions of BungeeUtilisalsX!" );
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
    public IPunishmentHelper getPunishmentExecutor()
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
        users.forEach( user -> user.sendMessage( prefix, message ) );
        getConsoleUser().sendMessage( prefix, message );
    }

    @Override
    public void announce( String prefix, String message, String permission )
    {
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


        langBroadcast( languageManager, message, placeholders );
    }

    @Override
    public void langPermissionBroadcast( String message, String permission, Object... placeholders )
    {
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
