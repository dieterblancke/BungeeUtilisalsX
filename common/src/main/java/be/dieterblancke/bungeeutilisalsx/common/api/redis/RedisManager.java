package be.dieterblancke.bungeeutilisalsx.common.api.redis;

import com.dbsoftwares.configuration.api.ISection;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface RedisManager
{

    IRedisDataManager getDataManager();

    void execute( Consumer<RedisClusterCommands<String, String>> consumer );

    <R> R execute( Function<RedisClusterCommands<String, String>, R> function );

    void executeAsync( Consumer<RedisClusterAsyncCommands<String, String>> consumer );

    <R> CompletableFuture<R> executeAsync( Function<RedisClusterAsyncCommands<String, String>, CompletableFuture<R>> function );

    void closeConnections();

    void subscribeToChannels( String... channels );

    void publishToChannel( String channel, String message );

    default <T> GenericObjectPoolConfig<T> getObjectPoolConfig( final ISection section )
    {
        final GenericObjectPoolConfig<T> poolConfig = new GenericObjectPoolConfig<>();

        poolConfig.setMinIdle( section.getInteger( "min-idle" ) );
        poolConfig.setMaxIdle( section.getInteger( "max-idle" ) );
        poolConfig.setMaxTotal( section.getInteger( "max-total" ) );

        return poolConfig;
    }
}
