package be.dieterblancke.bungeeutilisalsx.velocity.utils;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.MinecraftPacket;

public class VelocityPacketUtils
{

    public static void sendPacket( final Player player, final Object packet )
    {
        if ( player != null && packet instanceof MinecraftPacket )
        {
            ( (ConnectedPlayer) player ).getConnection().write( packet );
        }
    }
}
