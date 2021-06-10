package be.dieterblancke.bungeeutilisalsx.common.bridge.handlers;

import be.dieterblancke.bungeeutilisalsx.common.api.bridge.event.BridgeResponseEvent;

public interface BridgeMessageHandler<T>
{

    void accept( BridgeResponseEvent event, T data );

    Class<? extends T> getType();

}
