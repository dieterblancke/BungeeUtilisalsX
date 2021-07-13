package be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces;

import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.configuration.api.IConfiguration;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public interface MessageRecipient
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

    IConfiguration getLanguageConfig();

    default String buildLangMessage( final String path, final Object... placeholders )
    {
        return this.buildLangMessage( path, null, null, placeholders );
    }

    default String buildLangMessage(
            final String path,
            final Function<String, String> prePlaceholderFormatter,
            final Function<String, String> postPlaceholderFormatter,
            final Object... placeholders )
    {
        if ( !getLanguageConfig().exists( path ) )
        {
            return "";
        }
        final StringBuilder builder = new StringBuilder();

        if ( getLanguageConfig().isList( path ) )
        {
            final List<String> messages = getLanguageConfig().getStringList( path );

            if ( messages.isEmpty() )
            {
                return "";
            }

            for ( int i = 0; i < messages.size(); i++ )
            {
                final String message = replacePlaceHolders(
                        messages.get( i ),
                        prePlaceholderFormatter,
                        postPlaceholderFormatter,
                        placeholders
                );
                builder.append( message );

                if ( i < messages.size() - 1 )
                {
                    builder.append( "\n" );
                }
            }
        }
        else
        {
            final String message = replacePlaceHolders(
                    getLanguageConfig().getString( path ),
                    prePlaceholderFormatter,
                    postPlaceholderFormatter,
                    placeholders
            );

            if ( message.isEmpty() )
            {
                return "";
            }

            builder.append( message );
        }
        return builder.toString();
    }
}
