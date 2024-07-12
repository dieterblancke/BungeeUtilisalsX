package dev.endoy.bungeeutilisalsx.common.placeholders;

import dev.endoy.bungeeutilisalsx.common.api.placeholder.xml.XMLPlaceHolder;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.MessageUtils;
import dev.endoy.bungeeutilisalsx.common.api.utils.text.FontWidthInfo;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CenterPlaceHolder extends XMLPlaceHolder
{

    @Override
    public String format( final User user, String original, final Document doc )
    {
        final Elements elements = doc.select( "center" );

        for ( Element element : elements )
        {
            final String text = element.text();
            final String padChar = element.attr( "padchar" );

            if ( text.isEmpty() || text.trim().isEmpty() )
            {
                return original.replace( element.outerHtml().replace( "&amp;", "&" ), text );
            }

            return original.replace( element.outerHtml().replace( "&amp;", "&" ), this.center(
                    text,
                    padChar.isEmpty() ? ' ' : padChar.charAt( 0 )
            ) );
        }
        return original;
    }

    private String center( String text, final char pad )
    {
        final int length = getStringWidth( text );
        final int toCompensate = 154 - ( length / 2 );
        final int padLength = FontWidthInfo.getFontWidthInfo( pad ).getLength() + 1;
        final StringBuilder sb = new StringBuilder();
        int compensated = 0;

        while ( compensated < toCompensate )
        {
            sb.append( pad );
            compensated += padLength;
        }
        sb.append( text );
        sb.append( "&r" );

        return sb.toString();
    }

    private int getStringWidth( String text )
    {
        int length = 0;
        boolean nextIsColour = false;
        boolean bold = false;
        int chars = 0;

        for ( char c : text.toCharArray() )
        {
            if ( c == '&' || c == MessageUtils.SECTION_CHAR )
            {
                nextIsColour = true;
            }
            else if ( nextIsColour )
            {
                if ( c == 'l' )
                {
                    bold = true;
                }
                else if ( c == 'r' )
                {
                    bold = false;
                }
                nextIsColour = false;
            }
            else
            {
                FontWidthInfo info = FontWidthInfo.getFontWidthInfo( c );
                length += bold ? info.getBoldLength() : info.getLength();
                chars++;
            }
        }
        return length + chars;
    }
}
