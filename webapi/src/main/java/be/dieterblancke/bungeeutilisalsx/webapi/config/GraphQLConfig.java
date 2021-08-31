package be.dieterblancke.bungeeutilisalsx.webapi.config;

import be.dieterblancke.bungeeutilisalsx.webapi.coercings.DateCoercing;
import be.dieterblancke.bungeeutilisalsx.webapi.coercings.DateTimeCoercing;
import be.dieterblancke.bungeeutilisalsx.webapi.coercings.UuidCoercing;
import graphql.scalar.GraphqlLongCoercing;
import graphql.schema.GraphQLScalarType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GraphQLConfig
{

    @Bean
    GraphQLScalarType longScalar()
    {
        return GraphQLScalarType.newScalar()
                .name( "Long" )
                .description( "Built-in Long" )
                .coercing( new GraphqlLongCoercing() )
                .build();
    }

    @Bean
    GraphQLScalarType dateScalar()
    {
        return GraphQLScalarType.newScalar()
                .name( "Date" )
                .description( "Java 8 LocalDate as scalar." )
                .coercing( new DateCoercing() )
                .build();
    }

    @Bean
    GraphQLScalarType dateTimeScalar()
    {
        return GraphQLScalarType.newScalar()
                .name( "DateTime" )
                .description( "Java 8 LocalDate as scalar." )
                .coercing( new DateTimeCoercing() )
                .build();
    }

    @Bean
    GraphQLScalarType uuidScalar()
    {
        return GraphQLScalarType.newScalar()
                .name( "UUID" )
                .description( "Java UUID as scalar." )
                .coercing( new UuidCoercing() )
                .build();
    }
}
