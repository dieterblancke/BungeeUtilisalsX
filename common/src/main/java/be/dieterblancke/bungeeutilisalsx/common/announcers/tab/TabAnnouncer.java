package be.dieterblancke.bungeeutilisalsx.common.announcers.tab;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.AnnouncementType;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import be.dieterblancke.configuration.api.ISection;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Optional;

public class TabAnnouncer extends Announcer
{

    public TabAnnouncer()
    {
        super( AnnouncementType.TAB );
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
            final List<String> header = section.isList( "header" )
                    ? section.getStringList( "header" )
                    : Lists.newArrayList( section.getString( "header" ) );
            final List<String> footer = section.isList( "footer" )
                    ? section.getStringList( "footer" )
                    : Lists.newArrayList( section.getString( "footer" ) );

            addAnnouncement( new TabAnnouncement( language, header, footer, optionalGroup.get(), permission ) );
        }
    }
}