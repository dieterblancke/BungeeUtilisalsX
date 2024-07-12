package dev.endoy.bungeeutilisalsx.bungee;

import dev.endoy.bungeeutilisalsx.bungee.utils.BungeeServer;
import dev.endoy.bungeeutilisalsx.bungee.utils.CommandHolder;
import dev.endoy.bungeeutilisalsx.common.ServerOperationsApi;
import dev.endoy.bungeeutilisalsx.common.api.command.Command;
import dev.endoy.bungeeutilisalsx.common.api.server.IProxyServer;
import dev.endoy.bungeeutilisalsx.common.api.utils.other.PluginInfo;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginDescription;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class BungeeOperationsApi implements ServerOperationsApi
{

    private final Map<Command, CommandHolder> commandHolders = new HashMap<>();

    @Override
    public void registerCommand( final Command command )
    {
        if ( !commandHolders.containsKey( command ) && !command.isListenerBased() )
        {
            final CommandHolder commandHolder = new CommandHolder( command );

            ProxyServer.getInstance().getPluginManager().registerCommand( Bootstrap.getInstance(), commandHolder );

            commandHolders.put( command, commandHolder );
        }
    }

    @Override
    public void unregisterCommand( final Command command )
    {
        if ( commandHolders.containsKey( command ) )
        {
            ProxyServer.getInstance().getPluginManager().unregisterCommand( this.commandHolders.remove( command ) );
        }
    }

    @Override
    public List<IProxyServer> getServers()
    {
        return ProxyServer.getInstance()
                .getServers()
                .values()
                .stream()
                .map( ServerInfo::getName )
                .map( this::getServerInfo )
                .collect( Collectors.toList() );
    }

    @Override
    public IProxyServer getServerInfo( final String serverName )
    {
        final ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo( serverName );
        return serverInfo == null ? null : new BungeeServer( serverInfo );
    }

    @Override
    public List<PluginInfo> getPlugins()
    {
        return ProxyServer.getInstance().getPluginManager().getPlugins()
                .stream()
                .map( Plugin::getDescription )
                .map( this::getPluginInfo )
                .collect( Collectors.toList() );
    }

    @Override
    public Optional<PluginInfo> getPlugin( final String pluginName )
    {
        final Plugin plugin = ProxyServer.getInstance().getPluginManager().getPlugin( pluginName );

        return Optional.ofNullable( plugin )
                .map( Plugin::getDescription )
                .map( this::getPluginInfo );
    }

    @Override
    public Optional<Object> getPluginInstance( String pluginName )
    {
        return Optional.ofNullable( ProxyServer.getInstance().getPluginManager().getPlugin( pluginName ) );
    }

    @Override
    public long getMaxPlayers()
    {
        return ProxyServer.getInstance().getConfig().getListeners().iterator().next().getMaxPlayers();
    }

    @Override
    public Object getMessageComponent( final Component component )
    {
        return BungeeComponentSerializer.get().serialize( component );
    }

    private PluginInfo getPluginInfo( final PluginDescription pluginDescription )
    {
        return new PluginInfo(
                pluginDescription.getName(),
                pluginDescription.getVersion(),
                pluginDescription.getAuthor(),
                pluginDescription.getDepends(),
                pluginDescription.getSoftDepends(),
                pluginDescription.getDescription()
        );
    }
}
