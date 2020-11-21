package com.dbsoftwares.bungeeutilisalsx.velocity.hubbalancer.listeners;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.hubbalancer.HubServerType;
import com.dbsoftwares.bungeeutilisalsx.common.api.hubbalancer.ServerData;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.velocity.utils.LanguageUtils;
import com.dbsoftwares.bungeeutilisalsx.velocity.utils.VelocityServer;
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
        if ( optionalUser.isPresent() )
        {
            final User user = optionalUser.get();

            if ( user.getStorage().hasData( "HUBBALANCER_NO_REDIRECT" ) )
            {
                user.getStorage().removeData( "HUBBALANCER_NO_REDIRECT" );
                return;
            }
        }

        final ServerData optimal = BuX.getApi().getHubBalancer().findBestServer( HubServerType.LOBBY );

        if ( optimal == null || optimal.getServerInfo() == null )
        {
            event.setResult( ServerPreConnectEvent.ServerResult.denied() );

            LanguageUtils.sendLangMessage( event.getPlayer(), "hubbalancer.no-lobbies-found" );
        }
        else
        {
            event.setResult( ServerPreConnectEvent.ServerResult.allowed(
                    ( (VelocityServer) optimal.getServerInfo() ).getRegisteredServer() )
            );
        }
    }
}
