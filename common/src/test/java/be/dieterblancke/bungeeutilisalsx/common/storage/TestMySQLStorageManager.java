package be.dieterblancke.bungeeutilisalsx.common.storage;

import be.dieterblancke.bungeeutilisalsx.common.storage.hikari.MySQLStorageManager;
import org.testcontainers.containers.GenericContainer;

public class TestMySQLStorageManager extends MySQLStorageManager
{

    private static GenericContainer<?> mySQLContainer;

    public static GenericContainer<?> init()
    {
        mySQLContainer = new GenericContainer<>( "mysql:latest" )
                .withEnv( "MYSQL_DATABASE", "database" )
                .withEnv( "MYSQL_USER", "username" )
                .withEnv( "MYSQL_PASSWORD", "password" )
                .withEnv( "MYSQL_ALLOW_EMPTY_PASSWORD", "yes" )
                .withExposedPorts( 3306 );
        mySQLContainer.start();
        return mySQLContainer;
    }

    @Override
    public void close()
    {
        mySQLContainer.close();
    }
}
