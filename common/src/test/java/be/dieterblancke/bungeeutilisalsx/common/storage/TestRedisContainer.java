package be.dieterblancke.bungeeutilisalsx.common.storage;

import lombok.Data;
import org.testcontainers.containers.GenericContainer;

@Data
public class TestRedisContainer
{

    private GenericContainer<?> redisContainer;

    public void initialize()
    {
        redisContainer = new GenericContainer<>( "redis:latest" )
                .withExposedPorts( 6379 );
        redisContainer.start();
    }

    public void shutdown()
    {
        redisContainer.stop();
    }
}
