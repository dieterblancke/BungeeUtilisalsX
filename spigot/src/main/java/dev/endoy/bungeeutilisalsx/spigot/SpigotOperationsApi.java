package dev.endoy.bungeeutilisalsx.spigot;

import dev.endoy.bungeeutilisalsx.common.ServerOperationsApi;
import dev.endoy.bungeeutilisalsx.common.api.command.Command;
import dev.endoy.bungeeutilisalsx.common.api.server.IProxyServer;
import dev.endoy.bungeeutilisalsx.common.api.utils.other.PluginInfo;
import dev.endoy.bungeeutilisalsx.common.api.utils.reflection.ReflectionUtils;
import dev.endoy.bungeeutilisalsx.spigot.command.CommandHolder;
import lombok.SneakyThrows;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.bungeecord.BungeeComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class SpigotOperationsApi implements ServerOperationsApi
{

    private static final Field commandMap;

    static
    {
        commandMap = ReflectionUtils.getField( Bukkit.getServer().getClass(), "commandMap" );
    }

    private final Map<Command, CommandHolder> commandHolders = new HashMap<>();

    @Override
    @SneakyThrows
    public void registerCommand( final Command command )
    {
        if ( !commandHolders.containsKey( command ) && !command.isListenerBased() )
        {
            final CommandHolder commandHolder = new CommandHolder( command );

            CommandMap map = (CommandMap) commandMap.get( Bukkit.getServer() );
            unregisterCommands( map, command.getName(), command.getAliases() );
            map.register( command.getName(), "bux", commandHolder );

            commandHolders.put( command, commandHolder );
        }
    }

    @Override
    @SneakyThrows
    public void unregisterCommand( final Command command )
    {
        if ( commandHolders.containsKey( command ) )
        {
            CommandMap map = (CommandMap) commandMap.get( Bukkit.getServer() );
            unregisterCommands( map, command.getName(), command.getAliases() );
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
    public Optional<Object> getPluginInstance( String pluginName )
    {
        return Optional.ofNullable( Bukkit.getPluginManager().getPlugin( pluginName ) );
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

    @SneakyThrows
    @SuppressWarnings( "unchecked" )
    private void unregisterCommands( final CommandMap map, final String command, final String[] aliases )
    {
        final Field field;
        if ( ReflectionUtils.hasField( map.getClass(), "knownCommands" ) )
        {
            field = ReflectionUtils.getField( map.getClass(), "knownCommands" );
        }
        else
        {
            field = ReflectionUtils.getField( map.getClass().getSuperclass(), "knownCommands" );
        }
        final Map<String, Command> commands = (Map<String, Command>) field.get( map );

        commands.remove( command );

        for ( String alias : aliases )
        {
            commands.remove( alias );
        }

        field.set( map, commands );
    }
}
