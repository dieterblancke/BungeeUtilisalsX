package be.dieterblancke.bungeeutilisalsx.common.party;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.BuXTest;
import be.dieterblancke.bungeeutilisalsx.common.api.party.Party;
import be.dieterblancke.bungeeutilisalsx.common.api.party.exceptions.AlreadyInPartyException;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.storage.TestRedisContainer;
import be.dieterblancke.bungeeutilisalsx.common.util.TestInjectionUtil;
import com.dbsoftwares.configuration.yaml.YamlConfiguration;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SimplePartyManagerTest extends BuXTest
{

    private static final TestRedisContainer TEST_REDIS_CONTAINER = new TestRedisContainer();

    public SimplePartyManagerTest()
    {
        super( false );
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

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException, IOException
    {
        TestInjectionUtil.injectConfiguration(
                ConfigFiles.CONFIG,
                new YamlConfiguration( BuXTest.class.getResourceAsStream( "/config.yml" ) )
        );
        TestInjectionUtil.injectConfiguration(
                ConfigFiles.PARTY_CONFIG,
                new YamlConfiguration( BuXTest.class.getResourceAsStream( "/party/config.yml" ) )
        );

        ConfigFiles.CONFIG.getConfig().set( "multi-proxy.enabled", true );
        ConfigFiles.CONFIG.getConfig().set(
                "multi-proxy.redis.uri",
                "redis://redis@127.0.0.1:" + TEST_REDIS_CONTAINER.getRedisContainer().getMappedPort( 6379 )
        );
        ConfigFiles.PARTY_CONFIG.getConfig().set( "enabled", true );

        this.bungeeUtilisalsX.initialize();
    }

    @Test
    @DisplayName( "Test creation of party and if party exists" )
    void testCreateParty1() throws AlreadyInPartyException, InterruptedException
    {
        final User user = mock( User.class );
        when( user.getUuid() ).thenReturn( UUID.randomUUID() );
        when( user.getName() ).thenReturn( "test" );
        when( user.hasPermission( any() ) ).thenReturn( true );

        BuX.getInstance().getPartyManager().createParty( user );

        Thread.sleep( 2500 );

        Assertions.assertTrue( BuX.getInstance().getPartyManager().getCurrentPartyFor( "test" ).isPresent() );

        final List<Party> partyList = BuX.getInstance().getRedisManager().getDataManager().getRedisPartyDataManager().getAllParties();
        Assertions.assertEquals( partyList.size(), 1 );
        Assertions.assertEquals( partyList.get( 0 ).getPartyMembers().size(), 1 );
        Assertions.assertEquals( partyList.get( 0 ).getOwner().getUserName(), "test" );
    }
}