package be.dieterblancke.bungeeutilisalsx.webapi.error;

import be.dieterblancke.bungeeutilisalsx.webapi.auth.InsufficientPermissionsException;
import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.language.SourceLocation;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

@Component
public class GraphQLExceptionHandler
{

    @ExceptionHandler( RuntimeException.class )
    public GraphQLError exceptionHandler( RuntimeException exception )
    {
        if ( exception instanceof UndeclaredThrowableException && exception.getCause() instanceof ExecutionException && exception.getCause().getCause() instanceof ExecutionException )
        {
            final Throwable e = exception.getCause().getCause().getCause();

            if ( e instanceof InsufficientPermissionsException )
            {
                return exceptionHandler( (InsufficientPermissionsException) e );
            }
        }

        return GraphqlErrorBuilder.newError().message( "Internal Server Error(s) while executing query" ).build();
    }

    private GraphQLError exceptionHandler( InsufficientPermissionsException exception )
    {
        return GraphqlErrorBuilder.newError()
                .message( exception.getMessage() )
                .errorType( DefaultErrorClassification.AuthError )
                .extensions( new HashMap<String, Object>()
                {{
                    put( "source", toSourceLocation( exception ) );
                }} )
                .build();
    }

    private SourceLocation toSourceLocation( Throwable t )
    {
        if ( t.getStackTrace().length == 0 )
        {
            return null;
        }
        final StackTraceElement st = t.getStackTrace()[0];
        return new SourceLocation( st.getLineNumber(), -1, st.toString() );
    }

    public enum DefaultErrorClassification implements ErrorClassification
    {
        AuthError
    }
}
