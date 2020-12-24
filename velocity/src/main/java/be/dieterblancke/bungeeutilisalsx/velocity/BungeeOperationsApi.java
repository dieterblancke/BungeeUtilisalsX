package be.dieterblancke.bungeeutilisalsx.velocity;

import be.dieterblancke.bungeeutilisalsx.common.ProxyOperationsApi;
import be.dieterblancke.bungeeutilisalsx.common.api.command.Command;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.dump.PluginInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.velocity.utils.CommandHolder;
import be.dieterblancke.bungeeutilisalsx.velocity.utils.VelocityServer;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.meta.PluginDependency;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BungeeOperationsApi implements ProxyOperationsApi
{

    private final LoadingCache<String, IProxyServer> proxyServerCache = CacheBuilder.newBuilder()
            .expireAfterWrite( 1, TimeUnit.MINUTES )
            .build( new CacheLoader<String, IProxyServer>()
            {
                @Override
                public IProxyServer load( @Nonnull final String serverName )
                {
                    return Bootstrap.getInstance().getProxyServer()
                            .getServer( serverName )
                            .map( VelocityServer::new )
                            .orElseThrow( () -> new RuntimeException( "Could not find server " + serverName + "!" ) );
                }
            } );

    private final Map<Command, CommandHolder> commandHolders = new HashMap<>();

    @Override
    public void registerCommand( final Command command )
    {
        if ( !commandHolders.containsKey( command ) )
        {
            final CommandHolder commandHolder = new CommandHolder( command );
            final CommandManager commandManager = Bootstrap.getInstance().getProxyServer().getCommandManager();
            final CommandMeta meta = commandManager.metaBuilder( command.getName() ).aliases( command.getAliases() ).build();

            commandManager.register( meta, commandHolder );
            commandHolders.put( command, commandHolder );
        }
    }

    @Override
    public void unregisterCommand( final Command command )
    {
        if ( commandHolders.containsKey( command ) )
        {
            this.commandHolders.remove( command );
            Bootstrap.getInstance().getProxyServer().getCommandManager().unregister( command.getName() );
        }
    }

    @Override
    public List<IProxyServer> getServers()
    {
        return Bootstrap.getInstance().getProxyServer()
                .getAllServers()
                .stream()
                .map( RegisteredServer::getServerInfo )
                .map( ServerInfo::getName )
                .map( this::getServerInfo )
                .collect( Collectors.toList() );
    }

    @Override
    public IProxyServer getServerInfo( final String serverName )
    {
        try
        {
            return this.proxyServerCache.get( serverName );
        }
        catch ( ExecutionException e )
        {
            return Bootstrap.getInstance().getProxyServer()
                    .getServer( serverName )
                    .map( VelocityServer::new )
                    .orElse( null );
        }
    }

    @Override
    public String getProxyIdentifier()
    {
        return Bootstrap.getInstance().getProxyServer().getVersion().getName()
                + " " + Bootstrap.getInstance().getProxyServer().getVersion().getVendor()
                + " " + Bootstrap.getInstance().getProxyServer().getVersion().getVersion();
    }

    @Override
    public List<PluginInfo> getPlugins()
    {
        return Bootstrap.getInstance().getProxyServer().getPluginManager().getPlugins()
                .stream()
                .map( PluginContainer::getDescription )
                .map( desc -> new PluginInfo(
                        desc.getName().orElse( "" ),
                        desc.getVersion().orElse( "" ),
                        String.join( ", ", desc.getAuthors() ),
                        desc.getDependencies().stream()
                                .filter( dep -> !dep.isOptional() )
                                .map( PluginDependency::getId )
                                .collect( Collectors.toSet() ),
                        desc.getDependencies().stream()
                                .filter( PluginDependency::isOptional )
                                .map( PluginDependency::getId )
                                .collect( Collectors.toSet() ),
                        desc.getDescription().orElse( "" )
                ) )
                .collect( Collectors.toList() );
    }
}
