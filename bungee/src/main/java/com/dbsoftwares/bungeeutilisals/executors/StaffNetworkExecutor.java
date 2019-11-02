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

package com.dbsoftwares.bungeeutilisals.executors;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.data.StaffRankData;
import com.dbsoftwares.bungeeutilisals.api.data.StaffUser;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.events.network.NetworkStaffJoinEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.network.NetworkStaffLeaveEvent;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;

import java.util.List;

public class StaffNetworkExecutor implements EventExecutor
{

    @Event
    public void onJoin( final NetworkStaffJoinEvent event )
    {
        BungeeUtilisals.getInstance().getStaffMembers().add(
                new StaffUser( event.getUserName(), event.getUuid(), findStaffRank( event.getStaffRank() ) )
        );
    }

    @Event
    public void onLeave( final NetworkStaffLeaveEvent event )
    {
        BungeeUtilisals.getInstance().getStaffMembers().removeIf(
                staffUser -> staffUser.getName().equals( event.getUserName() )
        );
    }

    private StaffRankData findStaffRank( final String rankName )
    {
        final List<StaffRankData> ranks = FileLocation.GENERALCOMMANDS.getDataList();

        return ranks.stream()
                .filter( rank -> rank.getName().equals( rankName ) )
                .findFirst()
                .orElseThrow( () -> new RuntimeException(
                        "Could not find a staff rank called \"" + rankName + "\"."
                                + " If you are using redis, make sure the configs are synchronized."
                ) );
    }
}
