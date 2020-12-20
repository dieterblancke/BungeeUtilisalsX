package be.dieterblancke.bungeeutilisalsx.common.bridge.handlers.handlers;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.event.BridgeResponseEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.bridge.handlers.BridgeMessageHandler;
import com.google.gson.JsonPrimitive;

import java.util.Optional;

public class UserGetPingMessageHandler implements BridgeMessageHandler<JsonPrimitive>
{

    @Override
    public void accept( final BridgeResponseEvent event, final JsonPrimitive data )
    {
        final Optional<User> user = BuX.getApi().getUser( data.getAsString() );

        user.ifPresent( value -> event.reply( value.getPing() ) );
    }

    @Override
    public Class<? extends JsonPrimitive> getType()
    {
        return JsonPrimitive.class;
    }
}
