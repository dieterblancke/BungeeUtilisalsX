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

package com.dbsoftwares.bungeeutilisals.api.announcer;

import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import lombok.Data;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.stream.Stream;

@Data
public abstract class Announcement implements IAnnouncement
{

    protected ServerGroup serverGroup;
    protected String receivePermission;

    protected Announcement( ServerGroup serverGroup, String receivePermission )
    {
        this.serverGroup = serverGroup;
        this.receivePermission = receivePermission;
    }

    public abstract void send();

    protected Stream<ProxiedPlayer> filter( Stream<ProxiedPlayer> stream )
    {
        return receivePermission.isEmpty() ? stream : stream.filter( player -> player.hasPermission( receivePermission ) || player.hasPermission( "bungeeutilisals.*" ) );
    }

    public void clear()
    {
    }
}