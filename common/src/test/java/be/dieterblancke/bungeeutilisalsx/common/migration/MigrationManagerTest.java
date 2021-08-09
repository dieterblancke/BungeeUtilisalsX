package be.dieterblancke.bungeeutilisalsx.common.migration;

import be.dieterblancke.bungeeutilisalsx.common.BuXTest;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.storage.TestMariaDBStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.storage.TestMySQLStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.storage.TestPostgreSQLStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.storage.TestSQLiteStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.util.TestInjectionUtil;
import com.dbsoftwares.configuration.api.IConfiguration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import java.sql.SQLException;

class MigrationManagerTest extends BuXTest
{

    private static final int DEFAULT_MYSQL_PORT = 3306;
    private static final int DEFAULT_POSTGRESQL_PORT = 5432;

    public MigrationManagerTest()
    {
        super( false );
    }

    @BeforeEach
    void injectConfiguration() throws NoSuchFieldException, IllegalAccessException
    {
        TestInjectionUtil.injectConfiguration(
                ConfigFiles.CONFIG,
                IConfiguration.loadYamlConfiguration( this.getClass().getResourceAsStream( "/config.yml" ) )
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

            final MigrationManager migrationManager = new MigrationManager();
            migrationManager.initialize();
            migrationManager.migrate();
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

            final MigrationManager migrationManager = new MigrationManager();
            migrationManager.initialize();
            migrationManager.migrate();
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

            final MigrationManager migrationManager = new MigrationManager();
            migrationManager.initialize();
            migrationManager.migrate();
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

            final MigrationManager migrationManager = new MigrationManager();
            migrationManager.initialize();
            migrationManager.migrate();
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

            final MigrationManager migrationManager = new MigrationManager();
            migrationManager.initialize();
            migrationManager.migrate();

            // this is essentially what happens on restart, migrations are ran again, this is to make sure the migrations don't run multiple times.
            // if they do, it will throw an SQLException and fail
            migrationManager.initialize();
            migrationManager.migrate();

            migrationManager.initialize();
            migrationManager.migrate();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
            Assertions.fail();
        }
    }
}