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

package com.dbsoftwares.bungeeutilisals.announcers;

import com.dbsoftwares.bungeeutilisals.announcers.announcements.TitleAnnouncement;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.announcer.AnnouncementType;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcer;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.dbsoftwares.configuration.api.ISection;

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
            final ServerGroup group = ConfigFiles.SERVERGROUPS.getServer( section.getString( "server" ) );

            if ( group == null )
            {
                BUCore.getLogger().warning( "Could not find a servergroup or -name for " + section.getString( "server" ) + "!" );
                return;
            }

            final int fadeIn = section.getInteger( "fadein" );
            final int stay = section.getInteger( "stay" );
            final int fadeOut = section.getInteger( "fadeout" );
            final String permission = section.getString( "permission" );
            final boolean language = section.getBoolean( "language" );
            final String title = section.getString( "title" );
            final String subtitle = section.getString( "subtitle" );

            addAnnouncement( new TitleAnnouncement( language, title, subtitle, fadeIn, stay, fadeOut, group, permission ) );
        }
    }
}