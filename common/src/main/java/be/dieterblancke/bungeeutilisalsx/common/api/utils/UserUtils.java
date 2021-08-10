package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import be.dieterblancke.bungeeutilisalsx.common.BuX;

public class UserUtils
{

    public static long getOnlinePlayersOnDomain( final String domain )
    {
        final long amount;

        if ( BuX.getInstance().isRedisManagerEnabled() )
        {
            amount = BuX.getInstance().getRedisManager().getDataManager().getAmountOfOnlineUsersOnDomain( domain );
        }
        else
        {
            amount = BuX.getApi().getUsers().stream().filter( user -> user.getJoinedHost().equalsIgnoreCase( domain ) ).count();
        }

        return amount;
    }
}
