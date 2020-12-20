package be.dieterblancke.bungeeutilisalsx.common.storage;

import be.dieterblancke.bungeeutilisalsx.common.storage.hikari.PostgreSQLStorageManager;
import org.testcontainers.containers.GenericContainer;

public class TestPostgreSQLStorageManager extends PostgreSQLStorageManager {

    private static GenericContainer<?> postgresSqlContainer;

    public static GenericContainer<?> init()
    {
        postgresSqlContainer = new GenericContainer<>( "postgres:latest" )
                .withEnv( "POSTGRES_DB", "database" )
                .withEnv( "POSTGRES_USER", "username" )
                .withEnv( "POSTGRES_PASSWORD", "password" )
                .withExposedPorts( 5432 );
        postgresSqlContainer.start();
        return postgresSqlContainer;
    }

    @Override
    public void close()
    {
        postgresSqlContainer.close();
    }
}
