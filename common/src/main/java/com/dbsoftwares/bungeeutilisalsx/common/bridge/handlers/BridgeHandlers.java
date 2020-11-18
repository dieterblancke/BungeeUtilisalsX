package com.dbsoftwares.bungeeutilisalsx.common.bridge.handlers;

import com.dbsoftwares.bungeeutilisalsx.common.bridge.handlers.handlers.UserGetPingMessageHandler;
import com.dbsoftwares.bungeeutilisalsx.common.bridge.handlers.handlers.UserMessageHandler;
import com.dbsoftwares.bungeeutilisalsx.common.bridge.handlers.handlers.UserMoveServerMessageHandler;
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
