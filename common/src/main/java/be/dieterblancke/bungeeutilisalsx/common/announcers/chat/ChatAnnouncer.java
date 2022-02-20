package be.dieterblancke.bungeeutilisalsx.common.announcers.chat;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.AnnouncementType;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import be.dieterblancke.configuration.api.ISection;

import java.util.List;

public class ChatAnnouncer extends Announcer
{

    public ChatAnnouncer()
    {
        super( AnnouncementType.CHAT );
    }

    @Override
    public void loadAnnouncements()
    {
        for ( ISection section : configuration.getSectionList( "announcements" ) )
        {
            final ServerGroup group = ConfigFiles.SERVERGROUPS.getServer( section.getString( "server" ) );

            if ( group == null )
            {
                BuX.getLogger().warning(
                        "Could not find a servergroup or -name for " + section.getString( "server" ) + "!"
                );
                return;
            }

            final String messagesKey = "messages";
            final boolean usePrefix = section.getBoolean( "use-prefix" );
            final String permission = section.getString( "permission" );

            if ( section.isList( messagesKey ) )
            {
                List<String> messages = section.getStringList( messagesKey );

                addAnnouncement( new ChatAnnouncement( usePrefix, messages, group, permission ) );
            }
            else
            {
                String messagePath = section.getString( messagesKey );

                addAnnouncement( new ChatAnnouncement( usePrefix, messagePath, group, permission ) );
            }
        }
    }
}