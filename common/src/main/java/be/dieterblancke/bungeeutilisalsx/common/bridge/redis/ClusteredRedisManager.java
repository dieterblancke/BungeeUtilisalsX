package be.dieterblancke.bungeeutilisalsx.common.bridge.redis;

import be.dieterblancke.bungeeutilisalsx.common.api.bridge.redis.RedisManager;
import com.dbsoftwares.configuration.api.ISection;
import io.lettuce.core.RedisURI;
import io.lettuce.core.cluster.RedisClusterClient;
import io.lettuce.core.cluster.api.StatefulRedisClusterConnection;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import io.lettuce.core.cluster.pubsub.StatefulRedisClusterPubSubConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ClusteredRedisManager implements RedisManager
{

    private final RedisClusterClient redisClient;
    private final GenericObjectPool<StatefulRedisClusterConnection<String, String>> pool;
    private final StatefulRedisClusterPubSubConnection<String, String> pubSubConnection;

    public ClusteredRedisManager( final ISection section )
    {
        final List<RedisURI> redisURIs = section.getSectionList( "redis" )
                .stream()
                .map( this::getRedisURI )
                .collect( Collectors.toList() );

        this.redisClient = RedisClusterClient.create( redisURIs );
        this.pool = ConnectionPoolSupport.createGenericObjectPool(
                redisClient::connect,
                this.getObjectPoolConfig( section )
        );
        this.pubSubConnection = redisClient.connectPubSub();
        this.pubSubConnection.addListener( new PubSubListener() );
    }

    @Override
    public void execute( final Consumer<RedisClusterCommands<String, String>> consumer )
    {
        try ( StatefulRedisClusterConnection<String, String> connection = pool.borrowObject() )
        {
            consumer.accept( connection.sync() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    @Override
    public <R> R execute( final Function<RedisClusterCommands<String, String>, R> function )
    {
        R result = null;
        try ( StatefulRedisClusterConnection<String, String> connection = pool.borrowObject() )
        {
            result = function.apply( connection.sync() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void executeAsync( final Consumer<RedisClusterAsyncCommands<String, String>> consumer )
    {
        try ( StatefulRedisClusterConnection<String, String> connection = pool.borrowObject() )
        {
            consumer.accept( connection.async() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }

    @Override
    public <R> CompletableFuture<R> executeAsync( final Function<RedisClusterAsyncCommands<String, String>, CompletableFuture<R>> function )
    {
        CompletableFuture<R> result = null;
        try ( StatefulRedisClusterConnection<String, String> connection = pool.borrowObject() )
        {
            result = function.apply( connection.async() );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void closeConnections()
    {
        pubSubConnection.close();
        pool.close();
        redisClient.shutdown();
    }

    @Override
    public void subscribeToChannels( String... channels )
    {
        this.pubSubConnection.sync().subscribe( channels );
    }

    @Override
    public void publishToChannel( String channel, String message )
    {
        this.pubSubConnection.async().publish( channel, message );
    }
}