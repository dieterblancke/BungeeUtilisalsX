package com.dbsoftwares.bungeeutilisals.bridging.bungee.handlers.handlers;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.bridge.event.BridgeResponseEvent;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.bridging.bungee.handlers.BridgeMessageHandler;
import com.dbsoftwares.bungeeutilisals.commands.general.ServerCommandCall;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Optional;

public class UserMoveServerMessageHandler implements BridgeMessageHandler<JsonObject>
{

    @Override
    public void accept( final BridgeResponseEvent event, final JsonObject data )
    {
        final Optional<User> user = BUCore.getApi().getUser( data.get( "user" ).getAsString() );
        final ServerInfo server = ProxyServer.getInstance().getServerInfo( data.get( "server" ).getAsString() );

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
