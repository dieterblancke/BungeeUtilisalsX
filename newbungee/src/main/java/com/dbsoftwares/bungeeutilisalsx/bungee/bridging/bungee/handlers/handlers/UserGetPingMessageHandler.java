package com.dbsoftwares.bungeeutilisalsx.bungee.bridging.bungee.handlers.handlers;

import com.dbsoftwares.bungeeutilisalsx.bungee.bridging.bungee.handlers.BridgeMessageHandler;
import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.event.BridgeResponseEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
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
