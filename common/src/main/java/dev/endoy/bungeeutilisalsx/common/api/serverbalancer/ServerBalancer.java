package dev.endoy.bungeeutilisalsx.common.api.serverbalancer;

import dev.endoy.bungeeutilisalsx.common.api.server.IProxyServer;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.configs.ServerBalancerConfig.ServerBalancerGroup;

import java.util.Optional;

public interface ServerBalancer
{

    void setup();

    void shutdown();

    void reload();

    Optional<IProxyServer> getOptimalServer( ServerBalancerGroup serverBalancerGroup );

    Optional<IProxyServer> getOptimalServer( ServerBalancerGroup serverBalancerGroup, String serverToIgnore );

}
