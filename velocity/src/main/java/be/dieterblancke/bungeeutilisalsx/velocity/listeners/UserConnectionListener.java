package be.dieterblancke.bungeeutilisalsx.velocity.listeners;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerConnectEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerConnectEvent.ConnectReason;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerConnectedEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerKickEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.velocity.user.VelocityUser;
import be.dieterblancke.bungeeutilisalsx.velocity.utils.VelocityServer;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent;
import com.velocitypowered.api.event.player.KickedFromServerEvent.RedirectPlayer;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;

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

        if ( optional.isEmpty() )
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

        if ( optional.isEmpty() || targetServer.isPresent() )
        {
            return;
        }
        final UserServerConnectEvent userServerConnectEvent = new UserServerConnectEvent(
                optional.get(),
                BuX.getInstance().proxyOperations().getServerInfo( targetServer.get().getServerInfo().getName() ),
                ConnectReason.UNKNOWN // Velocity does not seem to have connect reasons as of now
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

        if ( optional.isPresent() )
        {
            final UserServerConnectedEvent userServerConnectedEvent = new UserServerConnectedEvent(
                    optional.get(),
                    event.getPreviousServer().map( server -> BuX.getInstance().proxyOperations().getServerInfo( server.getServerInfo().getName() ) ),
                    BuX.getInstance().proxyOperations().getServerInfo( event.getServer().getServerInfo().getName() )
            );
            BuX.getApi().getEventLoader().launchEvent( userServerConnectedEvent );
        }
        else
        {
            return;
        }
    }

    @Subscribe
    public void onConnect( KickedFromServerEvent event )
    {
        Optional<User> optional = BuX.getApi().getUser( event.getPlayer().getUsername() );

        if ( optional.isEmpty() )
        {
            return;
        }

        GsonComponentSerializer componentSerializer = GsonComponentSerializer.gson();
        UserServerKickEvent userServerKickEvent = new UserServerKickEvent(
                optional.get(),
                event.getServer() == null ? null : BuX.getInstance().proxyOperations().getServerInfo( event.getServer().getServerInfo().getName() ),
                event.getResult() instanceof RedirectPlayer
                        ? BuX.getInstance().proxyOperations().getServerInfo( ( (RedirectPlayer) event.getResult() ).getServer().getServerInfo().getName() )
                        : null,
                event.getServerKickReason().orElse( null )
        );
        BuX.getApi().getEventLoader().launchEvent( userServerKickEvent );

        if ( userServerKickEvent.isTargetChanged() )
        {
            event.setResult( RedirectPlayer.create(
                    ( (VelocityServer) userServerKickEvent.getRedirectServer() ).getRegisteredServer(),
                    userServerKickEvent.getKickMessage()
            ) );
        }
    }
}
