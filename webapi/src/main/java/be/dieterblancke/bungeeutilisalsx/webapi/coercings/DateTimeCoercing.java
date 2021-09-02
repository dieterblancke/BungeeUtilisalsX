package be.dieterblancke.bungeeutilisalsx.webapi.coercings;

import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DateTimeCoercing implements Coercing<LocalDateTime, String>
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
}