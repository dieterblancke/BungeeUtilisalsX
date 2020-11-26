package com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces;

import com.dbsoftwares.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Collection;
import java.util.function.Function;

public interface CanReceiveMessages
{

    default boolean isEmpty( final BaseComponent component )
    {
        if ( !( component instanceof TextComponent ) || !( (TextComponent) component ).getText().isEmpty() )
        {
            return false;
        }
        return this.isEmpty( component.getExtra() );
    }

    default boolean isEmpty( final BaseComponent[] components )
    {
        if ( components == null )
        {
            return true;
        }
        for ( BaseComponent component : components )
        {
            if ( !isEmpty( component ) )
            {
                return false;
            }
        }
        return true;
    }

    default boolean isEmpty( final Collection<BaseComponent> components )
    {
        if ( components == null )
        {
            return true;
        }
        for ( BaseComponent component : components )
        {
            if ( !isEmpty( component ) )
            {
                return false;
            }
        }
        return true;
    }

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
