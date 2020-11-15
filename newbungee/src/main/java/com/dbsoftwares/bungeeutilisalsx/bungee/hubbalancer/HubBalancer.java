package com.dbsoftwares.bungeeutilisalsx.bungee.hubbalancer;

import com.dbsoftwares.bungeeutilisalsx.bungee.Bootstrap;
import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.hubbalancer.HubServerType;
import com.dbsoftwares.bungeeutilisalsx.common.api.hubbalancer.IHubBalancer;
import com.dbsoftwares.bungeeutilisalsx.common.api.hubbalancer.ServerData;
import com.dbsoftwares.bungeeutilisalsx.bungee.hubbalancer.listeners.JoinListener;
import com.dbsoftwares.bungeeutilisalsx.bungee.hubbalancer.listeners.KickListener;
import com.dbsoftwares.bungeeutilisalsx.bungee.hubbalancer.tasks.ServerPingTask;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.MathUtils;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
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
            servers.add( createServerData( HubServerType.LOBBY, lobby ) );
        }

        for ( String fallback : configuration.getStringList( "fallback-servers" ) )
        {
            servers.add( createServerData( HubServerType.FALLBACK, fallback ) );
        }

        BuX.getInstance().getScheduler().runTaskRepeating(
                1, configuration.getInteger( "ping-delay" ), TimeUnit.SECONDS, new ServerPingTask()
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
            tempServers.add( createServerData( HubServerType.LOBBY, lobby ) );
        }

        for ( String fallback : configuration.getStringList( "fallback-servers" ) )
        {
            tempServers.add( createServerData( HubServerType.FALLBACK, fallback ) );
        }

        this.servers = tempServers;
        BuX.getInstance().getScheduler().runAsync( new ServerPingTask() );
    }

    private ServerData createServerData( final HubServerType type, final String server )
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
}
