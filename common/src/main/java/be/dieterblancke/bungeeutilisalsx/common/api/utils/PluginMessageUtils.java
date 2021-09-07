package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.util.List;

public class PluginMessageUtils
{

    public static void sendFriendGuiOpenMessage( final User user, final String gui )
    {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF( "friends:gui" );
        out.writeUTF( "open" );
        out.writeUTF( gui );
        out.writeUTF( user.getName() );

        sendPluginMessage( user, out.toByteArray() );
    }

    public static void sendExecuteConsoleCommandsMessage( final User user, final List<String> commands )
    {
        sendPluginMessage( user, getExecuteConsoleCommandsMessage( commands ) );
    }

    public static void sendExecuteConsoleCommandsMessage( final IProxyServer proxyServer, final List<String> commands )
    {
        sendPluginMessage( proxyServer, getExecuteConsoleCommandsMessage( commands ) );
    }

    private static byte[] getExecuteConsoleCommandsMessage( final List<String> commands )
    {
        final ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF( "commands:console" );
        out.writeUTF( String.join( "|", commands ) );

        return out.toByteArray();
    }

    public static void sendPluginMessage( final User user, final byte[] bytes )
    {
        final IProxyServer server = BuX.getInstance().proxyOperations().getServerInfo( user.getServerName() );

        sendPluginMessage( server, bytes );
    }

    public static void sendPluginMessage( final IProxyServer server, final byte[] bytes )
    {
        if ( server != null )
        {
            server.sendPluginMessage( "bux:main", bytes );
        }
    }
}
