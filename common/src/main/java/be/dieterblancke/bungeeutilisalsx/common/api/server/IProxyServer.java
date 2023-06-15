package be.dieterblancke.bungeeutilisalsx.common.api.server;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.HasMessagePlaceholders;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import lombok.Value;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface IProxyServer extends HasMessagePlaceholders
{

    String getName();

    Collection<String> getPlayers();

    Collection<User> getUsers();

    void sendPluginMessage( String channel, byte[] data );

    CompletableFuture<PingInfo> ping();

    @Override
    default MessagePlaceholders getMessagePlaceholders()
    {
        return MessagePlaceholders.create()
                .append( "server", getName() )
                .append( "serverName", getName() );
    }

    @Value
    class PingInfo
    {
        int onlinePlayers;
        int maxPlayers;
        String motd;
    }
}
