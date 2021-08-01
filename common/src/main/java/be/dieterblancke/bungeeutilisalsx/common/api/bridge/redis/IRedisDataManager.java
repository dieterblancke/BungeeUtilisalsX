package be.dieterblancke.bungeeutilisalsx.common.api.bridge.redis;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;

public interface IRedisDataManager
{

    void loadRedisUser( User user );

    void unloadRedisUser( User user );

    long getAmountOfOnlineUsersOnDomain( String domain );

}
