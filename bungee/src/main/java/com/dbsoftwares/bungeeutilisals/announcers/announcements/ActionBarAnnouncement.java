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

package com.dbsoftwares.bungeeutilisals.announcers.announcements;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.announcer.Announcement;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

import java.util.stream.Stream;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
public class ActionBarAnnouncement extends Announcement
{

    private boolean language;
    private int time;
    private String message;
    private ScheduledTask task;

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
            send( filter( ProxyServer.getInstance().getPlayers().stream() ) );
        } else
        {
            serverGroup.getServerInfos().forEach( server -> send( filter( server.getPlayers().stream() ) ) );
        }

        if ( time > 1 )
        {
            task = ProxyServer.getInstance().getScheduler().schedule( BungeeUtilisals.getInstance(), new Runnable()
            {

                private int count = 1;

                @Override
                public void run()
                {
                    count++;

                    if ( count > time )
                    {
                        task.cancel();
                        return;
                    }
                    if ( serverGroup.isGlobal() )
                    {
                        send( filter( ProxyServer.getInstance().getPlayers().stream() ) );
                    } else
                    {
                        serverGroup.getServerInfos().forEach( server -> send( filter( server.getPlayers().stream() ) ) );
                    }
                }
            }, 1, 1, java.util.concurrent.TimeUnit.SECONDS );
        }
    }

    private void send( Stream<ProxiedPlayer> stream )
    {
        stream.forEach( player ->
        {
            String bar = message;

            if ( language )
            {
                bar = BUCore.getApi().getLanguageManager().getLanguageConfiguration(
                        BungeeUtilisals.getInstance().getDescription().getName(), player
                ).getString( message );
            }

            player.sendMessage( ChatMessageType.ACTION_BAR, Utils.format( player, bar ) );
        } );
    }
}