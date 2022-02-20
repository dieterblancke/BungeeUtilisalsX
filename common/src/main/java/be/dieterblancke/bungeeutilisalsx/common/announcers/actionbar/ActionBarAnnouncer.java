package be.dieterblancke.bungeeutilisalsx.common.announcers.actionbar;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.AnnouncementType;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import be.dieterblancke.configuration.api.ISection;

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
            final ServerGroup group = ConfigFiles.SERVERGROUPS.getServer( section.getString( "server" ) );

            if ( group == null )
            {
                BuX.getLogger().warning( "Could not find a servergroup or -name for " + section.getString( "server" ) + "!" );
                return;
            }

            final boolean useLanguage = section.getBoolean( "use-language" );
            final int time = section.getInteger( "time" );
            final String permission = section.getString( "permission" );

            final String message = section.getString( "message" );

            addAnnouncement( new ActionBarAnnouncement( useLanguage, time, message, group, permission ) );
        }
    }
}