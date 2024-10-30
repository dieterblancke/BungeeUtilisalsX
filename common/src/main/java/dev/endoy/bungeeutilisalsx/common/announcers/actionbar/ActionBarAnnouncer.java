package dev.endoy.bungeeutilisalsx.common.announcers.actionbar;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.announcer.AnnouncementType;
import dev.endoy.bungeeutilisalsx.common.api.announcer.Announcer;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import dev.endoy.configuration.api.ISection;

import java.util.Optional;

public class ActionBarAnnouncer extends Announcer
{

    public ActionBarAnnouncer()
    {
        super( AnnouncementType.ACTIONBAR );
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

            final boolean useLanguage = section.getBoolean( "use-language" );
            final int time = section.getInteger( "time" );
            final String permission = section.getString( "permission" );

            final String message = section.getString( "message" );

            addAnnouncement( new ActionBarAnnouncement( useLanguage, time, message, optionalGroup.get(), permission ) );
        }
    }
}