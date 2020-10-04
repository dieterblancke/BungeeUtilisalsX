package com.dbsoftwares.bungeeutilisals.hubbalancer.listeners;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.other.hubbalancer.ServerData;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisals.api.utils.text.LanguageUtils;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

import java.util.Optional;

public class JoinListener implements Listener
{

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLogin( ServerConnectEvent event )
    {
        final ProxiedPlayer player = event.getPlayer();
        final ServerInfo target = event.getTarget();

        if ( !ConfigFiles.HUBBALANCER.getConfig().getStringList( "triggers" ).contains( target.getName() ) )
        {
            return;
        }
        final Optional<User> optionalUser = BUCore.getApi().getUser( player );
        if ( optionalUser.isPresent() )
        {
            final User user = optionalUser.get();

            if ( user.getStorage().hasData( "HUBBALANCER_NO_REDIRECT" ) )
            {
                user.getStorage().removeData( "HUBBALANCER_NO_REDIRECT" );
                return;
            }
        }

        final ServerData optimal = BUCore.getApi().getHubBalancer().findBestServer( ServerData.ServerType.LOBBY );

        if ( optimal == null || optimal.getServerInfo() == null )
        {
            event.setCancelled( true );

            LanguageUtils.sendLangMessage( event.getPlayer(), "hubbalancer.no-lobbies-found" );
        }
        else
        {
            event.setTarget( optimal.getServerInfo() );
        }
    }
}
