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

package be.dieterblancke.bungeeutilisalsx.bungee.utils.player;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.player.IPlayerUtils;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.List;
import java.util.UUID;

public class BungeePlayerUtils implements IPlayerUtils
{

    @Override
    public int getPlayerCount( String server )
    {
        ServerInfo info = ProxyServer.getInstance().getServerInfo( server );

        return info == null ? 0 : info.getPlayers().size();
    }

    @Override
    public List<String> getPlayers( String server )
    {
        List<String> players = Lists.newArrayList();
        ServerInfo info = ProxyServer.getInstance().getServerInfo( server );

        if ( info != null )
        {
            info.getPlayers().forEach( player -> players.add( player.getName() ) );
        }

        return players;
    }

    @Override
    public int getTotalCount()
    {
        return ProxyServer.getInstance().getPlayers().size();
    }

    @Override
    public List<String> getPlayers()
    {
        List<String> players = Lists.newArrayList();

        ProxyServer.getInstance().getPlayers().forEach( player -> players.add( player.getName() ) );

        return players;
    }

    @Override
    public IProxyServer findPlayer( String name )
    {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer( name );

        if ( player != null )
        {
            return BuX.getInstance().proxyOperations().getServerInfo( player.getServer().getInfo().getName() );
        }

        return null;
    }

    @Override
    public boolean isOnline( String name )
    {
        return ProxyServer.getInstance().getPlayer( name ) != null;
    }

    @Override
    public UUID getUuidNoFallback( String targetName )
    {
        final ProxiedPlayer player = ProxyServer.getInstance().getPlayer( targetName );

        if ( player != null )
        {
            return player.getUniqueId();
        }
        return null;
    }
}
