package be.dieterblancke.bungeeutilisalsx.bungee.utils;

import be.dieterblancke.bungeeutilisalsx.common.motd.MotdConnection;
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
