package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

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
        // preferably use a hset (similar to ProxySync) and use ProxySync to detect if a player needs to be cleaned.

        return amount;
    }
}
