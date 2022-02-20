package be.dieterblancke.bungeeutilisalsx.common.api.placeholder.placeholders;

import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.InputPlaceHolderEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.handler.InputPlaceHolderEventHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.EqualsAndHashCode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@EqualsAndHashCode( callSuper = true )
public class InputPlaceHolder extends PlaceHolder
{

    private final Pattern pattern;

    public InputPlaceHolder( boolean requiresUser, String prefix, InputPlaceHolderEventHandler handler )
    {
        super( null, requiresUser, handler );

        this.pattern = makePlaceholderWithArgsPattern( prefix );
    }

    private static Pattern makePlaceholderWithArgsPattern( String prefix )
    {
        return Pattern.compile( "(\\{" + Pattern.quote( prefix ) + ":)(.+?)(})" );
    }

    private static String extractArgumentFromPlaceholder( Matcher matcher )
    {
        return matcher.group( 2 ).trim();
    }

    @Override
    public String format( User user, String message )
    {
        Matcher matcher = pattern.matcher( message );

        while ( matcher.find() )
        {
            final String argument = extractArgumentFromPlaceholder( matcher );
            final InputPlaceHolderEvent event = new InputPlaceHolderEvent( user, this, message, argument );

            message = message.replace( matcher.group(), eventHandler.getReplacement( event ) );
        }

        return message;
    }
}