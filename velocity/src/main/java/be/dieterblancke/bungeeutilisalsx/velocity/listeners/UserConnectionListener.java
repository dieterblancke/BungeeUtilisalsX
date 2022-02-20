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
                event.getPreviousServer().map( server -> BuX.getInstance().proxyOperations().getServerInfo( server.getServerInfo().getName() ) ).orElse( null ),
                BuX.getInstance().proxyOperations().getServerInfo( event.getServer().getServerInfo().getName() )
        );
        BuX.getApi().getEventLoader().launchEvent( userServerConnectedEvent );
    }
}
