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

package be.dieterblancke.bungeeutilisalsx.common.announcers.actionbar;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcement;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

@Getter
@Setter
@ToString
@EqualsAndHashCode( callSuper = false )
public class ActionBarAnnouncement extends Announcement
{

    private boolean language;
    private int time;
    private String message;
    private ScheduledFuture task;

    public ActionBarAnnouncement( boolean language, int time, String message, ServerGroup serverGroup, String receivePermission )
    {
        super( serverGroup, receivePermission );

        this.language = language;
        this.time = time;
        this.message = message;
    }

    @Override
    public void send()
    {
        if ( serverGroup.isGlobal() )
        {
            send( filter( BuX.getApi().getUsers().stream() ) );
        }
        else
        {
            serverGroup.getServers().forEach( server -> send( filter( server.getUsers().stream() ) ) );
        }

        if ( time > 1 )
        {
            task = BuX.getInstance().getScheduler().runTaskRepeating( 1, 1, TimeUnit.SECONDS, new Runnable()
            {

                private int count = 1;

                @Override
                public void run()
                {
                    count++;

                    if ( count > time )
                    {
                        task.cancel( true );
                        return;
                    }
                    if ( serverGroup.isGlobal() )
                    {
                        send( filter( BuX.getApi().getUsers().stream() ) );
                    }
                    else
                    {
                        serverGroup.getServers().forEach( server -> send( filter( server.getUsers().stream() ) ) );
                    }
                }
            } );
        }
    }

    private void send( Stream<User> stream )
    {
        stream.forEach( player ->
        {
            String bar = message;

            if ( language )
            {
                bar = BuX.getApi().getLanguageManager().getLanguageConfiguration(
                        BuX.getInstance().getName(), player
                ).getConfig().getString( message );
            }

            player.sendActionBar( bar );
        } );
    }
}