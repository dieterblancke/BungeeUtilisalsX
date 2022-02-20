package be.dieterblancke.bungeeutilisalsx.common.api.placeholder.placeholders;

import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.PlaceHolderEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.handler.PlaceHolderEventHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

public class DefaultPlaceHolder extends PlaceHolder
{

    public DefaultPlaceHolder( String placeHolder, boolean requiresUser, PlaceHolderEventHandler handler )
    {
        super( placeHolder, requiresUser, handler );
    }

    @Override
    public String format( User user, String message )
    {
        if ( placeHolderName == null || !message.contains( placeHolderName ) )
        {
            return message;
        }
        PlaceHolderEvent event = new PlaceHolderEvent( user, this, message );
        return message.replace( placeHolderName, Utils.c( eventHandler.getReplacement( event ) ) );
    }
}