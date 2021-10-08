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

import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.Config;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

public class HubBalancerConfig extends Config
{

    @Getter
    private final List<ServerGroup> servers = Lists.newArrayList();

    public HubBalancerConfig( String location )
    {
        super( location );
    }

    public boolean isPreventedOn( final String server )
    {
        return servers.stream()
                .anyMatch( it -> it.isInGroup( server ) );
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

        for ( String server : config.getStringList( "commands.hub.prevent-on" ) )
        {
            servers.add( ConfigFiles.SERVERGROUPS.getServer( server ) );
        }
    }
}
