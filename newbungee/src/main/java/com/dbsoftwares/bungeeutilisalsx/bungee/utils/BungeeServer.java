package com.dbsoftwares.bungeeutilisalsx.bungee.utils;

import com.dbsoftwares.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import lombok.Data;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Collection;
import java.util.stream.Collectors;

@Data
public class BungeeServer implements IProxyServer
{

    private final ServerInfo serverInfo;

    public BungeeServer( final ServerInfo serverInfo )
    {
        this.serverInfo = serverInfo;
    }

    @Override
    public String getName()
    {
        return serverInfo.getName();
    }

    @Override
    public Collection<String> getPlayers()
    {
        return serverInfo.getPlayers().stream().map( ProxiedPlayer::getName ).collect( Collectors.toList() );
    }
}
