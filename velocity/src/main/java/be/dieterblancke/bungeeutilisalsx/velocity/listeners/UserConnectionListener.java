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

package be.dieterblancke.bungeeutilisalsx.velocity.listeners;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerConnectEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerConnectedEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.velocity.user.VelocityUser;
import be.dieterblancke.bungeeutilisalsx.velocity.utils.VelocityServer;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.Optional;

public class UserConnectionListener
{

    @Subscribe
    public void onConnect( final PostLoginEvent event )
    {
        final VelocityUser user = new VelocityUser();

        user.load( event.getPlayer() );
    }

    // Executing on FIRST to get it to execute early on in the quit procedure
    @Subscribe( order = PostOrder.FIRST )
    public void onDisconnect( final DisconnectEvent event )
    {
        final Player player = event.getPlayer();
        final Optional<User> optional = BuX.getApi().getUser( event.getPlayer().getUsername() );

        if ( !optional.isPresent() )
        {
            return;
        }
        final User user = optional.get();
        user.unload();
    }

    @Subscribe
    public void onConnect( final ServerPreConnectEvent event )
    {
        final Optional<User> optional = BuX.getApi().getUser( event.getPlayer().getUsername() );
        final Optional<RegisteredServer> targetServer = event.getResult().getServer();

        if ( !optional.isPresent() || targetServer.isPresent() )
        {
            return;
        }
        final UserServerConnectEvent userServerConnectEvent = new UserServerConnectEvent(
                optional.get(),
                BuX.getInstance().proxyOperations().getServerInfo( targetServer.get().getServerInfo().getName() )
        );
        BuX.getApi().getEventLoader().launchEvent( userServerConnectEvent );
        if ( userServerConnectEvent.isCancelled() )
        {
            event.setResult( ServerPreConnectEvent.ServerResult.denied() );
        }
        event.setResult( ServerPreConnectEvent.ServerResult.allowed(
                ( (VelocityServer) userServerConnectEvent.getTarget() ).getRegisteredServer()
        ) );
    }

    @Subscribe
    public void onConnect( final ServerConnectedEvent event )
    {
        final Optional<User> optional = BuX.getApi().getUser( event.getPlayer().getUsername() );

        if ( !optional.isPresent() )
        {
            return;
        }
        final UserServerConnectedEvent userServerConnectedEvent = new UserServerConnectedEvent(
                optional.get(),
                BuX.getInstance().proxyOperations().getServerInfo( event.getServer().getServerInfo().getName() )
        );
        BuX.getApi().getEventLoader().launchEvent( userServerConnectedEvent );
    }
}
