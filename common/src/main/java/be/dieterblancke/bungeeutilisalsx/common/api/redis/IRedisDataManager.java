package be.dieterblancke.bungeeutilisalsx.common.api.redis;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.concurrent.TimeUnit;

public interface IRedisDataManager
{

    void loadRedisUser( User user );

    void unloadRedisUser( User user );

    long getAmountOfOnlineUsersOnDomain( String domain );

    IRedisPartyDataManager getRedisPartyDataManager();

    boolean attemptShedLock( String type, int period, TimeUnit unit );

    default boolean attemptShedLock( String type, int period, be.dieterblancke.bungeeutilisalsx.common.api.utils.TimeUnit unit )
    {
        return attemptShedLock( type, period, unit.toJavaTimeUnit() );
    }

}
