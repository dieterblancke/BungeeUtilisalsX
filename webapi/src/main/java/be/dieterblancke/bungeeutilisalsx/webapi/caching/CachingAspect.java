package be.dieterblancke.bungeeutilisalsx.webapi.caching;

import be.dieterblancke.bungeeutilisalsx.common.api.cache.CacheHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.google.common.cache.Cache;
import lombok.Value;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class CachingAspect
{

    private static final Object NULL = new Object();
    private static final Cache<CachingKey, Object> CACHE = CacheHelper.<CachingKey, Object>builder()
            .build( builder ->
            {
                builder.maximumSize( 100000 );
                builder.expireAfterWrite( ConfigFiles.CONFIG.getConfig().getInteger( "cache-duration-minutes" ), TimeUnit.MINUTES );
            } );

    public static void clearCache()
    {
        CACHE.invalidateAll();
    }

    @Around( "@annotation(cacheable)" )
    public Object onCacheableExecution( final ProceedingJoinPoint joinPoint, final Cacheable cacheable ) throws Throwable
    {
        final String method = joinPoint.getSignature().toShortString();
        final Object[] args = joinPoint.getArgs();
        final CachingKey cachingKey = new CachingKey( method, args );
        final Object result = CACHE.get( cachingKey, () ->
        {
            try
            {
                final Object obj = joinPoint.proceed();

                return obj == null ? NULL : obj;
            }
            catch ( Throwable e )
            {
                e.printStackTrace();
                throw new ExecutionException( e );
            }
        } );

        return result == NULL ? null : result;
    }

    @Value
    public static class CachingKey
    {
        String method;
        Object[] args;
    }
}
