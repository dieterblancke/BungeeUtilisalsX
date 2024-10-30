package dev.endoy.bungeeutilisalsx.common.announcers.title;

import dev.endoy.configuration.api.ISection;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class TitleMessage
{
    String title;
    String subtitle;
    int fadeIn;
    int stay;
    int fadeOut;

    public TitleMessage( final ISection section )
    {
        this(
            section.getString( "title" ),
            section.getString( "subtitle" ),
            section.getInteger( "fadein" ),
            section.getInteger( "stay" ),
            section.getInteger( "fadeout" )
        );
    }
}
