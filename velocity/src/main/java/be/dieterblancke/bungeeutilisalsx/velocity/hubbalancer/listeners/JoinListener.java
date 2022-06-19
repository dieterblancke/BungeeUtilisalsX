package be.dieterblancke.bungeeutilisalsx.velocity.hubbalancer.listeners;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.hubbalancer.HubServerType;
import be.dieterblancke.bungeeutilisalsx.common.api.hubbalancer.ServerData;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorageKey;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.velocity.utils.VelocityServer;
import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPreConnectEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.RegisteredServer;

import java.util.List;
import java.util.Optional;

public class JoinListener
{

    @Subscribe( order = PostOrder.LAST )
    public void onLogin( ServerPreConnectEvent event )
    {
        final Player player = event.getPlayer();
        final RegisteredServer target = event.getResult().getServer().orElse( null );
        final List<String> triggers = ConfigFiles.HUBBALANCER.getConfig().getStringList( "triggers" );

        if ( target == null || !triggers.contains( target.getServerInfo().getName() ) )
        {
            return;
        }
        final Optional<User> optionalUser = BuX.getApi().getUser( player.getUsername() );
        if ( !optionalUser.isPresent() )
        {
            return;
        }
        final User user = optionalUser.get();

        if ( user.getStorage().hasData( UserStorageKey.HUBBALANCER_NO_REDIRECT ) )
        {
            user.getStorage().removeData( UserStorageKey.HUBBALANCER_NO_REDIRECT );
            return;
        }

        final ServerData optimal = BuX.getApi().getHubBalancer().findBestServer( HubServerType.LOBBY );

        if ( optimal == null || optimal.getServerInfo() == null )
        {
            event.setResult( ServerPreConnectEvent.ServerResult.denied() );

            user.sendLangMessage( "hubbalancer.no-lobbies-found" );
        }
        else
        {
            event.setResult( ServerPreConnectEvent.ServerResult.allowed(
                    ( (VelocityServer) optimal.getServerInfo() ).getRegisteredServer() )
            );
        }
    }
}
