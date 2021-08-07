package be.dieterblancke.bungeeutilisalsx.common.bridge.redis;

import be.dieterblancke.bungeeutilisalsx.common.api.redis.IRedisDataManager;
import be.dieterblancke.bungeeutilisalsx.common.api.redis.RedisManager;
import be.dieterblancke.bungeeutilisalsx.common.bridge.redis.data.RedisDataManager;
import com.dbsoftwares.configuration.api.ISection;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.cluster.api.async.RedisClusterAsyncCommands;
import io.lettuce.core.cluster.api.sync.RedisClusterCommands;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.support.ConnectionPoolSupport;
import lombok.Getter;
import org.apache.commons.pool2.impl.GenericObjectPool;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public class StandardRedisManager implements RedisManager
{

    private final RedisClient redisClient;
    private final GenericObjectPool<StatefulRedisConnection<String, String>> pool;
    private final StatefulRedisPubSubConnection<String, String> pubSubConnection;

    @Getter
    private final IRedisDataManager dataManager;

    public StandardRedisManager( final ISection section )
    {
        this.redisClient = RedisClient.create( section.getString( "uri" ) );
        this.pool = ConnectionPoolSupport.createGenericObjectPool(
                redisClient::connect,
                this.getObjectPoolConfig( section.getSection( "pooling" ) )
        );
        this.pubSubConnection = redisClient.connectPubSub();
        this.pubSubConnection.addListener( new PubSubListener() );
        this.dataManager = new RedisDataManager( this );
    }

    @Override
    public void execute( final Consumer<RedisClusterCommands<String, String>> consumer )
    {
        try ( StatefulRedisConnection<String, String> connection = pool.borrowObject() )
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
        try ( StatefulRedisConnection<String, String> connection = pool.borrowObject() )
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
        try ( StatefulRedisConnection<String, String> connection = pool.borrowObject() )
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
        try ( StatefulRedisConnection<String, String> connection = pool.borrowObject() )
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
