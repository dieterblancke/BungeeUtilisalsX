package be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.handler;

import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.InputPlaceHolderEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.PlaceHolderEvent;

public abstract class InputPlaceHolderEventHandler implements PlaceHolderEventHandler
{

    public abstract String getReplacement( InputPlaceHolderEvent event );

    @Override
    public String getReplacement( PlaceHolderEvent event )
    {
        return getReplacement( (InputPlaceHolderEvent) event );
    }
}
