package be.dieterblancke.bungeeutilisalsx.webapi.scalarresolvers;

import graphql.schema.*;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateTimeScalar extends GraphQLScalarType
{

    public DateTimeScalar()
    {
        super( "DateTime", "An Full Date Time Scalar", new Coercing<LocalDateTime, String>()
        {
            public String serialize( Object input ) throws CoercingSerializeException
            {
                return input.toString();
            }

            public LocalDateTime parseValue( Object input ) throws CoercingParseValueException
            {
                return LocalDateTime.parse( (String) input );
            }

            public LocalDateTime parseLiteral( Object input ) throws CoercingParseLiteralException
            {
                return LocalDateTime.parse( (String) input );
            }
        } );
    }
}