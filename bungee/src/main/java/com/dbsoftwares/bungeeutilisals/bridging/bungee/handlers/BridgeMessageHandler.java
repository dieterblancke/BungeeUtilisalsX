package com.dbsoftwares.bungeeutilisals.bridging.bungee.handlers;

import com.dbsoftwares.bungeeutilisals.api.bridge.event.BridgeResponseEvent;

public interface BridgeMessageHandler<T>
{

    void accept( BridgeResponseEvent event, T data );

    Class<? extends T> getType();

}
