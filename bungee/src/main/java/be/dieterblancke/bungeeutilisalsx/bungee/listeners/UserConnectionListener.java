package be.dieterblancke.bungeeutilisalsx.bungee.listeners;

import be.dieterblancke.bungeeutilisalsx.bungee.user.BungeeUser;
import be.dieterblancke.bungeeutilisalsx.bungee.utils.BungeeServer;
import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerConnectEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerConnectedEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import com.google.common.base.Strings;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Optional;

public class UserConnectionListener implements Listener
{

    @EventHandler
    public void onConnect( final PostLoginEvent event )
    {
        final BungeeUser user = new BungeeUser();

        user.load( event.getPlayer() );
    }

    // Executing on LOWEST priority to get it to execute early on in the quit procedure
    @EventHandler( priority = EventPriority.LOWEST )
    public void onDisconnect( final PlayerDisconnectEvent event )
    {
        final Optional<User> optional = BuX.getApi().getUser( event.getPlayer().getName() );

        if ( !optional.isPresent() )
        {
            return;
        }
        final User user = optional.get();
        user.unload();
    }

    @EventHandler
    public void onConnect( final ServerConnectEvent event )
    {
        final Optional<User> optional = BuX.getApi().getUser( event.getPlayer().getName() );

        if ( !optional.isPresent() )
        {
            return;
        }
        final UserServerConnectEvent userServerConnectEvent = new UserServerConnectEvent(
                optional.get(),
                BuX.getInstance().proxyOperations().getServerInfo( event.getTarget().getName() )
        );
        BuX.getApi().getEventLoader().launchEvent( userServerConnectEvent );
        if ( userServerConnectEvent.isCancelled() )
        {
            event.setCancelled( true );
        }
        event.setTarget( ( (BungeeServer) userServerConnectEvent.getTarget() ).getServerInfo() );
    }

    @EventHandler
    public void onConnect( final ServerConnectedEvent event )
    {
        final Optional<User> optional = BuX.getApi().getUser( event.getPlayer().getName() );

        if ( !optional.isPresent() )
        {
            return;
        }
        final UserServerConnectedEvent userServerConnectedEvent = new UserServerConnectedEvent(
                optional.get(),
                Optional.ofNullable( Strings.emptyToNull( optional.get().getServerName() ) )
                        .map( BuX.getInstance().proxyOperations()::getServerInfo )
                        .orElse( null ),
                BuX.getInstance().proxyOperations().getServerInfo( event.getServer().getInfo().getName() )
        );
        BuX.getApi().getEventLoader().launchEvent( userServerConnectedEvent );
    }
}
