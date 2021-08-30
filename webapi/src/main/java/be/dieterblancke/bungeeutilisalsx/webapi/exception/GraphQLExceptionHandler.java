package be.dieterblancke.bungeeutilisalsx.webapi.exception;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.language.SourceLocation;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;

@Component
public class GraphQLExceptionHandler
{

    @ExceptionHandler( RuntimeException.class )
    public GraphQLError handleException( RuntimeException exception )
    {
        if ( exception instanceof AuthException )
        {
            return handleException( (AuthException) exception );
        }

        return GraphqlErrorBuilder.newError().message( "Internal Server Error(s) while executing query" ).build();
    }

    private GraphQLError handleException( final AuthException exception )
    {
        return GraphqlErrorBuilder.newError()
                .message( exception.getMessage() )
                .errorType( DefaultErrorClassification.AuthError )
                .build();
    }

    public enum DefaultErrorClassification implements ErrorClassification
    {
        AuthError
    }
}
