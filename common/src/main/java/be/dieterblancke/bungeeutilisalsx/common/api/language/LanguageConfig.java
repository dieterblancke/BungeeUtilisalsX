package be.dieterblancke.bungeeutilisalsx.common.api.language;

import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.text.MessageBuilder;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import com.dbsoftwares.configuration.api.IConfiguration;
import lombok.Data;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;
import java.util.function.Function;

@Data
public class LanguageConfig
{

    private final Language language;
    private final IConfiguration config;

    public String buildLangMessage( final String path, final Object... placeholders )
    {
        return this.buildLangMessage( path, null, null, placeholders );
    }

    public String buildLangMessage(
            final String path,
            final Function<String, String> prePlaceholderFormatter,
            final Function<String, String> postPlaceholderFormatter,
            final Object... placeholders )
    {
        if ( !config.exists( path ) )
        {
            return "";
        }
        final StringBuilder builder = new StringBuilder();

        if ( config.isList( path ) )
        {
            final List<String> messages = config.getStringList( path );

            if ( messages.isEmpty() )
            {
                return "";
            }

            for ( int i = 0; i < messages.size(); i++ )
            {
                final String message = Utils.replacePlaceHolders(
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
            final String message = Utils.replacePlaceHolders(
                    config.getString( path ),
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

    public void sendLangMessage( final User user, final String path )
    {
        this.sendLangMessage( user, true, path );
    }

    public void sendLangMessage( final User user, final boolean prefix, final String path )
    {
        this.sendLangMessage( user, prefix, path, new Object[0] );
    }

    public void sendLangMessage( final User user, final String path, final Object... placeholders )
    {
        this.sendLangMessage( user, true, path, placeholders );
    }

    public void sendLangMessage( final User user,
                                 final boolean prefix,
                                 final String path,
                                 final Object... placeholders )
    {
        this.sendLangMessage( user, path, prefix, null, null, placeholders );
    }

    public void sendLangMessage( User user,
                                 String path,
                                 boolean prefix,
                                 Function<String, String> prePlaceholderFormatter,
                                 Function<String, String> postPlaceholderFormatter,
                                 Object... placeholders )
    {
        if ( config.isSection( path ) )
        {
            // section detected, assuming this is a message to be handled by MessageBuilder (hover / focus events)
            final TextComponent component = MessageBuilder.buildMessage(
                    user, config.getSection( path ), prePlaceholderFormatter, postPlaceholderFormatter, placeholders
            );

            user.sendMessage( component );
            return;
        }

        String message = buildLangMessage( path, prePlaceholderFormatter, postPlaceholderFormatter, placeholders );

        if ( message.isEmpty() )
        {
            return;
        }

        if ( message.startsWith( "noprefix: " ) )
        {
            prefix = false;
            message = message.replaceFirst( "noprefix: ", "" );
        }

        if ( prefix )
        {
            user.sendMessage( message );
        }
        else
        {
            user.sendRawColorMessage( message );
        }
    }
}
