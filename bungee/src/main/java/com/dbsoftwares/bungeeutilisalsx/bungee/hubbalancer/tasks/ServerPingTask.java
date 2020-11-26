package com.dbsoftwares.bungeeutilisalsx.bungee.hubbalancer.tasks;

import com.dbsoftwares.bungeeutilisalsx.bungee.utils.BungeeServer;
import com.dbsoftwares.bungeeutilisalsx.common.BuX;

public class ServerPingTask implements Runnable
{

    @Override
    public void run()
    {
        BuX.getApi().getHubBalancer().getServers().forEach( server ->
        {
            final BungeeServer bungeeServer = server.getServerInfo();

            if ( bungeeServer == null )
            {
                BuX.getLogger().warning( "Could not find server " + server.getName() + ". Please check if this is the correct name!" );
            }
            else
            {
                bungeeServer.getServerInfo().ping( ( serverPing, throwable ) -> server.setOnline( throwable == null ) );
            }
        } );
    }
}
