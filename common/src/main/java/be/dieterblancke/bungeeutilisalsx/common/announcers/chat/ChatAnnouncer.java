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

package be.dieterblancke.bungeeutilisalsx.common.announcers.chat;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.AnnouncementType;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import com.dbsoftwares.configuration.api.ISection;

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