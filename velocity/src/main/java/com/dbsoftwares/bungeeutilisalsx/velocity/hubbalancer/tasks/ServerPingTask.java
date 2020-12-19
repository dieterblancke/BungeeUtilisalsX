package com.dbsoftwares.bungeeutilisalsx.velocity.hubbalancer.tasks;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.velocity.utils.VelocityServer;

public class ServerPingTask implements Runnable
{

    @Override
    public void run()
    {
        BuX.getApi().getHubBalancer().getServers().forEach( server ->
        {
            final VelocityServer bungeeServer = (VelocityServer) server.getServerInfo();

            if ( bungeeServer == null )
            {
                BuX.getLogger().warning( "Could not find server " + server.getName() + ". Please check if this is the correct name!" );
            }
            else
            {
                bungeeServer.getRegisteredServer().ping().handle( ( serverPing, throwable ) ->
                {
                    server.setOnline( throwable == null );
                    return null;
                } );
            }
        } );
    }
}
