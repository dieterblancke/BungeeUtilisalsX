package dev.endoy.bungeeutilisalsx.common.api.bossbar;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

public class BarStyle
{

    public static final BarStyle SOLID;
    public static final BarStyle SIX_SEGMENTS;
    public static final BarStyle TEN_SEGMENTS;
    public static final BarStyle TWELVE_SEGMENTS;
    public static final BarStyle TWENTY_SEGMENTS;

    public static final List<BarStyle> values;

    static
    {
        SOLID = new BarStyle( 0 );
        SIX_SEGMENTS = new BarStyle( 1 );
        TEN_SEGMENTS = new BarStyle( 2 );
        TWELVE_SEGMENTS = new BarStyle( 3 );
        TWENTY_SEGMENTS = new BarStyle( 4 );

        values = Lists.newArrayList( SOLID, SIX_SEGMENTS, TEN_SEGMENTS, TWELVE_SEGMENTS, TWENTY_SEGMENTS );
    }

    @Getter
    private final int id;

    public BarStyle( int id )
    {
        this.id = id;
    }

    public static BarStyle[] values()
    {
        return values.toArray( new BarStyle[0] );
    }

    public static BarStyle fromId( int action )
    {
        return values.stream().filter( a -> a.id == action ).findFirst().orElse( SOLID );
    }

    public static BarStyle valueOf( String style )
    {
        if ( style == null )
        {
            return SOLID;
        }
        switch ( style )
        {
            default:
            case "SOLID":
                return SOLID;
            case "SIX_SEGMENTS":
                return SIX_SEGMENTS;
            case "TEN_SEGMENTS":
                return TEN_SEGMENTS;
            case "TWELVE_SEGMENTS":
                return TWELVE_SEGMENTS;
            case "TWENTY_SEGMENTS":
                return TWENTY_SEGMENTS;
        }
    }

    @Override
    public boolean equals( Object obj )
    {
        return obj == this || ( obj instanceof BarStyle && ( (BarStyle) obj ).getId() == id );
    }
}