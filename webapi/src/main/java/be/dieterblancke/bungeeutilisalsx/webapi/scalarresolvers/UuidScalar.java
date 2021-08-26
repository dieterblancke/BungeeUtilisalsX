package be.dieterblancke.bungeeutilisalsx.webapi.scalarresolvers;

import graphql.language.StringValue;
import graphql.scalars.util.Kit;
import graphql.schema.*;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UuidScalar extends GraphQLScalarType
{
    public UuidScalar()
    {
        super( "UUID", "UUID Scalar", new Coercing<UUID, String>()
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
        } );
    }
}
