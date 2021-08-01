package be.dieterblancke.bungeeutilisalsx.common.storage;

import be.dieterblancke.bungeeutilisalsx.common.storage.hikari.MariaDBStorageManager;
import org.testcontainers.containers.GenericContainer;

public class TestMariaDBStorageManager extends MariaDBStorageManager
{

    private static GenericContainer<?> mariaDbContainer;

    public static GenericContainer<?> init()
    {
        mariaDbContainer = new GenericContainer<>( "mariadb:latest" )
                .withEnv( "MYSQL_DATABASE", "database" )
                .withEnv( "MYSQL_USER", "username" )
                .withEnv( "MYSQL_PASSWORD", "password" )
                .withEnv( "MYSQL_ALLOW_EMPTY_PASSWORD", "yes" )
                .withExposedPorts( 3306 );
        mariaDbContainer.start();
        return mariaDbContainer;
    }

    @Override
    public void close()
    {
        mariaDbContainer.close();
    }
}
