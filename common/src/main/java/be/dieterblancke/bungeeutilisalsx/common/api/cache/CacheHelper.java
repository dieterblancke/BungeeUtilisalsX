package be.dieterblancke.bungeeutilisalsx.common.api.cache;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.SimpleCacheLoader;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class CacheHelper
{

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newSingleThreadScheduledExecutor();
    private static final List<Cache<?, ?>> CACHES = new ArrayList<>();

    static
    {
        // Cleanup to ensure that all caches are cleaned up regularly.
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate( () ->
        {
            for ( Cache<?, ?> cache : CACHES )
            {
                cache.cleanUp();
            }
        }, 30, 30, TimeUnit.SECONDS );
    }

    private static <K, V> Cache<K, V> register( final Cache<K, V> cache )
    {
        CACHES.add( cache );
        return cache;
    }

    private static <K, V> LoadingCache<K, V> register( final LoadingCache<K, V> cache )
    {
        CACHES.add( cache );
        return cache;
    }

    public static <K, V> CacheBuilderHelper<K, V> builder()
    {
        return new CacheBuilderHelper<>();
    }

    public static class CacheBuilderHelper<K, V>
    {

        private final CacheBuilder<K, V> builder = (CacheBuilder<K, V>) CacheBuilder.newBuilder();

        public Cache<K, V> build( final Consumer<CacheBuilder<K, V>> builderConsumer )
        {
            builderConsumer.accept( builder );
            return CacheHelper.register( builder.build() );
        }

        public LoadingCache<K, V> build( final Consumer<CacheBuilder<K, V>> builderConsumer, final CacheLoader<K, V> cacheLoader )
        {
            builderConsumer.accept( builder );
            return CacheHelper.register( builder.build( cacheLoader ) );
        }

        public LoadingCache<K, V> build( final Consumer<CacheBuilder<K, V>> builderConsumer, final SimpleCacheLoader<K, V> cacheLoader )
        {
            return build( builderConsumer, new CacheLoader<K, V>()
            {
                @Override
                public V load( @Nullable K k ) throws Exception
                {
                    return cacheLoader.load( k );
                }
            } );
        }
    }
}
