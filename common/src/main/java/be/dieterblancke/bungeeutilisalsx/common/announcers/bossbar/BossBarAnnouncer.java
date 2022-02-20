package be.dieterblancke.bungeeutilisalsx.common.announcers.bossbar;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.AnnouncementType;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcer;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.IAnnouncement;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TimeUnit;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import be.dieterblancke.configuration.api.ISection;
import com.google.common.collect.Lists;

import java.util.List;

public class BossBarAnnouncer extends Announcer
{

    public BossBarAnnouncer()
    {
        super( AnnouncementType.BOSSBAR );
    }

    @Override
    public void loadAnnouncements()
    {
        for ( ISection section : configuration.getSectionList( "announcements" ) )
        {
            final ServerGroup group = ConfigFiles.SERVERGROUPS.getServer( section.getString( "server" ) );

            if ( group == null )
            {
                BuX.getLogger().info( "Could not find a servergroup or -name for " + section.getString( "server" ) + "!" );
                return;
            }
            final List<BossBarMessage> messages = Lists.newArrayList();

            final int time;
            final TimeUnit unit;
            if ( section.isInteger( "stay" ) )
            {
                unit = TimeUnit.SECONDS;
                time = section.getInteger( "stay" );
            }
            else
            {
                unit = TimeUnit.valueOfOrElse( section.getString( "stay.unit" ), TimeUnit.SECONDS );
                time = section.getInteger( "stay.time" );
            }
            final String permission = section.getString( "permission" );

            for ( ISection message : section.getSectionList( "messages" ) )
            {
                messages.add( new BossBarMessage( message ) );
            }
            addAnnouncement( new BossBarAnnouncement( messages, unit, time, group, permission ) );
        }
    }

    @Override
    public void stop()
    {
        super.stop();
        for ( IAnnouncement announcement : super.getAnnouncements() )
        {
            announcement.clear();
        }
    }
}