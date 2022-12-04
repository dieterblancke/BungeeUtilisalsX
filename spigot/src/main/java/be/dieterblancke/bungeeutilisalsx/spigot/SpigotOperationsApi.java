package be.dieterblancke.bungeeutilisalsx.spigot;

import be.dieterblancke.bungeeutilisalsx.common.ServerOperationsApi;
import be.dieterblancke.bungeeutilisalsx.common.api.command.Command;
import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.PluginInfo;
import be.dieterblancke.bungeeutilisalsx.spigot.command.CommandHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.util.*;
import java.util.stream.Collectors;

public class SpigotOperationsApi implements ServerOperationsApi
{

    private final Map<Command, CommandHolder> commandHolders = new HashMap<>();

    @Override
    public void registerCommand( final Command command )
    {
        if ( !commandHolders.containsKey( command ) && !command.isListenerBased() )
        {
            final CommandHolder commandHolder = new CommandHolder( command );

//            TODO: register command
//            ProxyServer.getInstance().getPluginManager().registerCommand( Bootstrap.getInstance(), commandHolder );

            commandHolders.put( command, commandHolder );
        }
    }

    @Override
    public void unregisterCommand( final Command command )
    {
        if ( commandHolders.containsKey( command ) )
        {
//            TODO: unregister command
//            ProxyServer.getInstance().getPluginManager().unregisterCommand( this.commandHolders.remove( command ) );
        }
    }

    @Override
    public List<IProxyServer> getServers()
    {
        return List.of();
    }

    @Override
    public IProxyServer getServerInfo( final String serverName )
    {
        return null;
    }

    @Override
    public List<PluginInfo> getPlugins()
    {
        return Arrays.stream( Bukkit.getPluginManager().getPlugins() )
                .map( Plugin::getDescription )
                .map( this::getPluginInfo )
                .collect( Collectors.toList() );
    }

    @Override
    public Optional<PluginInfo> getPlugin( final String pluginName )
    {
        final Plugin plugin = Bukkit.getPluginManager().getPlugin( pluginName );

        return Optional.ofNullable( plugin )
                .map( Plugin::getDescription )
                .map( this::getPluginInfo );
    }

    @Override
    public long getMaxPlayers()
    {
        return Bukkit.getMaxPlayers();
    }

    @Override
    public Object getMessageComponent( final Component component )
    {
        return BungeeComponentSerializer.get().serialize( component );
    }

    private PluginInfo getPluginInfo( final PluginDescriptionFile pluginDescription )
    {
        return new PluginInfo(
                pluginDescription.getName(),
                pluginDescription.getVersion(),
                String.join( ", ", pluginDescription.getAuthors() ),
                new HashSet<>( pluginDescription.getDepend() ),
                new HashSet<>( pluginDescription.getSoftDepend() ),
                pluginDescription.getDescription()
        );
    }
}
