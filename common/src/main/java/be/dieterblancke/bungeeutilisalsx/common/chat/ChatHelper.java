package be.dieterblancke.bungeeutilisalsx.common.chat;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.text.UnicodeTranslator;
import com.dbsoftwares.configuration.api.ISection;
import net.md_5.bungee.api.ChatColor;

import java.nio.CharBuffer;

public class ChatHelper
{

    private static final String FANCY_FONT_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ,?;.:+=-%*$!'\"@|&<>0123456789#";

    public static String replaceSymbols( String message )
    {
        final ISection symbolsSection = ConfigFiles.UTFSYMBOLS.getConfig().getSection( "symbols.symbols" );

        for ( String key : symbolsSection.getKeys() )
        {
            final String value = symbolsSection.getString( key );

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
        }
        return message;
    }

    public static String changeToFancyFont( String message )
    {
        final char[] chars = FANCY_FONT_CHARACTERS.toCharArray();
        final StringBuilder builder = new StringBuilder();
        final char[] messageChars = message.toCharArray();

        for ( int i = 0; i < messageChars.length; i++ )
        {
            char replaceableChar = messageChars[i];

            if ( ( replaceableChar == '&' || replaceableChar == ChatColor.COLOR_CHAR )
                    && i < ( messageChars.length - 1 )
                    && ChatColor.getByChar( messageChars[i + 1] ) != null )
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
