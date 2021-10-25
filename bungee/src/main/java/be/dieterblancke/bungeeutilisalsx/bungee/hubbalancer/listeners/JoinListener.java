package be.dieterblancke.bungeeutilisalsx.bungee.hubbalancer.listeners;

import be.dieterblancke.bungeeutilisalsx.bungee.utils.BungeeServer;
import be.dieterblancke.bungeeutilisalsx.bungee.utils.LanguageUtils;
import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.hubbalancer.HubServerType;
import be.dieterblancke.bungeeutilisalsx.common.api.hubbalancer.ServerData;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorageKey;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Optional;

public class JoinListener implements Listener
{

    @EventHandler( priority = EventPriority.HIGHEST )
    public void onLogin( ServerConnectEvent event )
    {
        final ProxiedPlayer player = event.getPlayer();
        final ServerInfo target = event.getTarget();

        if ( !BuX.getApi().getHubBalancer().isTrigger( target.getName() ) )
        {
            return;
        }
        final Optional<User> optionalUser = BuX.getApi().getUser( player.getName() );
        if ( optionalUser.isPresent() )
        {
            final User user = optionalUser.get();

            if ( user.getStorage().hasData( UserStorageKey.HUBBALANCER_NO_REDIRECT ) )
            {
                user.getStorage().removeData( UserStorageKey.HUBBALANCER_NO_REDIRECT );
                return;
            }
        }

        final ServerData optimal = BuX.getApi().getHubBalancer().findBestServer( HubServerType.LOBBY );

        if ( optimal == null || optimal.getServerInfo() == null )
        {
            event.setCancelled( true );

            LanguageUtils.sendLangMessage( event.getPlayer(), "hubbalancer.no-lobbies-found" );
        }
        else
        {
            event.setTarget( ( (BungeeServer) optimal.getServerInfo() ).getServerInfo() );
        }
    }
}
