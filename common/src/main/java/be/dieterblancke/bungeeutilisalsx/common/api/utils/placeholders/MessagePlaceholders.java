package be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class MessagePlaceholders implements HasMessagePlaceholders
{

    // TODO: look to use HasMessagePlaceholders and replace current placeholders with this class implementation
    private static final MessagePlaceholders EMPTY = new MessagePlaceholders()
    {
        @Override
        public String format( String input )
        {
            return input; // no-op implementation
        }
    };
    private final Map<String, Supplier<Object>> placeHolders = new HashMap<>();

    private MessagePlaceholders()
    {
    }

    public static MessagePlaceholders empty()
    {
        return EMPTY;
    }

    public static MessagePlaceholders create()
    {
        return new MessagePlaceholders();
    }

    public static MessagePlaceholders fromArray( final Object... placeholders )
    {
        final MessagePlaceholders messagePlaceholders = new MessagePlaceholders();

        for ( int i = 0; i < placeholders.length - 1; i += 2 )
        {
            messagePlaceholders.append( placeholders[i].toString(), placeholders[i + 1] );
        }

        return messagePlaceholders;
    }

    public MessagePlaceholders append( final String key, final Object value )
    {
        this.placeHolders.put( key, () -> value );
        return this;
    }

    public MessagePlaceholders append( final String key, final Supplier<Object> value )
    {
        this.placeHolders.put( key, value );
        return this;
    }

    public MessagePlaceholders append( final HasMessagePlaceholders hasMessagePlaceholders )
    {
        this.placeHolders.putAll( hasMessagePlaceholders.getMessagePlaceholders().placeHolders );
        return this;
    }

    public String format( String input )
    {
        for ( Map.Entry<String, Supplier<Object>> entry : placeHolders.entrySet() )
        {
            final String key = !entry.getKey().startsWith( "{" ) && !entry.getKey().endsWith( "}" )
                    ? "{" + entry.getKey() + "}"
                    : entry.getKey();

            if ( input.contains( key ) )
            {
                input = input.replace( key, String.valueOf( entry.getValue().get() ) );
            }
        }

        return input;
    }

    @Override
    public MessagePlaceholders getMessagePlaceholders()
    {
        return this;
    }

    public Object[] asArray()
    {
        List<Object> objects = new ArrayList<>();

        for ( Map.Entry<String, Supplier<Object>> entry : placeHolders.entrySet() )
        {
            objects.add( entry.getKey() );
            objects.add( entry.getValue().get() );
        }

        return objects.toArray();
    }
}