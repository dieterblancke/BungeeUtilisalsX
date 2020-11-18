package com.dbsoftwares.bungeeutilisalsx.common.api.utils;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;

public class UserUtils
{

    public static long getOnlinePlayersOnDomain( final String domain )
    {
        int amount = 0;

        for ( User user : BuX.getApi().getUsers() )
        {
            if ( user.getJoinedHost().equalsIgnoreCase( domain ) )
            {
                amount++;
            }
        }
        // TODO: find a solution to reliably get online players on domain on other redis servers

        return amount;
    }
}
