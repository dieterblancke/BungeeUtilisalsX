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

package be.dieterblancke.bungeeutilisalsx.common.api.utils.server;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import com.google.common.collect.Lists;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class ServerGroup
{

    private String name;
    private boolean global;
    private List<String> servers;

    public ServerGroup( String name, boolean global, List<String> servers )
    {
        this.name = name;
        this.global = global;
        this.servers = searchServers( servers );
    }

    private List<String> searchServers( List<String> servers )
    {
        LinkedList<String> foundServers = Lists.newLinkedList();

        servers.forEach( server ->
        {
            for ( IProxyServer info : BuX.getInstance().proxyOperations().getServers() )
            {
                String name = info.getName().toLowerCase();

                if ( server.startsWith( "*" ) )
                {
                    if ( name.endsWith( server.substring( 1 ).toLowerCase() ) )
                    {
                        foundServers.add( info.getName() );
                    }
                }
                else if ( server.endsWith( "*" ) )
                {
                    if ( name.startsWith( server.substring( 0, server.length() - 1 ).toLowerCase() ) )
                    {
                        foundServers.add( info.getName() );
                    }
                }
                else
                {
                    if ( name.equalsIgnoreCase( server ) )
                    {
                        foundServers.add( info.getName() );
                    }
                }
            }
        } );

        return foundServers;
    }

    public void addServer( final String server )
    {
        this.servers.add( server );
    }

    public int getPlayers()
    {
        int players = 0;

        if ( global )
        {
            return BuX.getApi().getPlayerUtils().getTotalCount();
        }

        for ( String server : servers )
        {
            players += BuX.getApi().getPlayerUtils().getPlayerCount( server );
        }

        return players;
    }

    public List<String> getPlayerList()
    {
        List<String> players = Lists.newArrayList();

        if ( global )
        {
            return BuX.getApi().getPlayerUtils().getPlayers();
        }

        for ( String server : servers )
        {
            players.addAll( BuX.getApi().getPlayerUtils().getPlayers( server ) );
        }

        return players;
    }

    public List<IProxyServer> getServers()
    {
        final List<IProxyServer> servers = Lists.newArrayList();

        this.servers.forEach( serverName ->
        {
            IProxyServer info = BuX.getInstance().proxyOperations().getServerInfo( serverName );

            if ( info != null )
            {
                servers.add( info );
            }
        } );

        return servers;
    }

    public boolean isInGroup( final String serverName )
    {
        for ( String server : servers )
        {
            if ( server.equalsIgnoreCase( serverName ) )
            {
                return true;
            }
        }
        return false;
    }
}
