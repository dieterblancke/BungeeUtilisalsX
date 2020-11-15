package com.dbsoftwares.bungeeutilisalsx.bungee.utils;

import com.dbsoftwares.bungeeutilisalsx.common.api.utils.Utils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class UserUtils
{

    public static String getJoinedHost( final ProxiedPlayer player )
    {
        final String joinedHost;
        if ( player.getPendingConnection().getVirtualHost() == null )
        {
            joinedHost = null;
        }
        else
        {
            if ( player.getPendingConnection().getVirtualHost().getHostName() == null )
            {
                joinedHost = Utils.getIP( player.getPendingConnection().getVirtualHost().getAddress() );
            }
            else
            {
                joinedHost = player.getPendingConnection().getVirtualHost().getHostName();
            }
        }
        return joinedHost;
    }

    public static long getOnlinePlayersOnDomain( final String domain )
    {
        int amount = 0;

        for ( ProxiedPlayer player : ProxyServer.getInstance().getPlayers() )
        {
            if ( getJoinedHost( player ).equalsIgnoreCase( domain ) )
            {
                amount++;
            }
        }
        // TODO: find a solution to reliably get online players on domain on other redis servers

        return amount;
    }
}
