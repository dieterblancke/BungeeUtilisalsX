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

package be.dieterblancke.bungeeutilisalsx.common.announcers.bossbar;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.AnnouncementType;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcer;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.IAnnouncement;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarColor;
import be.dieterblancke.bungeeutilisalsx.common.api.bossbar.BarStyle;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TimeUnit;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import com.dbsoftwares.configuration.api.ISection;
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
                messages.add(
                        new BossBarMessage(
                                BarColor.valueOf( message.getString( "color" ) ),
                                BarStyle.valueOf( message.getString( "style" ) ),
                                message.getFloat( "progress" ),
                                message.getBoolean( "language" ),
                                message.getString( "text" )
                        )
                );
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