package be.dieterblancke.bungeeutilisalsx.common.api.utils.other;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.HasMessagePlaceholders;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;

import java.util.Collection;

public interface IProxyServer extends HasMessagePlaceholders
{

    String getName();

    Collection<String> getPlayers();

    Collection<User> getUsers();

    void sendPluginMessage( String channel, byte[] data );

    @Override
    default MessagePlaceholders getMessagePlaceholders()
    {
        return MessagePlaceholders.create()
                .append( "server", getName() )
                .append( "serverName", getName() );
    }
}
