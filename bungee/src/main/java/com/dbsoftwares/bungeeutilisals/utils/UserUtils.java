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

package com.dbsoftwares.bungeeutilisals.utils;

import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class UserUtils
{

    public static String getJoinedHost( final ProxiedPlayer player )
    {
        final String joinedHost;
        if ( player.getPendingConnection().getVirtualHost() == null )
        {
            joinedHost = null;
        }
        else
        {
            if ( player.getPendingConnection().getVirtualHost().getHostName() == null )
            {
                joinedHost = Utils.getIP( player.getPendingConnection().getVirtualHost().getAddress() );
            }
            else
            {
                joinedHost = player.getPendingConnection().getVirtualHost().getHostName();
            }
        }
        return joinedHost;
    }

    public static long getOnlinePlayersOnDomain( final String domain )
    {
        return ProxyServer.getInstance().getPlayers()
                .stream()
                .filter( player -> getJoinedHost( player ).equalsIgnoreCase( domain ) )
                .count();
    }
}
