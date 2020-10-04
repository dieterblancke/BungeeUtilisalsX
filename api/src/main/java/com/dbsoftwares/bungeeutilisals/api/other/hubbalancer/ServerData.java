package com.dbsoftwares.bungeeutilisals.api.other.hubbalancer;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
public class ServerData
{

    private Set<ServerType> types;
    private String name;
    private boolean online;

    public ServerInfo getServerInfo()
    {
        return ProxyServer.getInstance().getServerInfo( name );
    }

    public int getCount()
    {
        return BUCore.getApi().getPlayerUtils().getPlayerCount( name );
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }
        final ServerData data = (ServerData) o;
        return Objects.equals( name, data.name );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name );
    }

    public boolean isType( final ServerType type )
    {
        return types.contains( type );
    }

    public enum ServerType
    {
        LOBBY, FALLBACK
    }
}
