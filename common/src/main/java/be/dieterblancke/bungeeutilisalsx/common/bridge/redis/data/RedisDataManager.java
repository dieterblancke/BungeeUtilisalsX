package be.dieterblancke.bungeeutilisalsx.common.bridge.redis.data;

import be.dieterblancke.bungeeutilisalsx.common.api.bridge.redis.IRedisDataManager;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.redis.RedisManager;
import be.dieterblancke.bungeeutilisalsx.common.api.cache.CacheHelper;
import com.google.common.cache.Cache;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class RedisDataManager implements IRedisDataManager
{

    private final RedisManager redisManager;
    private final Cache<String, Integer> domainCountCache = CacheHelper.<String, Integer>builder()
            .build( builder -> {
                builder.maximumSize( 100 );
                builder.expireAfterWrite( 3, TimeUnit.MINUTES );
            } );

    @Override
    public int getAmountOfOnlineUsersOnDomain( final String domain )
    {
        RedisDataConstants.DOMAIN_PREFIX +


        final String key = PREFIX_USER + user.getUniqueId();
        final String proxyOnlineKey = PREFIX_PROXY_ONLINE + user.getProxy().getId();

        this.redisManager.execute( commands ->
        {
            commands.hdel( key, FIELD_USER_NAME, FIELD_USER_IP, FIELD_USER_PROXY, FIELD_USER_SERVER );
            commands.srem( proxyOnlineKey, user.getUniqueId().toString() );
        } );

        return 0;
    }

    private static final class RedisDataConstants {
        private static final String DOMAIN_PREFIX = "domain:";
    }
}
