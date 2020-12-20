package be.dieterblancke.bungeeutilisalsx.common.api.hubbalancer;


import java.util.Set;
import java.util.stream.Stream;

public interface IHubBalancer
{

    Set<ServerData> getServers();

    void reload();

    ServerData findBestServer( HubServerType type );

    ServerData findBestServer( HubServerType type, Stream<ServerData> stream );
}
