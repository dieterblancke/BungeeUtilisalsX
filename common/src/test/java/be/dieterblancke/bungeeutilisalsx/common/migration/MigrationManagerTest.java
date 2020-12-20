package be.dieterblancke.bungeeutilisalsx.common.migration;

import be.dieterblancke.bungeeutilisalsx.common.BuXTest;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.storage.TestMySQLStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.util.TestInjectionUtil;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import java.sql.SQLException;

class MigrationManagerTest extends BuXTest
{

    private static final int DEFAULT_MYSQL_PORT = 3306;

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

        final ISection section = ConfigFiles.CONFIG.getConfig().getSection( "storage.schemas" );

        for ( String key : section.getKeys() )
        {
            PlaceHolderAPI.addPlaceHolder(
                    "{" + key + "-table}",
                    false,
                    event -> section.getString( key )
            );
        }
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
            final GenericContainer container = TestMySQLStorageManager.init();

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
}