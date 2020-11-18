package com.dbsoftwares.bungeeutilisalsx.bungee.utils;

import com.dbsoftwares.bungeeutilisalsx.common.motd.MotdConnection;
import lombok.Data;
import net.md_5.bungee.api.connection.PendingConnection;

import java.net.InetSocketAddress;

@Data
public class BungeeMotdConnection implements MotdConnection
{

    private final int version;
    private final String name;
    private final InetSocketAddress virtualHost;

    public BungeeMotdConnection( final PendingConnection connection )
    {
        this.version = connection.getVersion();
        this.name = connection.getName();
        this.virtualHost = connection.getVirtualHost();
    }
}
