package com.dbsoftwares.bungeeutilisalsx.common.bridge.handlers;

import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.event.BridgeResponseEvent;

public interface BridgeMessageHandler<T>
{

    void accept( BridgeResponseEvent event, T data );

    Class<? extends T> getType();

}
