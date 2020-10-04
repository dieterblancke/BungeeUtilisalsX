package com.dbsoftwares.bungeeutilisals.bridging.bungee.handlers;

import com.dbsoftwares.bungeeutilisals.bridging.bungee.handlers.handlers.*;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;

public enum BridgeHandlers
{

    USER( UserMessageHandler.class ),
    GET_USER_PING( UserGetPingMessageHandler.class ),
    USER_MOVE_SERVER( UserMoveServerMessageHandler.class );

    @Getter
    private BridgeMessageHandler handler;

    BridgeHandlers( final Class<? extends BridgeMessageHandler> handler )
    {
        try
        {
            this.handler = handler.getConstructor().newInstance();
        }
        catch ( InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e )
        {
            e.printStackTrace();
        }
    }
}
