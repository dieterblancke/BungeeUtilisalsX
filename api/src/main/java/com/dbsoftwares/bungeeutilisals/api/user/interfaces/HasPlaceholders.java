package com.dbsoftwares.bungeeutilisals.api.user.interfaces;

import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;

import java.util.function.Function;

public interface HasPlaceholders
{

    default String replacePlaceHolders( String message,
                                        final Function<String, String> prePlaceholderFormatter,
                                        final Function<String, String> postPlaceholderFormatter,
                                        final Object... placeholders )
    {
        if ( prePlaceholderFormatter != null )
        {
            message = prePlaceholderFormatter.apply( message );
        }
        for ( int i = 0; i < placeholders.length - 1; i += 2 )
        {
            message = message.replace( placeholders[i].toString(), placeholders[i + 1].toString() );
        }
        message = PlaceHolderAPI.formatMessage( (User) this, message );
        if ( postPlaceholderFormatter != null )
        {
            message = postPlaceholderFormatter.apply( message );
        }
        return message;
    }

    default String replacePlaceHolders( String message, final Object... placeholders )
    {
        for ( int i = 0; i < placeholders.length - 1; i += 2 )
        {
            message = message.replace( placeholders[i].toString(), placeholders[i + 1].toString() );
        }
        message = PlaceHolderAPI.formatMessage( (User) this, message );
        return message;
    }
}
