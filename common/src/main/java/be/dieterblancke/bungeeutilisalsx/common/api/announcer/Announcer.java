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

package be.dieterblancke.bungeeutilisalsx.common.api.announcer;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.FileUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TimeUnit;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.RandomIterator;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import com.dbsoftwares.configuration.api.IConfiguration;
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
                    "/announcer/" + type.toString().toLowerCase() + ".yml"
            ) )
            {
                IConfiguration.createDefaultFile( inputStream, file );
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }
        }

        configuration = IConfiguration.loadYamlConfiguration( file );
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
            if ( a instanceof GroupedAnnouncement )
            {
                final GroupedAnnouncement ga = (GroupedAnnouncement) a;

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