package be.dieterblancke.bungeeutilisalsx.spigot.utils;

import be.dieterblancke.bungeeutilisalsx.spigot.Bootstrap;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;

public class PluginMessageUtils
{

    public static void sendPlayerProxyExecuteCommandPluginMessage( final Player player, final String command )
    {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF( "commands" );
        out.writeUTF( "proxy-execute" );
        out.writeUTF( command );

        player.sendPluginMessage( Bootstrap.getInstance(), "bux:main", out.toByteArray() );
    }

    public static void sendConsoleProxyExecuteCommandPluginMessage( final Player player, final String command )
    {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF( "commands" );
        out.writeUTF( "proxy-console-execute" );
        out.writeUTF( command );

        player.sendPluginMessage( Bootstrap.getInstance(), "bux:main", out.toByteArray() );
    }
}
