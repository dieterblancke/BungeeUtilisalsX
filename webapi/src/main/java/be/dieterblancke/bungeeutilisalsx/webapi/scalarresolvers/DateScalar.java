package be.dieterblancke.bungeeutilisalsx.webapi.scalarresolvers;

import graphql.language.StringValue;
import graphql.scalars.util.Kit;
import graphql.schema.*;
import org.springframework.stereotype.Component;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.function.Function;

@Component
public class DateScalar extends GraphQLScalarType
{

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern( "yyyy-MM-dd" );

    public DateScalar()
    {
        super( "Date", "An RFC-3339 compliant Full Date Scalar", new Coercing<LocalDate, String>()
        {
            public String serialize( Object input ) throws CoercingSerializeException
            {
                Object temporalAccessor;
                if ( input instanceof TemporalAccessor )
                {
                    temporalAccessor = input;
                }
                else
                {
                    if ( !( input instanceof String ) )
                    {
                        throw new CoercingSerializeException( "Expected a 'String' or 'java.time.temporal.TemporalAccessor' but was '" + Kit.typeName( input ) + "'." );
                    }

                    temporalAccessor = this.parseLocalDate( input.toString(), CoercingSerializeException::new );
                }

                try
                {
                    return DateScalar.dateFormatter.format( (TemporalAccessor) temporalAccessor );
                }
                catch ( DateTimeException var4 )
                {
                    throw new CoercingSerializeException( "Unable to turn TemporalAccessor into full date because of : '" + var4.getMessage() + "'." );
                }
            }

            public LocalDate parseValue( Object input ) throws CoercingParseValueException
            {
                Object temporalAccessor;
                if ( input instanceof TemporalAccessor )
                {
                    temporalAccessor = input;
                }
                else
                {
                    if ( !( input instanceof String ) )
                    {
                        throw new CoercingParseValueException( "Expected a 'String' or 'java.time.temporal.TemporalAccessor' but was '" + Kit.typeName( input ) + "'." );
                    }

                    temporalAccessor = this.parseLocalDate( input.toString(), CoercingParseValueException::new );
                }

                try
                {
                    return LocalDate.from( (TemporalAccessor) temporalAccessor );
                }
                catch ( DateTimeException var4 )
                {
                    throw new CoercingParseValueException( "Unable to turn TemporalAccessor into full date because of : '" + var4.getMessage() + "'." );
                }
            }

            public LocalDate parseLiteral( Object input ) throws CoercingParseLiteralException
            {
                if ( !( input instanceof StringValue ) )
                {
                    throw new CoercingParseLiteralException( "Expected AST type 'StringValue' but was '" + Kit.typeName( input ) + "'." );
                }
                else
                {
                    return this.parseLocalDate( ( (StringValue) input ).getValue(), CoercingParseLiteralException::new );
                }
            }

            private LocalDate parseLocalDate( String s, Function<String, RuntimeException> exceptionMaker )
            {
                try
                {
                    TemporalAccessor temporalAccessor = DateScalar.dateFormatter.parse( s );
                    return LocalDate.from( temporalAccessor );
                }
                catch ( DateTimeParseException var4 )
                {
                    throw exceptionMaker.apply( "Invalid RFC3339 full date value : '" + s + "'. because of : '" + var4.getMessage() + "'" );
                }
            }
        } );
    }
}