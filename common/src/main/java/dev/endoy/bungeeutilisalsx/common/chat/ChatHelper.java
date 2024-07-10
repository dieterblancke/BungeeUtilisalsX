package dev.endoy.bungeeutilisalsx.common.chat;

import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.UnicodeTranslator;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.configuration.api.ISection;

import java.nio.CharBuffer;
import java.util.List;

public class ChatHelper
{

    private static final String FANCY_FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,?;.:+=-%*$!'\"@|&<>0123456789#";

    public static String replaceSymbols( final User user, String message )
    {
        if ( ConfigFiles.UTFSYMBOLS.getConfig().isList( "symbols.symbols" ) )
        {
            final List<ISection> sections = ConfigFiles.UTFSYMBOLS.getConfig().getSectionList( "symbols.symbols" );

            for ( ISection section : sections )
            {
                if ( section.exists( "permission" ) )
                {
                    if ( !user.hasPermission( section.getString( "permission" ) ) )
                    {
                        continue;
                    }
                }

                message = replaceSymbol( message, section.getString( "symbol" ), section.getString( "triggers" ) );
            }
        }
        else
        {
            final ISection symbolsSection = ConfigFiles.UTFSYMBOLS.getConfig().getSection( "symbols.symbols" );

            for ( String key : symbolsSection.getKeys() )
            {
                message = replaceSymbol( message, key, symbolsSection.getString( key ) );
            }
        }
        return message;
    }

    private static String replaceSymbol( String message, final String key, final String value )
    {
        if ( value.contains( ", " ) )
        {
            for ( String val : value.split( ", " ) )
            {
                message = message.replace( val, UnicodeTranslator.translate( key ) );
            }
        }
        else
        {
            message = message.replace( value, UnicodeTranslator.translate( key ) );
        }
        return message;
    }

    public static String changeToFancyFont( final String message )
    {
        final char[] chars = FANCY_FONT_CHARACTERS.toCharArray();
        final StringBuilder builder = new StringBuilder();
        final char[] messageChars = message.toCharArray();

        for ( int i = 0; i < messageChars.length; i++ )
        {
            char replaceableChar = messageChars[i];

            if ( replaceableChar == '&' && i < ( messageChars.length - 1 ) )
            {
                builder.append( replaceableChar );
                builder.append( messageChars[i + 1] );
                i++;
            }
            else
            {
                final boolean charFound = CharBuffer.wrap( chars ).chars().mapToObj( ch -> (char) ch ).anyMatch( c -> c == replaceableChar );

                if ( charFound )
                {
                    builder.append( (char) ( 65248 + replaceableChar ) );
                }
                else
                {
                    builder.append( replaceableChar );
                }
            }
        }

        return builder.toString();
    }
}
