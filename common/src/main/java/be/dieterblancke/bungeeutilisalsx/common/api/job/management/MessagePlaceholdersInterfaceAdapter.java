package be.dieterblancke.bungeeutilisalsx.common.api.job.management;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MessagePlaceholdersInterfaceAdapter implements JsonSerializer<MessagePlaceholders>, JsonDeserializer<MessagePlaceholders>
{

    private final Gson gson = new Gson();

    @Override
    public JsonElement serialize( final MessagePlaceholders object, final Type interfaceType, final JsonSerializationContext context )
    {
        final JsonObject wrapper = new JsonObject();
        wrapper.addProperty( "type", object.getClass().getName() );
        wrapper.add( "data", gson.toJsonTree( object.getMessagePlaceholders().asArray() ) );
        return wrapper;
    }

    @Override
    public MessagePlaceholders deserialize( final JsonElement elem, final Type interfaceType, final JsonDeserializationContext context ) throws JsonParseException
    {
        final JsonObject wrapper = (JsonObject) elem;
        final JsonArray data = get( wrapper, "data" ).getAsJsonArray();

        List<Object> objects = new ArrayList<>();
        for ( JsonElement item : data )
        {
            objects.add( item.getAsString() );
        }

        return MessagePlaceholders.fromArray( objects.toArray() );
    }

    private JsonElement get( final JsonObject wrapper, String memberName )
    {
        final JsonElement elem = wrapper.get( memberName );
        if ( elem == null )
        {
            throw new JsonParseException( "No '" + memberName + "' member found in what was expected to be an interface wrapper" );
        }
        return elem;
    }
}
