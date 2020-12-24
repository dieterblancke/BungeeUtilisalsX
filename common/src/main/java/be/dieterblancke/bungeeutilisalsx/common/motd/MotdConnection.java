package be.dieterblancke.bungeeutilisalsx.common.motd;

import java.net.InetSocketAddress;

public interface MotdConnection
{
    int getVersion();

    String getName();

    InetSocketAddress getVirtualHost();
}
