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
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.server.ServerGroup;
import lombok.Getter;
import lombok.Value;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class IngameMotdConfig extends Config
{

    @Getter
    private final List<IngameMotd> motds = new ArrayList<>();

    public IngameMotdConfig( String location )
    {
        super( location );
    }

    @Override
    public void purge()
    {
        motds.clear();
    }

    @Override
    public void setup()
    {
        motds.addAll(
                config.getSectionList( "motds" )
                        .stream()
                        .map( section -> new IngameMotd(
                                section.exists( "server" )
                                        ? ConfigFiles.SERVERGROUPS.getServer( section.getString( "server" ) )
                                        : null,
                                section.exists( "once-per-session" ) && section.getBoolean( "once-per-session" ),
                                section.exists( "language" ) && section.getBoolean( "language" ),
                                section.isString( "message" )
                                        ? Collections.singletonList( section.getString( "message" ) )
                                        : section.getStringList( "message" )
                        ) )
                        .collect( Collectors.toList() )
        );
    }

    public List<IngameMotd> getApplicableMotds( final IProxyServer server )
    {
        return motds
                .stream()
                .filter( motd -> motd.getServer() == null || ( server != null && motd.getServer().isInGroup( server.getName() ) ) )
                .collect( Collectors.toList() );
    }

    @Value
    public static class IngameMotd
    {
        UUID uuid = UUID.randomUUID();
        ServerGroup server;
        boolean oncePerSession;
        boolean language;
        List<String> message;
    }
}
