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

package be.dieterblancke.bungeeutilisalsx.bungee.listeners;

import be.dieterblancke.bungeeutilisalsx.bungee.Bootstrap;
import be.dieterblancke.bungeeutilisalsx.bungee.utils.BungeeMotdConnection;
import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.other.ProxyMotdPingEvent;
import be.dieterblancke.bungeeutilisalsx.common.motd.MotdConnection;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.connection.PendingConnection;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.net.InetSocketAddress;

public class MotdPingListener implements Listener
{

    @EventHandler
    public void onPing( final ProxyPingEvent event )
    {
        event.registerIntent( Bootstrap.getInstance() );

        final MotdConnection motdConnection = this.createMotdConnection( event.getConnection() );
        final ProxyMotdPingEvent proxyMotdPingEvent = new ProxyMotdPingEvent(
                motdConnection,
                ( e ) ->
                {
                    if ( e.getMotdPingResponse() == null )
                    {
                        return;
                    }
                    final ServerPing orig = event.getResponse();

                    event.getResponse().setPlayers( new Players(
                            orig.getPlayers().getMax(),
                            orig.getPlayers().getOnline(),
                            e.getMotdPingResponse().getPlayers()
                                    .stream()
                                    .map( it -> new PlayerInfo( it.getName(), it.getUuid() ) )
                                    .toArray( PlayerInfo[]::new )
                    ) );
                    event.getResponse().setDescriptionComponent( e.getMotdPingResponse().getMotd() );

                    event.completeIntent( Bootstrap.getInstance() );
                }
        );

        BuX.getApi().getEventLoader().launchEventAsync( proxyMotdPingEvent );
    }

    // Name not known on serverlist ping, so we're loading the last seen name on the IP as the initial connection name.
    private MotdConnection createMotdConnection( final PendingConnection connection )
    {
        return new BungeeMotdConnection(
                connection.getVersion(),
                (InetSocketAddress) connection.getSocketAddress(),
                connection.getVirtualHost()
        );
    }
}