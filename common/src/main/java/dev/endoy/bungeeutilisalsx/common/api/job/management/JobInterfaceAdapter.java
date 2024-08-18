package dev.endoy.bungeeutilisalsx.common.api.job.management;

import com.google.gson.*;
import dev.endoy.bungeeutilisalsx.common.api.job.Job;

import java.lang.reflect.Type;

public class JobInterfaceAdapter implements JsonSerializer<Job>, JsonDeserializer<Job>
{

    private final Gson gson = new Gson();

    @Override
    public JsonElement serialize( final Job object, final Type interfaceType, final JsonSerializationContext context )
    {
        final JsonObject wrapper = new JsonObject();
        wrapper.addProperty( "type", object.getClass().getName() );
        wrapper.add( "data", gson.toJsonTree( object ) );
        return wrapper;
    }

    @Override
    public Job deserialize( final JsonElement elem, final Type interfaceType, final JsonDeserializationContext context ) throws JsonParseException
    {
        final JsonObject wrapper = (JsonObject) elem;
        final JsonElement typeName = get( wrapper, "type" );
        final JsonElement data = get( wrapper, "data" );
        final Type actualType = typeForName( typeName );

        return gson.fromJson( data, actualType );
    }

    private Type typeForName( final JsonElement typeElem )
    {
        try
        {
            return Class.forName( typeElem.getAsString() );
        }
        catch ( ClassNotFoundException e )
        {
            throw new JsonParseException( e );
        }
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