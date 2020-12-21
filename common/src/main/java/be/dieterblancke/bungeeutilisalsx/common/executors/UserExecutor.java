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

package be.dieterblancke.bungeeutilisalsx.common.executors;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.network.NetworkStaffJoinEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.network.NetworkStaffLeaveEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserLoadEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerConnectedEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserUnloadEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffRankData;

import java.util.Comparator;

public class UserExecutor implements EventExecutor
{

    @Event
    public void onLoad( final UserLoadEvent event )
    {
        event.getApi().getUsers().add( event.getUser() );
    }

    @Event
    public void onUnload( final UserUnloadEvent event )
    {
        event.getApi().getUsers().remove( event.getUser() );
    }

    @Event
    public void onStaffLoad( final UserLoadEvent event )
    {
        final User user = event.getUser();
        final StaffRankData rank = findStaffRank( user );

        if ( rank == null )
        {
            return;
        }

        BuX.getApi().getEventLoader().launchEvent(
                new NetworkStaffJoinEvent( user.getName(), user.getUuid(), rank.getName() )
        );
    }

    @Event
    public void onStaffUnload( UserUnloadEvent event )
    {
        final User user = event.getUser();
        final StaffRankData rank = findStaffRank( user );

        BuX.getApi().getEventLoader().launchEvent(
                new NetworkStaffLeaveEvent( user.getName(), user.getUuid(), rank == null ? null : rank.getName() )
        );
    }

    @Event
    public void onServerConnected( final UserServerConnectedEvent event )
    {
        BuX.getApi().getStorageManager().getDao().getUserDao().setCurrentServer(
                event.getUser().getUuid(),
                event.getTarget().getName()
        );
    }

    @Event
    public void updateServerOnUserDisconnect( final UserUnloadEvent event )
    {
        if ( !Utils.isSpigot() )
        {
            BuX.getApi().getStorageManager().getDao().getUserDao().setCurrentServer(
                    event.getUser().getUuid(),
                    null
            );
        }
    }

    private StaffRankData findStaffRank( final User user )
    {
        return ConfigFiles.RANKS.getRanks().stream()
                .filter( rank -> user.hasPermission( rank.getPermission(), true ) )
                .max( Comparator.comparingInt( StaffRankData::getPriority ) )
                .orElse( null );
    }
}