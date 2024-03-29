package be.dieterblancke.bungeeutilisalsx.common.api.serverbalancer;

import be.dieterblancke.bungeeutilisalsx.common.api.server.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.configs.ServerBalancerConfig.ServerBalancerGroup;

import java.util.Optional;

public interface ServerBalancer
{

    void setup();

    void shutdown();

    void reload();

    Optional<IProxyServer> getOptimalServer( ServerBalancerGroup serverBalancerGroup );

    Optional<IProxyServer> getOptimalServer( ServerBalancerGroup serverBalancerGroup, String serverToIgnore );

}
