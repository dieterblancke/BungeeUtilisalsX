package be.dieterblancke.bungeeutilisalsx.common.chat;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.text.UnicodeTranslator;
import com.dbsoftwares.configuration.api.ISection;

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

        for ( char replaceableChar : message.toCharArray() )
        {
            int i = 0;
            boolean charFound = false;
            while ( !charFound && i < chars.length )
            {
                if ( chars[i] == replaceableChar )
                {
                    charFound = true;
                }
                i++;
            }
            if ( charFound )
            {
                builder.append( (char) ( 65248 + replaceableChar ) );
            }
            else
            {
                builder.append( replaceableChar );
            }
        }

        return builder.toString();
    }
}
