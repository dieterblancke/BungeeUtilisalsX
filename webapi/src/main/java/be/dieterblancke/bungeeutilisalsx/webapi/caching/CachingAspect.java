package be.dieterblancke.bungeeutilisalsx.webapi.caching;

import be.dieterblancke.bungeeutilisalsx.common.api.cache.CacheHelper;
import com.google.common.cache.Cache;
import lombok.Value;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class CachingAspect
{

    private static final Cache<CachingKey, Object> CACHE = CacheHelper.<CachingKey, Object>builder()
            .build( builder ->
            {
                builder.maximumSize( 100000 );
                builder.expireAfterWrite( 10, TimeUnit.MINUTES );
            } );

    @Around( "@annotation(cacheable)" )
    public Object onCacheableExecution( final ProceedingJoinPoint joinPoint, final Cacheable cacheable ) throws Throwable
    {
        final String method = joinPoint.getSignature().toShortString();
        final Object[] args = joinPoint.getArgs();
        final CachingKey cachingKey = new CachingKey( method, args );

        // TODO
        System.out.println( "\n=======> Executing @Around on method: " + method + ", " + Arrays.toString( args ) );

        return joinPoint.proceed();
    }

    @Value
    public static class CachingKey
    {
        String method;
        Object[] args;
    }
}
