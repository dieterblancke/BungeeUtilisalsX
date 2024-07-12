package dev.endoy.bungeeutilisalsx.common.api.announcer;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.utils.FileUtils;
import dev.endoy.bungeeutilisalsx.common.api.utils.TimeUnit;
import dev.endoy.bungeeutilisalsx.common.api.utils.other.RandomIterator;
import dev.endoy.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import dev.endoy.configuration.api.IConfiguration;
import dev.endoy.configuration.yaml.YamlConfigurationOptions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;

@Data
public abstract class Announcer
{

    private static final File folder;
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool( 2 );
    @Getter
    private static Map<AnnouncementType, Announcer> announcers = Maps.newHashMap();

    static
    {
        folder = new File( BuX.getInstance().getDataFolder(), "announcer" );

        if ( !folder.exists() )
        {
            folder.mkdirs();
        }
    }

    protected IConfiguration configuration;
    private ScheduledFuture task;
    private AnnouncementType type;
    private List<IAnnouncement> announcements = Lists.newArrayList();
    private Iterator<IAnnouncement> announcementIterator;
    private boolean enabled;
    private TimeUnit unit;
    private int delay;
    private boolean random;
    private boolean groupPerServer;

    public Announcer( final AnnouncementType type )
    {
        this.type = type;

        final File file = new File( folder, type.toString().toLowerCase() + ".yml" );

        if ( !file.exists() )
        {
            try ( InputStream inputStream = FileUtils.getResourceAsStream(
                    "/configurations/announcer/" + type.toString().toLowerCase() + ".yml"
            ) )
            {
                IConfiguration.createDefaultFile( inputStream, file );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }

        configuration = IConfiguration.loadYamlConfiguration(
                file,
                YamlConfigurationOptions.builder().useComments( true ).build()
        );
        load();
    }

    @SafeVarargs
    public static void registerAnnouncers( Class<? extends Announcer>... classes )
    {
        for ( Class<? extends Announcer> clazz : classes )
        {
            try
            {
                final Announcer announcer = clazz.getConstructor().newInstance();

                if ( announcer.isEnabled() )
                {
                    announcer.loadAnnouncements();
                    announcer.start();

                    BuX.getLogger().info( "Loading " + announcer.getType().toString().toLowerCase() + " announcements ..." );
                }

                announcers.put( announcer.getType(), announcer );
            }
            catch ( InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }
        }
    }

    public void start()
    {
        if ( task != null )
        {
            throw new IllegalStateException( "Announcer is already running." );
        }

        task = SCHEDULED_EXECUTOR_SERVICE.scheduleWithFixedDelay(
                new Runnable()
                {

                    private IAnnouncement previous;

                    @Override
                    public void run()
                    {
                        if ( groupPerServer )
                        {
                            for ( IAnnouncement announcement : announcements )
                            {
                                if ( announcement instanceof GroupedAnnouncement )
                                {
                                    announcement.send();
                                }
                            }
                        }
                        else
                        {
                            if ( previous != null )
                            {
                                previous.clear();
                            }
                            final IAnnouncement next = getNextAnnouncement();
                            next.send();
                            previous = next;
                        }
                    }
                },
                0,
                delay,
                unit.toJavaTimeUnit()
        );
    }

    public void stop()
    {
        if ( task == null )
        {
            return;
        }
        task.cancel( true );
        task = null;
    }

    public void addAnnouncement( Announcement announcement )
    {
        if ( groupPerServer )
        {
            final GroupedAnnouncement groupedAnnouncement = getGroupedAnnouncement( announcement.getServerGroup() );
            groupedAnnouncement.getAnnouncements().add( announcement );
        }
        else
        {
            announcements.add( announcement );
        }
    }

    private GroupedAnnouncement getGroupedAnnouncement( final ServerGroup group )
    {
        for ( IAnnouncement a : announcements )
        {
            if ( a instanceof final GroupedAnnouncement ga )
            {

                if ( ga.getGroup().equals( group ) )
                {
                    return ga;
                }
            }
        }

        final GroupedAnnouncement announcement = new GroupedAnnouncement( random, group, Lists.newArrayList() );
        announcements.add( announcement );
        return announcement;
    }

    private IAnnouncement getNextAnnouncement()
    {
        if ( announcements.isEmpty() )
        {
            throw new RuntimeException( "No " + type.toString().toLowerCase() + " announcements are found! Please create some in your config" );
        }
        if ( announcementIterator == null || !announcementIterator.hasNext() )
        {
            announcementIterator = random ? new RandomIterator<>( announcements ) : announcements.iterator();
        }
        return announcementIterator.next();
    }

    public abstract void loadAnnouncements();

    public void reload()
    {
        try
        {
            configuration.reload();
        }
        catch ( IOException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            return;
        }
        stop();
        announcements.clear();
        announcementIterator = null;

        load();

        if ( enabled )
        {
            loadAnnouncements();
            start();
        }
    }

    private void load()
    {
        this.enabled = configuration.getBoolean( "enabled" );
        this.unit = TimeUnit.valueOfOrElse( configuration.getString( "delay.unit" ), TimeUnit.SECONDS );
        this.delay = configuration.getInteger( "delay.time" );
        this.random = configuration.getBoolean( "random" );
        this.groupPerServer = configuration.exists( "group-per-server" ) ? configuration.getBoolean( "group-per-server" ) : false;
    }
}