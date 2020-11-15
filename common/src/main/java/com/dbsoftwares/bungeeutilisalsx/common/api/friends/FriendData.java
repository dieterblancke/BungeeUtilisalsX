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

package com.dbsoftwares.bungeeutilisalsx.common.api.friends;

import com.dbsoftwares.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;
import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import lombok.Data;

import java.util.Date;
import java.util.UUID;

@Data
public class FriendData
{

    private UUID uuid;
    private String friend;
    private Date friendSince;
    private boolean online;
    private Date lastOnline;

    public FriendData()
    {
    }

    public FriendData( final UUID uuid, final String friend, final Date friendSince, final Date lastSeen )
    {
        this.uuid = uuid;
        this.friend = friend;
        this.friendSince = friendSince;
        this.online = BuX.getApi().getPlayerUtils().isOnline( friend );
        this.lastOnline = lastSeen;
    }
}