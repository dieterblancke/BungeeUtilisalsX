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

package be.dieterblancke.bungeeutilisalsx.velocity.utils.player;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.player.IPlayerUtils;
import be.dieterblancke.bungeeutilisalsx.velocity.Bootstrap;
import com.velocitypowered.api.proxy.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class VelocityPlayerUtils implements IPlayerUtils
{

    @Override
    public int getPlayerCount( String server )
    {
        return Bootstrap.getInstance().getProxyServer().getServer( server )
                .map( s -> s.getPlayersConnected().size() )
                .orElse( 0 );
    }

    @Override
    public List<String> getPlayers( String server )
    {
        return Bootstrap.getInstance().getProxyServer().getServer( server )
                .map( s -> s.getPlayersConnected()
                        .stream()
                        .map( Player::getUsername )
                        .collect( Collectors.toList() )
                )
                .orElse( new ArrayList<>() );
    }

    @Override
    public int getTotalCount()
    {
        return Bootstrap.getInstance().getProxyServer().getPlayerCount();
    }

    @Override
    public List<String> getPlayers()
    {
        return Bootstrap.getInstance().getProxyServer().getAllPlayers()
                .stream()
                .map( Player::getUsername )
                .collect( Collectors.toList() );
    }

    @Override
    public IProxyServer findPlayer( String name )
    {
        return Bootstrap.getInstance().getProxyServer().getPlayer( name )
                .flatMap( value -> value.getCurrentServer()
                        .map( server -> BuX.getInstance().proxyOperations().getServerInfo( server.getServerInfo().getName() ) ) )
                .orElse( null );
    }

    @Override
    public boolean isOnline( String name )
    {
        return Bootstrap.getInstance().getProxyServer().getPlayer( name ).isPresent();
    }

    @Override
    public UUID getUuidNoFallback( String targetName )
    {
        final Player player = Bootstrap.getInstance().getProxyServer().getPlayer( targetName ).orElse( null );

        if ( player != null )
        {
            return player.getUniqueId();
        }
        return null;
    }
}
