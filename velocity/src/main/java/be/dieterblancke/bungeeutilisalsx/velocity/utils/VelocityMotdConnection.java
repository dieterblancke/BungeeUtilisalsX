package be.dieterblancke.bungeeutilisalsx.velocity.utils;

import be.dieterblancke.bungeeutilisalsx.common.motd.MotdConnection;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.InetSocketAddress;

@Data
@AllArgsConstructor
public class VelocityMotdConnection implements MotdConnection
{

    private final int version;
    private final String name;
    private final InetSocketAddress virtualHost;

}
