package com.dbsoftwares.bungeeutilisals.api.other.hubbalancer;

import com.dbsoftwares.bungeeutilisals.api.other.hubbalancer.ServerData.ServerType;

import java.util.Set;
import java.util.stream.Stream;

public interface IHubBalancer
{

    Set<ServerData> getServers();

    void reload();

    ServerData findBestServer( ServerType type );

    ServerData findBestServer( final ServerType type, final Stream<ServerData> stream );
}
