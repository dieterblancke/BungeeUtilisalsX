package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.flattener.ComponentFlattener;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import java.util.HashMap;
import java.util.Map;

public class MessageUtils
{

    private static final LegacyComponentSerializer LEGACY_COMPONENT_SERIALIZER = LegacyComponentSerializer
            .builder()
            .flattener( ComponentFlattener.textOnly() )
            .build();
    private static final Map<String, String> COLOR_MAPPINGS = new HashMap<>()
    {{
        put( "0", "black" );
        put( "1", "dark_blue" );
        put( "2", "dark_green" );
        put( "3", "dark_aqua" );
        put( "4", "dark_red" );
        put( "5", "dark_purple" );
        put( "6", "gold" );
        put( "7", "gray" );
        put( "8", "dark_gray" );
        put( "9", "blue" );
        put( "a", "green" );
        put( "b", "aqua" );
        put( "c", "red" );
        put( "d", "light_purple" );
        put( "e", "yellow" );
        put( "f", "white" );
        put( "k", "obfuscated" );
        put( "l", "bold" );
        put( "m", "strikethrough" );
        put( "n", "underline" );
        put( "o", "italic" );
        put( "r", "reset" );
    }};

    private MessageUtils()
    {
    }

    public static Component fromText( String text )
    {
        for ( Map.Entry<String, String> entry : COLOR_MAPPINGS.entrySet() )
        {
            text = text.replace( "&" + entry.getKey(), "<" + entry.getValue() + ">" );
        }

        return MiniMessage.miniMessage().deserialize( text );
    }

    public static Component fromTextNoColors( String text )
    {
        return LEGACY_COMPONENT_SERIALIZER.deserialize( text );
    }
}
