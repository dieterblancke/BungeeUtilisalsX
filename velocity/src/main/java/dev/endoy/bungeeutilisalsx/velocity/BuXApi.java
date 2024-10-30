package dev.endoy.bungeeutilisalsx.velocity;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.IBuXApi;
import dev.endoy.bungeeutilisalsx.common.api.announcer.Announcer;
import dev.endoy.bungeeutilisalsx.common.api.bossbar.BarColor;
import dev.endoy.bungeeutilisalsx.common.api.bossbar.BarStyle;
import dev.endoy.bungeeutilisalsx.common.api.bossbar.IBossBar;
import dev.endoy.bungeeutilisalsx.common.api.event.event.IEventLoader;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.BroadcastLanguageMessageJob;
import dev.endoy.bungeeutilisalsx.common.api.job.jobs.BroadcastMessageJob;
import dev.endoy.bungeeutilisalsx.common.api.language.ILanguageManager;
import dev.endoy.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import dev.endoy.bungeeutilisalsx.common.api.serverbalancer.ServerBalancer;
import dev.endoy.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.other.StaffUser;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.HasMessagePlaceholders;
import dev.endoy.bungeeutilisalsx.common.api.utils.player.IPlayerUtils;
import dev.endoy.bungeeutilisalsx.velocity.bossbar.BossBar;
import dev.endoy.bungeeutilisalsx.velocity.user.VelocityConsoleUser;
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
    private final IPunishmentHelper punishmentExecutor;
    private final IPlayerUtils playerUtils;
    private final User consoleUser = new VelocityConsoleUser();
    private final List<User> users = Collections.synchronizedList( Lists.newArrayList() );
    private final ServerBalancer serverBalancer;

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
    public void langBroadcast( final String message, final HasMessagePlaceholders placeholders )
    {
        BuX.getInstance().getJobManager().executeJob( new BroadcastLanguageMessageJob( message, "", placeholders.getMessagePlaceholders() ) );
    }

    @Override
    public void langPermissionBroadcast( final String message, final String permission, final HasMessagePlaceholders placeholders )
    {
        BuX.getInstance().getJobManager().executeJob( new BroadcastLanguageMessageJob( message, permission, placeholders.getMessagePlaceholders() ) );
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
