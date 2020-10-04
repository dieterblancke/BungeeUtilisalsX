package com.dbsoftwares.bungeeutilisals.bridging.bungee.handlers.handlers;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.bridge.event.BridgeResponseEvent;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.handlers.BridgeMessageHandler;
import com.google.gson.JsonPrimitive;

import java.util.Optional;

public class UserGetPingMessageHandler implements BridgeMessageHandler<JsonPrimitive>
{

    @Override
    public void accept( final BridgeResponseEvent event, final JsonPrimitive data )
    {
        final Optional<User> user = BUCore.getApi().getUser( data.getAsString() );

        user.ifPresent( value -> event.reply( value.getPing() ) );
    }

    @Override
    public Class<? extends JsonPrimitive> getType()
    {
        return JsonPrimitive.class;
    }
}
