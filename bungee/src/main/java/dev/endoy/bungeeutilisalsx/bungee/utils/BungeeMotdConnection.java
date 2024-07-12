package dev.endoy.bungeeutilisalsx.bungee.utils;

import dev.endoy.bungeeutilisalsx.common.motd.MotdConnection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.InetSocketAddress;

@Data
@AllArgsConstructor
public class BungeeMotdConnection extends MotdConnection
{

    private final int version;
    private final InetSocketAddress remoteIp;
    private final InetSocketAddress virtualHost;

}
