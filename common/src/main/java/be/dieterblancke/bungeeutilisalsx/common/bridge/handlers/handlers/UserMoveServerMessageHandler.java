package be.dieterblancke.bungeeutilisalsx.common.bridge.handlers.handlers;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.event.BridgeResponseEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.IProxyServer;
import be.dieterblancke.bungeeutilisalsx.common.bridge.handlers.BridgeMessageHandler;
import be.dieterblancke.bungeeutilisalsx.common.commands.general.ServerCommandCall;
import com.google.gson.JsonObject;

import java.util.Optional;

public class UserMoveServerMessageHandler implements BridgeMessageHandler<JsonObject>
{

    @Override
    public void accept( final BridgeResponseEvent event, final JsonObject data )
    {
        final Optional<User> user = BuX.getApi().getUser( data.get( "user" ).getAsString() );
        final IProxyServer server = BuX.getInstance().proxyOperations().getServerInfo( data.get( "server" ).getAsString() );

        if ( user.isPresent() && server != null )
        {
            ServerCommandCall.sendToServer( user.get(), server );
        }
    }

    @Override
    public Class<? extends JsonObject> getType()
    {
        return JsonObject.class;
    }
}
