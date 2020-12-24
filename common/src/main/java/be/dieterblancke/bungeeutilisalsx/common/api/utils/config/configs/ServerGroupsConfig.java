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

package be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.Config;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.util.Map;

public class ServerGroupsConfig extends Config
{

    @Getter
    private final Map<String, ServerGroup> servers = Maps.newHashMap();

    public ServerGroupsConfig( String location )
    {
        super( location );
    }

    public ServerGroup getServer( final String server )
    {
        return servers.get( server );
    }

    @Override
    public void purge()
    {
        servers.clear();
    }

    @Override
    public void setup()
    {
        if ( config == null )
        {
            return;
        }

        for ( ISection group : config.getSectionList( "groups" ) )
        {
            String name = group.getString( "name" );

            if ( group.isList( "servers" ) )
            {
                servers.put( name, new ServerGroup( name, false, group.getStringList( "servers" ) ) );
            }
            else
            {
                servers.put( name, new ServerGroup( name, true, Lists.newArrayList() ) );
            }
        }

        for ( IProxyServer server : BuX.getInstance().proxyOperations().getServers() )
        {
            if ( !servers.containsKey( server.getName() ) )
            {
                servers.put(
                        server.getName(),
                        new ServerGroup( server.getName(), false, Lists.newArrayList( server.getName() ) )
                );
            }
        }
    }
}
