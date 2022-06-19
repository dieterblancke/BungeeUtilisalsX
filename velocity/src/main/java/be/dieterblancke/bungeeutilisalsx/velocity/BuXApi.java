package be.dieterblancke.bungeeutilisalsx.velocity;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.IBuXApi;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcer;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarColor;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarStyle;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.IBossBar;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.IEventLoader;
import be.dieterblancke.bungeeutilisalsx.common.api.hubbalancer.IHubBalancer;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.BroadcastLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.BroadcastMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.language.ILanguageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.player.IPlayerUtils;
import be.dieterblancke.bungeeutilisalsx.velocity.bossbar.BossBar;
import be.dieterblancke.bungeeutilisalsx.velocity.user.VelocityConsoleUser;
import com.google.common.collect.Lists;
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
    private final IHubBalancer hubBalancer;
    private final IPunishmentHelper punishmentExecutor;
    private final IPlayerUtils playerUtils;
    private final User consoleUser = new VelocityConsoleUser();
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
        return Collections.unmodifiableList( users );
    }

    @Override
    public void addUser( User user )
    {
        this.users.add( user );
    }

    @Override
    public void removeUser( User user )
    {
        this.users.remove( user );
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
    public void broadcast( final String message )
    {
        BuX.getInstance().getJobManager().executeJob( new BroadcastMessageJob( null, message, "" ) );
    }

    @Override
    public void broadcast( final String message, final String permission )
    {
        BuX.getInstance().getJobManager().executeJob( new BroadcastMessageJob( null, message, permission ) );
    }

    @Override
    public void announce( final String prefix, final String message )
    {
        BuX.getInstance().getJobManager().executeJob( new BroadcastMessageJob( prefix, message, "" ) );
    }

    @Override
    public void announce( final String prefix, final String message, final String permission )
    {
        BuX.getInstance().getJobManager().executeJob( new BroadcastMessageJob( prefix, message, permission ) );
    }

    @Override
    public void langBroadcast( final String message, final Object... placeholders )
    {
        BuX.getInstance().getJobManager().executeJob( new BroadcastLanguageMessageJob( message, "", placeholders ) );
    }

    @Override
    public void langPermissionBroadcast( final String message, final String permission, final Object... placeholders )
    {
        BuX.getInstance().getJobManager().executeJob( new BroadcastLanguageMessageJob( message, permission, placeholders ) );
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
                                   final Component message )
    {
        return createBossBar( UUID.randomUUID(), color, style, progress, message );
    }

    @Override
    public IBossBar createBossBar( final UUID uuid,
                                   final BarColor color,
                                   final BarStyle style,
                                   final float progress,
                                   final Component message )
    {
        return new BossBar( uuid, color, style, progress, message );
    }

    @Override
    public List<StaffUser> getStaffMembers()
    {
        return BuX.getInstance().getStaffMembers();
    }
}
