package dev.endoy.bungeeutilisalsx.velocity;

import dev.endoy.bungeeutilisalsx.common.ServerOperationsApi;
import dev.endoy.bungeeutilisalsx.common.api.command.Command;
import dev.endoy.bungeeutilisalsx.common.api.server.IProxyServer;
import dev.endoy.bungeeutilisalsx.common.api.utils.other.PluginInfo;
import dev.endoy.bungeeutilisalsx.velocity.utils.CommandHolder;
import dev.endoy.bungeeutilisalsx.velocity.utils.VelocityServer;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;
import com.velocitypowered.api.plugin.meta.PluginDependency;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import net.kyori.adventure.text.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class VelocityOperationsApi implements ServerOperationsApi
{

    private final Map<Command, CommandHolder> commandHolders = new HashMap<>();

    @Override
    public void registerCommand( final Command command )
    {
        if ( !commandHolders.containsKey( command ) && !command.isListenerBased() )
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
        return Bootstrap.getInstance().getProxyServer()
                .getServer( serverName )
                .map( VelocityServer::new )
                .orElse( null );
    }

    @Override
    public List<PluginInfo> getPlugins()
    {
        return Bootstrap.getInstance().getProxyServer().getPluginManager().getPlugins()
                .stream()
                .map( PluginContainer::getDescription )
                .map( this::getPluginInfo )
                .collect( Collectors.toList() );
    }

    @Override
    public Optional<PluginInfo> getPlugin( final String pluginName )
    {
        return Bootstrap.getInstance().getProxyServer().getPluginManager().getPlugins()
                .stream()
                .filter( it -> it.getDescription().getName().orElse( "" ).equalsIgnoreCase( pluginName )
                        || it.getDescription().getId().equalsIgnoreCase( pluginName ) )
                .findFirst()
                .map( PluginContainer::getDescription )
                .map( this::getPluginInfo );
    }

    @Override
    public long getMaxPlayers()
    {
        return Bootstrap.getInstance().getProxyServer().getConfiguration().getShowMaxPlayers();
    }

    @Override
    public Object getMessageComponent( final Component component )
    {
        return component;
    }

    private PluginInfo getPluginInfo( final PluginDescription pluginDescription )
    {
        return new PluginInfo(
                pluginDescription.getName().orElse( "" ),
                pluginDescription.getVersion().orElse( "" ),
                String.join( ", ", pluginDescription.getAuthors() ),
                pluginDescription.getDependencies().stream()
                        .filter( dep -> !dep.isOptional() )
                        .map( PluginDependency::getId )
                        .collect( Collectors.toSet() ),
                pluginDescription.getDependencies().stream()
                        .filter( PluginDependency::isOptional )
                        .map( PluginDependency::getId )
                        .collect( Collectors.toSet() ),
                pluginDescription.getDescription().orElse( "" )
        );
    }
}
