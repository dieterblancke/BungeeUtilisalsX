package be.dieterblancke.bungeeutilisalsx.common.serverbalancer;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer.PingInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.serverbalancer.ServerBalancer;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.MathUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TimeUnit;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.ServerBalancerConfig.ServerBalancerGroup;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.ServerBalancerConfig.ServerBalancerGroupPinger;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.ServerBalancerConfig.ServerBalancingMethod;
import lombok.Data;
import lombok.Value;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class SimpleServerBalancer implements ServerBalancer
{

    private final Map<ServerBalancerGroup, ScheduledFuture<?>> pingerTasks = new ConcurrentHashMap<>();
    private final Map<ServerBalancerGroup, ServerBalancerGroupStatus> groupStatuses = new ConcurrentHashMap<>();
    private boolean status = false;

    @Override
    public void setup()
    {
        if ( status )
        {
            throw new IllegalStateException( "The SimpleServerBalancer has already been setup." );
        }

        setupPingerTasks();
        status = true;
    }

    @Override
    public void shutdown()
    {
        if ( !status )
        {
            throw new IllegalStateException( "The SimpleServerBalancer has not yet been set up." );
        }
        pingerTasks.values().forEach( it -> it.cancel( true ) );
        pingerTasks.clear();
        status = false;
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
        ServerBalancingMethod balancingMethod = balancerGroup.getMethod();
        ServerBalancerGroupStatus groupStatus = groupStatuses.get( balancerGroup );

        return switch ( balancingMethod )
                {
                    case RANDOM -> Optional.ofNullable( MathUtils.getRandomFromList( groupStatus.getAvailableServers() ) );
                    case LEAST_PLAYERS -> groupStatus.getAvailableServers().stream().min( Comparator.comparingInt( o -> groupStatus.serverStatuses().get( o ).getOnlinePlayers() ) );
                    case FIRST_NON_FULL -> groupStatus.getAvailableServers().stream().findFirst();
                    case MOST_PLAYERS -> groupStatus.getAvailableServers().stream().max( Comparator.comparingInt( o -> groupStatus.serverStatuses().get( o ).getOnlinePlayers() ) );
                };
    }

    private void setupPingerTasks()
    {
        for ( ServerBalancerGroup balancerGroup : ConfigFiles.SERVER_BALANCER_CONFIG.getBalancerGroups() )
        {
            ServerBalancerGroupPinger pinger = balancerGroup.getPinger();
            ScheduledFuture<?> pingerTask = BuX.getInstance().getScheduler().runTaskRepeating( pinger.getDelay(), pinger.getDelay(), TimeUnit.SECONDS, () ->
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
                    if ( !groupStatus.serverStatuses().containsKey( server ) )
                    {
                        groupStatus.serverStatuses().put( server, new ServerBalancerServerStatus() );
                    }

                    ServerBalancerServerStatus serverStatus = groupStatus.serverStatuses().get( server );
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

    public record ServerBalancerGroupStatus(Map<IProxyServer, ServerBalancerServerStatus> serverStatuses)
    {

        public List<IProxyServer> getAvailableServers()
        {
            List<IProxyServer> availableServers = new ArrayList<>();

            for ( Entry<IProxyServer, ServerBalancerServerStatus> entry : serverStatuses.entrySet() )
            {
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
