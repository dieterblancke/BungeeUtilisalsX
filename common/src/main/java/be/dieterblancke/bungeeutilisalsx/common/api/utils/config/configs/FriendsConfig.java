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
import java.util.Objects;
import java.util.stream.Collectors;

public class FriendsConfig extends Config
{

    @Getter
    private final List<ServerGroup> disabledSwitchMessageServers = Lists.newArrayList();

    public FriendsConfig( String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        disabledSwitchMessageServers.clear();
    }

    @Override
    public void setup()
    {
        disabledSwitchMessageServers.addAll(
                config.getStringList( "ignore-for-switch" )
                        .stream()
                        .map( str -> ConfigFiles.SERVERGROUPS.getServer( str ) )
                        .filter( Objects::nonNull )
                        .collect( Collectors.toList() )
        );
    }

    public boolean isDisabledServerSwitch( final String serverName )
    {
        if ( serverName == null )
        {
            return false;
        }
        return disabledSwitchMessageServers.stream().anyMatch( group -> group.isInGroup( serverName ) );
    }
}
