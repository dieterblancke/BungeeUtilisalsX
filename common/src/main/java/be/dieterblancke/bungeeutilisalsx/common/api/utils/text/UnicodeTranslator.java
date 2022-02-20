package be.dieterblancke.bungeeutilisalsx.common.api.utils.text;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public class UnicodeTranslator
{

    private static int translate( final CharSequence input, final int index, final Writer out ) throws IOException
    {
        if ( input.charAt( index ) == '\\' && index + 1 < input.length() && input.charAt( index + 1 ) == 'u' )
        {
            int i = 2;
            while ( index + i < input.length() && input.charAt( index + i ) == 'u' )
            {
                i++;
            }

            if ( index + i < input.length() && input.charAt( index + i ) == '+' )
            {
                i++;
            }

            if ( index + i + 4 <= input.length() )
            {
                final CharSequence unicode = input.subSequence( index + i, index + i + 4 );

                try
                {
                    final int value = Integer.parseInt( unicode.toString(), 16 );
                    out.write( (char) value );
                }
                catch ( final NumberFormatException nfe )
                {
                    throw new IllegalArgumentException( "Unable to parse unicode value: " + unicode, nfe );
                }
                return i + 4;
            }
            throw new IllegalArgumentException( "Less than 4 hex digits in unicode value: '"
                    + input.subSequence( index, input.length() )
                    + "' due to end of CharSequence" );
        }
        return 0;
    }

    public static String translate( final CharSequence input )
    {
        if ( input == null )
        {
            return null;
        }
        try
        {
            final StringWriter writer = new StringWriter( input.length() * 2 );
            translate( input, writer );
            return writer.toString();
        }
        catch ( final IOException ioe )
        {
            throw new RuntimeException( ioe );
        }
    }

    private static void translate( final CharSequence input, final Writer out ) throws IOException
    {
        if ( input == null )
        {
            return;
        }
        int pos = 0;
        final int len = input.length();
        while ( pos < len )
        {
            final int consumed = translate( input, pos, out );
            if ( consumed == 0 )
            {
                final char c1 = input.charAt( pos );
                out.write( c1 );
                pos++;
                if ( Character.isHighSurrogate( c1 ) && pos < len )
                {
                    final char c2 = input.charAt( pos );
                    if ( Character.isLowSurrogate( c2 ) )
                    {
                        out.write( c2 );
                        pos++;
                    }
                }
                continue;
            }
            for ( int pt = 0; pt < consumed; pt++ )
            {
                pos += Character.charCount( Character.codePointAt( input, pos ) );
            }
        }
    }
}