package be.dieterblancke.bungeeutilisalsx.bungee.hubbalancer;

import be.dieterblancke.bungeeutilisalsx.bungee.Bootstrap;
import be.dieterblancke.bungeeutilisalsx.bungee.hubbalancer.listeners.JoinListener;
import be.dieterblancke.bungeeutilisalsx.bungee.hubbalancer.listeners.KickListener;
import be.dieterblancke.bungeeutilisalsx.bungee.hubbalancer.tasks.ServerPingTask;
import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.hubbalancer.HubServerType;
import be.dieterblancke.bungeeutilisalsx.common.api.hubbalancer.IHubBalancer;
import be.dieterblancke.bungeeutilisalsx.common.api.hubbalancer.ServerData;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MathUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.configuration.api.IConfiguration;
import com.google.common.collect.Sets;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HubBalancer implements IHubBalancer
{

    @Getter
    private final Set<ServerData> triggerServers = new HashSet<>();
    @Getter
    private Set<ServerData> servers = new HashSet<>();

    public HubBalancer()
    {
        final IConfiguration configuration = ConfigFiles.HUBBALANCER.getConfig();

        for ( String lobby : configuration.getStringList( "lobbies" ) )
        {
            servers.addAll( createServerData( HubServerType.LOBBY, lobby ) );
        }

        for ( String fallback : configuration.getStringList( "fallback-servers" ) )
        {
            servers.addAll( createServerData( HubServerType.FALLBACK, fallback ) );
        }

        for ( String trigger : configuration.getStringList( "triggers" ) )
        {
            triggerServers.addAll( createServerData( HubServerType.TRIGGER, trigger ) );
        }

        BuX.getInstance().getScheduler().runTaskRepeating(
                5, configuration.getInteger( "ping-delay" ), TimeUnit.SECONDS, new ServerPingTask()
        );

        ProxyServer.getInstance().getPluginManager().registerListener( Bootstrap.getInstance(), new JoinListener() );
        ProxyServer.getInstance().getPluginManager().registerListener( Bootstrap.getInstance(), new KickListener() );
    }

    public void reload()
    {
        final IConfiguration configuration = ConfigFiles.HUBBALANCER.getConfig();
        final Set<ServerData> tempServers = Sets.newHashSet();

        for ( String lobby : configuration.getStringList( "lobbies" ) )
        {
            tempServers.addAll( createServerData( HubServerType.LOBBY, lobby ) );
        }

        for ( String fallback : configuration.getStringList( "fallback-servers" ) )
        {
            tempServers.addAll( createServerData( HubServerType.FALLBACK, fallback ) );
        }

        this.servers = tempServers;

        triggerServers.clear();
        for ( String trigger : configuration.getStringList( "triggers" ) )
        {
            triggerServers.addAll( createServerData( HubServerType.TRIGGER, trigger ) );
        }

        BuX.getInstance().getScheduler().runAsync( new ServerPingTask() );
    }

    private List<ServerData> createServerData( final HubServerType type, final String server )
    {
        if ( server.startsWith( "*" ) )
        {
            final List<ServerData> servers = new ArrayList<>();
            for ( IProxyServer proxyServer : BuX.getInstance().proxyOperations().getServers() )
            {
                if ( proxyServer.getName().toLowerCase().endsWith( server.substring( 1 ).toLowerCase() ) )
                {
                    servers.addAll( this.createServerData( type, proxyServer.getName() ) );
                }
            }
            return servers;
        }
        else if ( server.endsWith( "*" ) )
        {
            final List<ServerData> servers = new ArrayList<>();

            for ( IProxyServer proxyServer : BuX.getInstance().proxyOperations().getServers() )
            {
                if ( proxyServer.getName().toLowerCase().startsWith( server.substring( 0, server.length() - 1 ).toLowerCase() ) )
                {
                    servers.addAll( this.createServerData( type, proxyServer.getName() ) );
                }
            }
            return servers;
        }
        else
        {
            final ServerData data = this.servers.stream()
                    .filter( serverData -> serverData.getName().equals( server ) )
                    .findFirst()
                    .orElse( new ServerData( Sets.newHashSet(), server, false ) );

            if ( !data.isType( type ) )
            {
                data.getTypes().add( type );
            }

            return Collections.singletonList( data );
        }
    }

    public ServerData findBestServer( HubServerType type )
    {
        return this.findBestServer( type, this.servers.stream() );
    }

    public ServerData findBestServer( final HubServerType type, final Stream<ServerData> stream )
    {
        final List<ServerData> servers = stream
                .filter( server -> server.isType( type ) )
                .filter( ServerData::isOnline )
                .collect( Collectors.toList() );

        if ( servers.isEmpty() )
        {
            return null;
        }
        final boolean random = ConfigFiles.HUBBALANCER.getConfig().getBoolean( "random" );

        if ( random )
        {
            return MathUtils.getRandomFromList( servers );
        }

        servers.sort( Comparator.comparingInt( ServerData::getCount ) );
        return servers.get( 0 );
    }

    @Override
    public boolean isTrigger( String name )
    {
        return this.triggerServers.stream()
                .filter( trigger -> trigger.getName().equalsIgnoreCase( name ) )
                .findAny()
                .isPresent();
    }
}
