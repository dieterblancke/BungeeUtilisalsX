package be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.handler;

import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.PlaceHolderEvent;

public interface PlaceHolderEventHandler
{

    String getReplacement( PlaceHolderEvent event );

}
