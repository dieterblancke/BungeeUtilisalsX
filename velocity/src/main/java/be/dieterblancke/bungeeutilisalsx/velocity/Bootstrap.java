package be.dieterblancke.bungeeutilisalsx.velocity;

import be.dieterblancke.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.common.BootstrapUtil;
import be.dieterblancke.bungeeutilisalsx.velocity.library.VelocityLibraryClassLoader;
import be.dieterblancke.bungeeutilisalsx.velocity.utils.Slf4jLoggerWrapper;
import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Dependency;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import lombok.Getter;
import org.bstats.velocity.Metrics;
import org.bstats.velocity.Metrics.Factory;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Plugin(
        id = "bungeeutilisalsx",
        name = "BungeeUtilisalsX",
        version = "2.1.4",
        authors = { "didjee2" },
        dependencies = {
                @Dependency( id = "skinsrestorer", optional = true ),
                @Dependency( id = "protocolize", optional = true )
        }
)
public class Bootstrap
{

    @Getter
    private static Bootstrap instance;
    @Getter
    private final ProxyServer proxyServer;
    @Getter
    private final Logger logger;
    @Getter
    private final File dataFolder;
    private final Factory metricsFactory;
    private AbstractBungeeUtilisalsX abstractBungeeUtilisalsX;

    @Inject
    public Bootstrap( final ProxyServer proxyServer,
                      final org.slf4j.Logger logger,
                      final @DataDirectory Path dataDirectory,
                      final Factory metricsFactory )
    {
        instance = this;
        this.proxyServer = proxyServer;
        this.logger = LogManager.getLogManager().getLogger( "" );
        for ( Handler handler : this.logger.getHandlers() )
        {
            this.logger.removeHandler( handler );
        }
        this.logger.addHandler( new Slf4jLoggerWrapper( logger ) );
        this.dataFolder = dataDirectory.toFile();
        this.metricsFactory = metricsFactory;
    }

    @Subscribe
    public void onProxyInitialization( final ProxyInitializeEvent event )
    {
        BootstrapUtil.loadLibraries( this.getDataFolder(), new VelocityLibraryClassLoader(), logger );

        abstractBungeeUtilisalsX = new BungeeUtilisalsX();
        abstractBungeeUtilisalsX.initialize();
    }

    @Subscribe
    public void onProxyShutdown( final ProxyShutdownEvent event )
    {
        abstractBungeeUtilisalsX.shutdown();
    }

    protected Metrics createMetrics()
    {
        return metricsFactory.make( this, 13139 );
    }
}
