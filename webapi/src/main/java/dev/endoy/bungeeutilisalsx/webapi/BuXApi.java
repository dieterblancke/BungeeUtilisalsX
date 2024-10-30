package dev.endoy.bungeeutilisalsx.webapi;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.IBuXApi;
import dev.endoy.bungeeutilisalsx.common.api.announcer.Announcer;
import dev.endoy.bungeeutilisalsx.common.api.bossbar.BarColor;
import dev.endoy.bungeeutilisalsx.common.api.bossbar.BarStyle;
import dev.endoy.bungeeutilisalsx.common.api.bossbar.IBossBar;
import dev.endoy.bungeeutilisalsx.common.api.event.event.IEventLoader;
import dev.endoy.bungeeutilisalsx.common.api.language.ILanguageManager;
import dev.endoy.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import dev.endoy.bungeeutilisalsx.common.api.serverbalancer.ServerBalancer;
import dev.endoy.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.other.StaffUser;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.HasMessagePlaceholders;
import dev.endoy.bungeeutilisalsx.common.api.utils.player.IPlayerUtils;
import dev.endoy.bungeeutilisalsx.webapi.console.WebApiConsoleUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

@Getter
@RequiredArgsConstructor
public class BuXApi implements IBuXApi
{

    private final ILanguageManager languageManager;
    private final IEventLoader eventLoader;
    private final WebApiConsoleUser consoleUser = new WebApiConsoleUser();

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
    public List<User> getUsers()
    {
        return Collections.emptyList();
    }

    @Override
    public void addUser( User user )
    {
    }

    @Override
    public void removeUser( User user )
    {
    }

    @Override
    public List<User> getUsers( String permission )
    {
        return Collections.emptyList();
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
    public void langBroadcast( String message, HasMessagePlaceholders placeholders )
    {
    }

    @Override
    public void langPermissionBroadcast( String message, String permission, HasMessagePlaceholders placeholders )
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
        throw new UnsupportedOperationException( "Bossbar is not suppored in the web version of BungeeUtilisalsX!" );
    }

    @Override
    public IBossBar createBossBar( final BarColor color,
                                   final BarStyle style,
                                   final float progress,
                                   final Component message )
    {
        throw new UnsupportedOperationException( "Bossbar is not suppored in the web version of BungeeUtilisalsX!" );
    }

    @Override
    public IBossBar createBossBar( final UUID uuid,
                                   final BarColor color,
                                   final BarStyle style,
                                   final float progress,
                                   final Component message )
    {
        throw new UnsupportedOperationException( "Bossbar is not suppored in the web version of BungeeUtilisalsX!" );
    }

    @Override
    public List<StaffUser> getStaffMembers()
    {
        return BuX.getInstance().getStaffMembers();
    }

    @Override
    public ServerBalancer getServerBalancer()
    {
        throw new UnsupportedOperationException( "The ServerBalancer is not suppored in the web version of BungeeUtilisalsX!" );
    }
}
