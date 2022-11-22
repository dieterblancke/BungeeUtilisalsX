package be.dieterblancke.bungeeutilisalsx.common;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.Platform;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.commands.CommandManager;
import be.dieterblancke.bungeeutilisalsx.common.job.SingleProxyJobManager;
import be.dieterblancke.bungeeutilisalsx.common.party.SimplePartyManager;
import be.dieterblancke.bungeeutilisalsx.common.redis.RedisManagerFactory;
import be.dieterblancke.bungeeutilisalsx.common.util.TestInjectionUtil;
import be.dieterblancke.configuration.yaml.YamlConfiguration;
import lombok.SneakyThrows;
import org.mockito.Mockito;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

import static org.mockito.Mockito.mock;

public class TestBungeeUtilisalsX extends AbstractBungeeUtilisalsX
{

    private final Logger LOGGER = Logger.getLogger( "BungeeUtilisalsX" );

    @Override
    @SneakyThrows
    public void initialize()
    {
        if ( ConfigFiles.CONFIG.getConfig() == null )
        {
            TestInjectionUtil.injectConfiguration(
                    ConfigFiles.CONFIG,
                    new YamlConfiguration( BuXTest.class.getResourceAsStream( "/configurations/config.yml" ) )
            );
        }
        this.api = mock( IBuXApi.class, Mockito.RETURNS_DEEP_STUBS );

        if ( ConfigFiles.CONFIG.getConfig().getBoolean( "multi-proxy.enabled" ) )
        {
            try
            {
                this.setRedisManager( RedisManagerFactory.create() );
            }
            catch ( Exception e )
            {
                // do nothing
            }
        }
        this.setJobManager( new SingleProxyJobManager() );

        if ( ConfigFiles.PARTY_CONFIG.getConfig() != null )
        {
            this.setPartyManager( new SimplePartyManager() );
        }
    }

    @Override
    protected IBuXApi createBuXApi()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public CommandManager getCommandManager()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void loadPlaceHolders()
    {
        // do nothing
    }

    @Override
    protected void registerLanguages()
    {
        // do nothing
    }

    @Override
    protected void registerListeners()
    {
        // do nothing
    }

    @Override
    protected void registerPluginSupports()
    {

    }

    @Override
    public ProxyOperationsApi proxyOperations()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getDataFolder()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getVersion()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<StaffUser> getStaffMembers()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public IPluginDescription getDescription()
    {
        throw new UnsupportedOperationException();
    }

    @Override
    public Logger getLogger()
    {
        return LOGGER;
    }

    @Override
    public Platform getPlatform()
    {
        return null;
    }

    @Override
    protected void registerMetrics()
    {
        // do nothing
    }
}
