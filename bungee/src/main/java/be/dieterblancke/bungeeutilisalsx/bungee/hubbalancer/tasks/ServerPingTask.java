package be.dieterblancke.bungeeutilisalsx.bungee.hubbalancer.tasks;

import be.dieterblancke.bungeeutilisalsx.bungee.utils.BungeeServer;
import be.dieterblancke.bungeeutilisalsx.common.BuX;

public class ServerPingTask implements Runnable
{

    @Override
    public void run()
    {
        BuX.getApi().getHubBalancer().getServers().forEach( server ->
        {
            final BungeeServer bungeeServer = (BungeeServer) server.getServerInfo();

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
