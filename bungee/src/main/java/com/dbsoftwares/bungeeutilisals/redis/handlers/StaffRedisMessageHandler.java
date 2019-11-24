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

package com.dbsoftwares.bungeeutilisals.redis.handlers;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.AbstractEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.network.NetworkStaffJoinEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.network.NetworkStaffLeaveEvent;
import com.dbsoftwares.bungeeutilisals.redis.RedisMessageHandler;
import com.dbsoftwares.bungeeutilisals.utils.redisdata.NetworkStaffConnectData;
import com.dbsoftwares.bungeeutilisals.utils.redisdata.NetworkStaffConnectData.StaffNetworkAction;

public class StaffRedisMessageHandler extends RedisMessageHandler<NetworkStaffConnectData>
{

    public StaffRedisMessageHandler()
    {
        super( NetworkStaffConnectData.class );
    }

    @Override
    public void handle( NetworkStaffConnectData data )
    {
        final AbstractEvent event;
        if ( data.getAction().equals( StaffNetworkAction.STAFF_JOIN ) )
        {
            event = new NetworkStaffJoinEvent( data.getName(), data.getUuid(), data.getRank() );
        }
        else
        {
            event = new NetworkStaffLeaveEvent( data.getName(), data.getUuid(), data.getRank() );
        }

        BUCore.getApi().getEventLoader().launchEvent( event );
    }
}
