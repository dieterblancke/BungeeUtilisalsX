package dev.endoy.bungeeutilisalsx.common.api.placeholder.xml;

import dev.endoy.bungeeutilisalsx.common.api.placeholder.event.PlaceHolderEvent;
import dev.endoy.bungeeutilisalsx.common.api.placeholder.placeholders.ClassPlaceHolder;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import com.google.common.collect.Lists;
import lombok.EqualsAndHashCode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.List;

@EqualsAndHashCode( callSuper = true )
public class XMLPlaceHolders extends ClassPlaceHolder
{

    private final List<XMLPlaceHolder> placeHolders = Lists.newArrayList();

    public XMLPlaceHolders()
    {
        super( "xml", false );
    }

    @Override
    public String getReplacement( final PlaceHolderEvent placeHolderEvent )
    {
        return null;
    }

    @Override
    public String format( final User user, String str )
    {
        final Document document = Jsoup.parse( str );
        document.outputSettings().prettyPrint( false );
        document.outputSettings().charset( "UTF-8" );

        for ( XMLPlaceHolder placeHolder : placeHolders )
        {
            str = placeHolder.format( user, str, document );
        }

        return str;
    }

    public void addXmlPlaceHolder( final XMLPlaceHolder placeHolder )
    {
        this.placeHolders.add( placeHolder );
    }
}
