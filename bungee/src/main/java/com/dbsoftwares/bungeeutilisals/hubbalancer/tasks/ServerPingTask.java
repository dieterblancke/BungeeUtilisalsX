package com.dbsoftwares.bungeeutilisals.hubbalancer.tasks;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import net.md_5.bungee.api.config.ServerInfo;

public class ServerPingTask implements Runnable
{

    @Override
    public void run()
    {
        BUCore.getApi().getHubBalancer().getServers().forEach( server ->
        {
            final ServerInfo info = server.getServerInfo();

            if ( info == null )
            {
                BUCore.getLogger().warning( "Could not find server " + server.getName() + ". Please check if this is the correct name!" );
            }
            else
            {
                info.ping( ( serverPing, throwable ) -> server.setOnline( throwable == null ) );
            }
        } );
    }
}
