package be.dieterblancke.bungeeutilisalsx.webapi.coercings;

import graphql.language.StringValue;
import graphql.scalars.util.Kit;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidCoercing implements Coercing<UUID, String>
{

    public String serialize( Object input ) throws CoercingSerializeException
    {
        return input.toString();
    }

    public UUID parseValue( Object input ) throws CoercingParseValueException
    {
        if ( !( input instanceof String ) )
        {
            throw new CoercingParseValueException( "Expected a String value." );
        }
        return UUID.fromString( input.toString() );
    }

    public UUID parseLiteral( Object input ) throws CoercingParseLiteralException
    {
        if ( !( input instanceof StringValue ) )
        {
            throw new CoercingParseLiteralException( "Expected AST type 'StringValue' but was '" + Kit.typeName( input ) + "'." );
        }
        else
        {
            return UUID.fromString( ( (StringValue) input ).getValue() );
        }
    }
}
