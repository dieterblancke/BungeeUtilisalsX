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

package com.dbsoftwares.bungeeutilisalsx.common.announcers.bossbar;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.announcer.Announcement;
import com.dbsoftwares.bungeeutilisalsx.common.api.bossbar.IBossBar;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.TimeUnit;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.Utils;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.stream.Stream;

@Getter
@Setter
@EqualsAndHashCode( callSuper = true )
public class BossBarAnnouncement extends Announcement
{

    private TimeUnit stayUnit;
    private int stayTime;

    private List<BossBarMessage> messages;
    private List<IBossBar> bars = Lists.newArrayList();

    private ScheduledFuture task;

    public BossBarAnnouncement( final List<BossBarMessage> messages,
                                final TimeUnit stayUnit,
                                final int stayTime,
                                final ServerGroup serverGroup,
                                final String receivePermission )
    {
        super( serverGroup, receivePermission );

        this.messages = messages;
        this.stayUnit = stayUnit;
        this.stayTime = stayTime;
    }

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
    }

    private void send( Stream<User> stream )
    {
        stream.forEach( user ->
        {
            final IConfiguration config = user.getLanguageConfig();

            messages.forEach( message ->
            {
                final IBossBar bar = BuX.getApi().createBossBar();
                bar.setMessage(
                        Utils.format( user, message.isLanguage()
                                ? config.getString( message.getText() )
                                : message.getText() )
                );
                bar.setColor( message.getColor() );
                bar.setProgress( message.getProgress() );
                bar.setStyle( message.getStyle() );

                bar.addUser( user );
                bars.add( bar );
            } );
        } );
        if ( stayTime > 0 )
        {
            task = BuX.getInstance().getScheduler().runTaskDelayed( stayTime, stayUnit.toJavaTimeUnit(), this::clear );
        }
    }

    @Override
    public void clear()
    {
        bars.forEach( bar ->
        {
            bar.clearUsers();
            bar.unregister();
        } );
        if ( task != null )
        {
            // for if stay > the announcement rotation delay (avoiding useless method calling)
            task.cancel( true );
            task = null;
        }
    }
}