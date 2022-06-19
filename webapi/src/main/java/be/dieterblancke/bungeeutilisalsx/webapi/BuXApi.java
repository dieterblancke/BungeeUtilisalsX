package be.dieterblancke.bungeeutilisalsx.webapi;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.IBuXApi;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcer;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarColor;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarStyle;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.IBossBar;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.IEventLoader;
import be.dieterblancke.bungeeutilisalsx.common.api.hubbalancer.IHubBalancer;
import be.dieterblancke.bungeeutilisalsx.common.api.language.ILanguageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.player.IPlayerUtils;
import be.dieterblancke.bungeeutilisalsx.webapi.console.WebApiConsoleUser;
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
    public IHubBalancer getHubBalancer()
    {
        throw new UnsupportedOperationException( "The HubBalancer is currently only supported in the proxy versions of BungeeUtilisalsX!" );
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
}
