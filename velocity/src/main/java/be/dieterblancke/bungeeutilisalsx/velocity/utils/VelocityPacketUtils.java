package be.dieterblancke.bungeeutilisalsx.velocity.utils;

import com.velocitypowered.api.network.ProtocolVersion;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.util.MessagePosition;
import com.velocitypowered.proxy.connection.client.ConnectedPlayer;
import com.velocitypowered.proxy.protocol.MinecraftPacket;
import com.velocitypowered.proxy.protocol.packet.Chat;
import com.velocitypowered.proxy.protocol.packet.HeaderAndFooter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

public class VelocityPacketUtils
{

    public static void sendPacket( final Player player, final Object packet )
    {
        if ( packet instanceof MinecraftPacket )
        {
            ( (ConnectedPlayer) player ).getConnection().write( packet );
        }
    }

    public static void sendTabPacket( final Player player, final BaseComponent[] header, final BaseComponent[] footer )
    {
        if ( player.getProtocolVersion().compareTo( ProtocolVersion.MINECRAFT_1_8 ) >= 0 )
        {
            sendPacket( player, new HeaderAndFooter(
                    ComponentSerializer.toString( header ),
                    ComponentSerializer.toString( footer )
            ) );
        }
    }

    public static void sendMessagePacket( final Player player, final BaseComponent... components )
    {
        final Chat chat = new Chat();
        chat.setType( (byte) MessagePosition.CHAT.ordinal() );
        chat.setMessage( ComponentSerializer.toString( components ) );

        sendPacket( player, chat );
    }
}
