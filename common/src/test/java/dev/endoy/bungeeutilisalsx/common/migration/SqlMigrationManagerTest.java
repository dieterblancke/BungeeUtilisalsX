package dev.endoy.bungeeutilisalsx.common.migration;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.BuXTest;
import dev.endoy.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.migration.sql.SqlMigrationManager;
import dev.endoy.bungeeutilisalsx.common.storage.TestMariaDBStorageManager;
import dev.endoy.bungeeutilisalsx.common.storage.TestMySQLStorageManager;
import dev.endoy.bungeeutilisalsx.common.storage.TestPostgreSQLStorageManager;
import dev.endoy.bungeeutilisalsx.common.storage.TestSQLiteStorageManager;
import dev.endoy.bungeeutilisalsx.common.util.TestInjectionUtil;
import dev.endoy.configuration.api.IConfiguration;
import org.junit.jupiter.api.*;
import org.mockito.stubbing.Answer;
import org.testcontainers.containers.GenericContainer;

import java.sql.SQLException;

import static org.mockito.Mockito.when;

@Disabled
class SqlMigrationManagerTest extends BuXTest
{

    private static final int DEFAULT_MYSQL_PORT = 3306;
    private static final int DEFAULT_POSTGRESQL_PORT = 5432;

    public SqlMigrationManagerTest()
    {
        super( true );

        when( BuX.getApi().getStorageManager() )
                .thenAnswer( (Answer<AbstractStorageManager>) invocationOnMock -> BuX.getInstance().getAbstractStorageManager() );
    }

    @BeforeEach
    void injectConfiguration() throws NoSuchFieldException, IllegalAccessException
    {
        TestInjectionUtil.injectConfiguration(
                ConfigFiles.CONFIG,
                IConfiguration.loadYamlConfiguration( this.getClass().getResourceAsStream( "/configurations/config.yml" ) )
        );
    }

    @AfterEach
    void shutdownContainer() throws SQLException
    {
        super.shutdown();
    }

    @Test
    void testMigrationsOnMySQL()
    {
        try
        {
            final GenericContainer<?> container = TestMySQLStorageManager.init();

            ConfigFiles.CONFIG.getConfig().set( "storage.type", "MYSQL" );
            ConfigFiles.CONFIG.getConfig().set( "storage.hostname", container.getContainerIpAddress() );
            ConfigFiles.CONFIG.getConfig().set( "storage.port", container.getMappedPort( DEFAULT_MYSQL_PORT ) );

            TestInjectionUtil.injectStorageManager( new TestMySQLStorageManager() );

            final SqlMigrationManager sqlMigrationManager = new SqlMigrationManager();
            sqlMigrationManager.initialize();
            sqlMigrationManager.migrate();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    void testMigrationsOnPostgreSQL()
    {
        try
        {
            final GenericContainer<?> container = TestPostgreSQLStorageManager.init();

            ConfigFiles.CONFIG.getConfig().set( "storage.type", "POSTGRESQL" );
            ConfigFiles.CONFIG.getConfig().set( "storage.hostname", container.getContainerIpAddress() );
            ConfigFiles.CONFIG.getConfig().set( "storage.port", container.getMappedPort( DEFAULT_POSTGRESQL_PORT ) );

            TestInjectionUtil.injectStorageManager( new TestPostgreSQLStorageManager() );

            final SqlMigrationManager sqlMigrationManager = new SqlMigrationManager();
            sqlMigrationManager.initialize();
            sqlMigrationManager.migrate();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    void testMigrationsOnMariaDB()
    {
        try
        {
            final GenericContainer<?> container = TestMariaDBStorageManager.init();

            ConfigFiles.CONFIG.getConfig().set( "storage.type", "MARIADB" );
            ConfigFiles.CONFIG.getConfig().set( "storage.hostname", container.getContainerIpAddress() );
            ConfigFiles.CONFIG.getConfig().set( "storage.port", container.getMappedPort( DEFAULT_MYSQL_PORT ) );

            TestInjectionUtil.injectStorageManager( new TestMariaDBStorageManager() );

            final SqlMigrationManager sqlMigrationManager = new SqlMigrationManager();
            sqlMigrationManager.initialize();
            sqlMigrationManager.migrate();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    void testMigrationsOnSqLite()
    {
        try
        {
            ConfigFiles.CONFIG.getConfig().set( "storage.type", "SQLITE" );
            TestInjectionUtil.injectStorageManager( new TestSQLiteStorageManager() );

            final SqlMigrationManager sqlMigrationManager = new SqlMigrationManager();
            sqlMigrationManager.initialize();
            sqlMigrationManager.migrate();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            Assertions.fail();
        }
    }

    @Test
    void testMultipleMigrationRunsOnSQLite()
    {
        try
        {
            ConfigFiles.CONFIG.getConfig().set( "storage.type", "SQLITE" );
            TestInjectionUtil.injectStorageManager( new TestSQLiteStorageManager() );

            final SqlMigrationManager sqlMigrationManager = new SqlMigrationManager();
            sqlMigrationManager.initialize();
            sqlMigrationManager.migrate();

            // this is essentially what happens on restart, migrations are ran again, this is to make sure the migrations don't run multiple times.
            // if they do, it will throw an SQLException and fail
            sqlMigrationManager.initialize();
            sqlMigrationManager.migrate();

            sqlMigrationManager.initialize();
            sqlMigrationManager.migrate();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            Assertions.fail();
        }
    }
}