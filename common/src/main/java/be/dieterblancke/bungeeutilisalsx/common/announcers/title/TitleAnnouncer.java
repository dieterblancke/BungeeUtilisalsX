package be.dieterblancke.bungeeutilisalsx.common.announcers.title;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.AnnouncementType;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import be.dieterblancke.configuration.api.ISection;

import java.util.Optional;

public class TitleAnnouncer extends Announcer
{

    public TitleAnnouncer()
    {
        super( AnnouncementType.TITLE );
    }

    @Override
    public void loadAnnouncements()
    {
        for ( ISection section : configuration.getSectionList( "announcements" ) )
        {
            final Optional<ServerGroup> optionalGroup = ConfigFiles.SERVERGROUPS.getServer( section.getString( "server" ) );

            if ( optionalGroup.isEmpty() )
            {
                BuX.getLogger().warning( "Could not find a servergroup or -name for " + section.getString( "server" ) + "!" );
                return;
            }
            final String permission = section.getString( "permission" );
            final boolean language = section.getBoolean( "language" );

            addAnnouncement( new TitleAnnouncement( language, new TitleMessage( section ), optionalGroup.get(), permission ) );
        }
    }
}