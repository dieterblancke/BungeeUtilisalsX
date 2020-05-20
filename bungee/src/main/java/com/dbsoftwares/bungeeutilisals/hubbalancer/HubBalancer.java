package com.dbsoftwares.bungeeutilisals.hubbalancer;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.other.hubbalancer.IHubBalancer;
import com.dbsoftwares.bungeeutilisals.api.other.hubbalancer.ServerData;
import com.dbsoftwares.bungeeutilisals.api.other.hubbalancer.ServerData.ServerType;
import com.dbsoftwares.bungeeutilisals.api.utils.MathUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisals.hubbalancer.listeners.JoinListener;
import com.dbsoftwares.bungeeutilisals.hubbalancer.listeners.KickListener;
import com.dbsoftwares.bungeeutilisals.hubbalancer.tasks.ServerPingTask;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Sets;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HubBalancer implements IHubBalancer
{

    @Getter
    private Set<ServerData> servers = Sets.newHashSet();

    public HubBalancer()
    {
        final IConfiguration configuration = ConfigFiles.HUBBALANCER.getConfig();

        for ( String lobby : configuration.getStringList( "lobbies" ) )
        {
            servers.add( createServerData( ServerType.LOBBY, lobby ) );
        }

        for ( String fallback : configuration.getStringList( "fallback-servers" ) )
        {
            servers.add( createServerData( ServerType.FALLBACK, fallback ) );
        }

        ProxyServer.getInstance().getScheduler().schedule(
                BungeeUtilisals.getInstance(), new ServerPingTask(), 1, configuration.getInteger( "ping-delay" ), TimeUnit.SECONDS
        );

        ProxyServer.getInstance().getPluginManager().registerListener( BungeeUtilisals.getInstance(), new JoinListener() );
        ProxyServer.getInstance().getPluginManager().registerListener( BungeeUtilisals.getInstance(), new KickListener() );
    }

    public void reload()
    {
        final IConfiguration configuration = ConfigFiles.HUBBALANCER.getConfig();
        final Set<ServerData> tempServers = Sets.newHashSet();

        for ( String lobby : configuration.getStringList( "lobbies" ) )
        {
            tempServers.add( createServerData( ServerType.LOBBY, lobby ) );
        }

        for ( String fallback : configuration.getStringList( "fallback-servers" ) )
        {
            tempServers.add( createServerData( ServerType.FALLBACK, fallback ) );
        }

        this.servers = tempServers;
        ProxyServer.getInstance().getScheduler().runAsync( BungeeUtilisals.getInstance(), new ServerPingTask() );
    }

    private ServerData createServerData( final ServerType type, final String server )
    {
        final ServerData data = this.servers.stream()
                .filter( serverData -> serverData.getName().equals( server ) )
                .findFirst()
                .orElse( new ServerData( Sets.newHashSet(), server, false ) );

        if ( !data.isType( type ) )
        {
            data.getTypes().add( type );
        }

        return data;
    }

    public ServerData findBestServer( ServerType type )
    {
        return this.findBestServer( type, this.servers.stream() );
    }

    public ServerData findBestServer( final ServerType type, final Stream<ServerData> stream )
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
}
