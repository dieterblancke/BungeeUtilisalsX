package dev.endoy.bungeeutilisalsx.common.serverbalancer;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.server.IProxyServer;
import dev.endoy.bungeeutilisalsx.common.api.server.IProxyServer.PingInfo;
import dev.endoy.bungeeutilisalsx.common.api.serverbalancer.ServerBalancer;
import dev.endoy.bungeeutilisalsx.common.api.utils.MathUtils;
import dev.endoy.bungeeutilisalsx.common.api.utils.TimeUnit;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.configs.ServerBalancerConfig.ServerBalancerGroup;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.configs.ServerBalancerConfig.ServerBalancerGroupPinger;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.configs.ServerBalancerConfig.ServerBalancingMethod;
import lombok.Data;
import lombok.Getter;
import lombok.Value;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class SimpleServerBalancer implements ServerBalancer
{

    private final Map<ServerBalancerGroup, ScheduledFuture<?>> pingerTasks = new ConcurrentHashMap<>();
    private final Map<ServerBalancerGroup, ServerBalancerGroupStatus> groupStatuses = new ConcurrentHashMap<>();
    @Getter
    private boolean initialized = false;

    @Override
    public void setup()
    {
        if ( initialized )
        {
            throw new IllegalStateException( "The SimpleServerBalancer has already been setup." );
        }

        setupPingerTasks();
        initialized = true;
    }

    @Override
    public void shutdown()
    {
        if ( !initialized )
        {
            throw new IllegalStateException( "The SimpleServerBalancer has not yet been set up." );
        }

        pingerTasks.values().forEach( it -> it.cancel( true ) );
        pingerTasks.clear();
        initialized = false;
    }

    @Override
    public void reload()
    {
        shutdown();
        setup();
    }

    @Override
    public Optional<IProxyServer> getOptimalServer( ServerBalancerGroup balancerGroup )
    {
        return this.getOptimalServer( balancerGroup, null );
    }

    @Override
    public Optional<IProxyServer> getOptimalServer( ServerBalancerGroup balancerGroup, String serverToIgnore )
    {
        ServerBalancingMethod balancingMethod = balancerGroup.getMethod();
        ServerBalancerGroupStatus groupStatus = groupStatuses.get( balancerGroup );

        return switch ( balancingMethod )
        {
            case RANDOM ->
                Optional.ofNullable( MathUtils.getRandomFromList( groupStatus.getAvailableServers( serverToIgnore ) ) );
            case LEAST_PLAYERS ->
                groupStatus.getAvailableServers( serverToIgnore ).stream().min( Comparator.comparingInt( o -> groupStatus.getServerStatuses().get( o ).getOnlinePlayers() ) );
            case FIRST_NON_FULL -> groupStatus.getAvailableServers( serverToIgnore ).stream().findFirst();
            case MOST_PLAYERS ->
                groupStatus.getAvailableServers( serverToIgnore ).stream().max( Comparator.comparingInt( o -> groupStatus.getServerStatuses().get( o ).getOnlinePlayers() ) );
        };
    }

    private void setupPingerTasks()
    {
        for ( ServerBalancerGroup balancerGroup : ConfigFiles.SERVER_BALANCER_CONFIG.getBalancerGroups() )
        {
            ServerBalancerGroupPinger pinger = balancerGroup.getPinger();
            ScheduledFuture<?> pingerTask = BuX.getInstance().getScheduler().runTaskRepeating( 0, pinger.getDelay(), TimeUnit.SECONDS, () ->
            {
                final ServerBalancerGroupStatus groupStatus;
                if ( !groupStatuses.containsKey( balancerGroup ) )
                {
                    groupStatus = new ServerBalancerGroupStatus( new ConcurrentHashMap<>() );
                    groupStatuses.put( balancerGroup, groupStatus );
                }
                else
                {
                    groupStatus = groupStatuses.get( balancerGroup );
                }

                for ( IProxyServer server : balancerGroup.getServerGroup().getServers() )
                {
                    if ( !groupStatus.getServerStatuses().containsKey( server ) )
                    {
                        groupStatus.getServerStatuses().put( server, new ServerBalancerServerStatus() );
                    }

                    ServerBalancerServerStatus serverStatus = groupStatus.getServerStatuses().get( server );
                    boolean canPing = false;

                    if ( serverStatus.getFailureCount() >= pinger.getMaxAttempts() )
                    {
                        if ( serverStatus.getLastUpdate() + TimeUnit.SECONDS.toMillis( pinger.getCooldown() ) <= System.currentTimeMillis() )
                        {
                            serverStatus.setFailureCount( 0 );
                            canPing = true;
                        }
                    }
                    else
                    {
                        canPing = true;
                    }

                    if ( canPing )
                    {
                        server.ping().whenComplete( ( pingInfo, throwable ) ->
                        {
                            serverStatus.update( throwable == null ? pingInfo : null );

                            // mark server as offline if it meets none of the MOTD filters
                            if ( throwable != null && pinger.getMotdFilters() != null && !pinger.getMotdFilters().isEmpty() )
                            {
                                if ( pinger.getMotdFilters()
                                    .stream()
                                    .noneMatch( filter -> filter.matcher( serverStatus.getMotd() ).matches() ) )
                                {
                                    serverStatus.setOnline( false );
                                }
                            }
                        } );
                    }
                }
            } );
            pingerTasks.put( balancerGroup, pingerTask );
        }
    }

    @Value
    public static class ServerBalancerGroupStatus
    {
        Map<IProxyServer, ServerBalancerServerStatus> serverStatuses;

        public List<IProxyServer> getAvailableServers( String serverToIgnore )
        {
            List<IProxyServer> availableServers = new ArrayList<>();

            for ( Entry<IProxyServer, ServerBalancerServerStatus> entry : serverStatuses.entrySet() )
            {
                if ( entry.getKey() == null )
                {
                    continue;
                }
                if ( serverToIgnore != null && entry.getKey().getName().equals( serverToIgnore ) )
                {
                    continue;
                }

                if ( entry.getValue().isOnline() && entry.getValue().getOnlinePlayers() < entry.getValue().getMaxPlayers() )
                {
                    availableServers.add( entry.getKey() );
                }
            }

            return availableServers;
        }
    }

    @Data
    public static class ServerBalancerServerStatus
    {

        private long lastUpdate;
        private int failureCount = 0;
        private boolean online;
        private String motd = "";
        private int onlinePlayers;
        private int maxPlayers;

        public ServerBalancerServerStatus()
        {
        }

        public void update( PingInfo pingInfo )
        {
            lastUpdate = System.currentTimeMillis();

            if ( pingInfo == null )
            {
                failureCount++;
                online = false;
                motd = "";
                onlinePlayers = 0;
                maxPlayers = 0;
            }
            else
            {
                failureCount = 0;
                online = true;
                motd = pingInfo.getMotd();
                onlinePlayers = pingInfo.getOnlinePlayers();
                maxPlayers = pingInfo.getMaxPlayers();
            }
        }
    }
}
