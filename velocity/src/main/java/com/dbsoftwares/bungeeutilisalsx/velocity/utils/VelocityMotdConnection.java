package com.dbsoftwares.bungeeutilisalsx.velocity.utils;

import com.dbsoftwares.bungeeutilisalsx.common.motd.MotdConnection;
import com.velocitypowered.api.proxy.InboundConnection;
import lombok.Data;

import java.net.InetSocketAddress;

@Data
public class VelocityMotdConnection implements MotdConnection
{

    private final int version;
    private final String name;
    private final InetSocketAddress virtualHost;

    public VelocityMotdConnection( final InboundConnection connection, final String userName )
    {
        this.version = connection.getProtocolVersion().getProtocol();
        this.name = userName;
        this.virtualHost = connection.getVirtualHost().orElse( null );
    }
}
