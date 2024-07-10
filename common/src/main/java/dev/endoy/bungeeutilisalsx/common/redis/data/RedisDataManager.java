package dev.endoy.bungeeutilisalsx.common.redis.data;

import dev.endoy.bungeeutilisalsx.common.api.cache.CacheHelper;
import dev.endoy.bungeeutilisalsx.common.api.redis.IRedisDataManager;
import dev.endoy.bungeeutilisalsx.common.api.redis.PartyDataManager;
import dev.endoy.bungeeutilisalsx.common.api.redis.RedisManager;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import com.google.common.base.MoreObjects;
import com.google.common.cache.LoadingCache;
import lombok.Getter;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Getter
public class RedisDataManager implements IRedisDataManager
{

    private final RedisManager redisManager;
    private final PartyDataManager redisPartyDataManager;
    private final LoadingCache<String, Long> domainCountCache = CacheHelper.<String, Long>builder()
            .build( builder ->
            {
                builder.maximumSize( 250 );
                builder.expireAfterWrite( 3, TimeUnit.MINUTES );
            }, this::getAmountOfOnlineUsersOnDomainUncached );

    public RedisDataManager( final RedisManager redisManager )
    {
        this.redisManager = redisManager;
        this.redisPartyDataManager = new RedisPartyDataManager( redisManager );
    }

    @Override
    public void loadRedisUser( final User user )
    {
        final String uuid = user.getUuid().toString();
        final String domain = user.getJoinedHost();

        this.redisManager.executeAsync( commands ->
        {
            commands.sadd( RedisDataConstants.DOMAIN_PREFIX + domain, uuid );
        } );
    }

    @Override
    public void unloadRedisUser( final User user )
    {
        final String uuid = user.getUuid().toString();
        final String domain = user.getJoinedHost();

        this.redisManager.executeAsync( commands ->
        {
            commands.srem( RedisDataConstants.DOMAIN_PREFIX + domain, uuid );
        } );
    }

    @Override
    public long getAmountOfOnlineUsersOnDomain( final String domain )
    {
        try
        {
            return domainCountCache.get( domain );
        }
        catch ( ExecutionException e )
        {
            e.printStackTrace();
            return this.getAmountOfOnlineUsersOnDomainUncached( domain );
        }
    }

    @Override
    public boolean attemptShedLock( final String type, final int period, final TimeUnit unit )
    {
        final Long lastExecute = this.redisManager.execute( commands ->
        {
            final String str = commands.get( RedisDataConstants.SHEDLOCK_PREFIX + type );

            return str == null ? null : Long.parseLong( str );
        } );

        if ( MoreObjects.firstNonNull( lastExecute, 0L ) + unit.toMillis( period ) > System.currentTimeMillis() )
        {
            return false;
        }

        this.redisManager.executeAsync( commands ->
        {
            commands.set( RedisDataConstants.SHEDLOCK_PREFIX + type, String.valueOf( System.currentTimeMillis() ) );
        } );

        return true;
    }

    private long getAmountOfOnlineUsersOnDomainUncached( final String domain )
    {
        return redisManager.execute( commands ->
        {
            return commands.scard( RedisDataConstants.DOMAIN_PREFIX + domain );
        } );
    }
}
