package be.dieterblancke.bungeeutilisalsx.common.serverbalancer;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.serverbalancer.ServerBalancer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TimeUnit;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.ServerBalancerConfig.ServerBalancerGroup;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.ServerBalancerConfig.ServerBalancerGroupPinger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class SimpleServerBalancer implements ServerBalancer
{

    private final Map<ServerBalancerGroup, ScheduledFuture<?>> pingerTasks = new ConcurrentHashMap<>();

    public SimpleServerBalancer()
    {
        this.setupPingerTasks();
    }

    private void setupPingerTasks() {
        pingerTasks.values().forEach( it -> it.cancel( true ) );
        pingerTasks.clear();

        for ( ServerBalancerGroup balancerGroup : ConfigFiles.SERVER_BALANCER_CONFIG.getBalancerGroups() )
        {
            ServerBalancerGroupPinger pinger = balancerGroup.getPinger();
            ScheduledFuture<?> pingerTask = BuX.getInstance().getScheduler().runTaskRepeating( pinger.getDelay(), pinger.getDelay(), TimeUnit.SECONDS, () ->
            {
                // TODO: ping and handle result, if failure, keep track of how many times it failed, if > maxAttempts, wait for COOLDOWN seconds
            } );
            pingerTasks.put( balancerGroup, pingerTask );
        }
    }
}
