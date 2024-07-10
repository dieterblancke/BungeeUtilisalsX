package dev.endoy.bungeeutilisalsx.common.api.placeholder.event.handler;

import dev.endoy.bungeeutilisalsx.common.api.placeholder.event.InputPlaceHolderEvent;
import dev.endoy.bungeeutilisalsx.common.api.placeholder.event.PlaceHolderEvent;

public abstract class InputPlaceHolderEventHandler implements PlaceHolderEventHandler
{

    public abstract String getReplacement( InputPlaceHolderEvent event );

    @Override
    public String getReplacement( PlaceHolderEvent event )
    {
        return getReplacement( (InputPlaceHolderEvent) event );
    }
}
