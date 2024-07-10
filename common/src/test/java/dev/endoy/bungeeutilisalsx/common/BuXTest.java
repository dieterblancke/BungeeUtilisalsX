package dev.endoy.bungeeutilisalsx.common;

import dev.endoy.bungeeutilisalsx.common.storage.TestRedisContainer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.sql.SQLException;

public class BuXTest
{

    protected static final TestRedisContainer TEST_REDIS_CONTAINER = new TestRedisContainer();

    protected AbstractBungeeUtilisalsX bungeeUtilisalsX;

    public BuXTest( final boolean shouldInitialize )
    {
        bungeeUtilisalsX = new TestBungeeUtilisalsX();

        if ( shouldInitialize )
        {
            bungeeUtilisalsX.initialize();
        }
    }

    @BeforeAll
    static void setupRedis()
    {
        TEST_REDIS_CONTAINER.initialize();
    }

    @AfterAll
    static void shutdownRedis()
    {
        TEST_REDIS_CONTAINER.shutdown();
    }

    public void shutdown() throws SQLException
    {
        bungeeUtilisalsX.getAbstractStorageManager().close();
    }
}
