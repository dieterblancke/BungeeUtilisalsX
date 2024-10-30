package dev.endoy.bungeeutilisalsx.common.announcers.bossbar;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.announcer.AnnouncementType;
import dev.endoy.bungeeutilisalsx.common.api.announcer.Announcer;
import dev.endoy.bungeeutilisalsx.common.api.announcer.IAnnouncement;
import dev.endoy.bungeeutilisalsx.common.api.utils.TimeUnit;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import dev.endoy.configuration.api.ISection;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;

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
            final Optional<ServerGroup> optionalGroup = ConfigFiles.SERVERGROUPS.getServer( section.getString( "server" ) );

            if ( optionalGroup.isEmpty() )
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
            addAnnouncement( new BossBarAnnouncement( messages, unit, time, optionalGroup.get(), permission ) );
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