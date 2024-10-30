package graphql.execution;

import dev.endoy.bungeeutilisalsx.webapi.exception.AuthException;
import graphql.ExceptionWhileDataFetching;
import graphql.PublicApi;
import graphql.language.SourceLocation;
import graphql.util.LogKit;
import org.slf4j.Logger;

@PublicApi
public class SimpleDataFetcherExceptionHandler implements DataFetcherExceptionHandler
{
    private static final Logger logNotSafe = LogKit.getNotPrivacySafeLogger( SimpleDataFetcherExceptionHandler.class );

    public SimpleDataFetcherExceptionHandler()
    {
    }

    public DataFetcherExceptionHandlerResult onException( DataFetcherExceptionHandlerParameters handlerParameters )
    {
        final Throwable exception = handlerParameters.getException();
        final SourceLocation sourceLocation = handlerParameters.getSourceLocation();
        final ResultPath path = handlerParameters.getPath();
        final ExceptionWhileDataFetching error = new ExceptionWhileDataFetching( path, exception, sourceLocation );

        if ( !( exception instanceof AuthException ) )
        {
            logNotSafe.warn( error.getMessage(), exception );
        }

        return DataFetcherExceptionHandlerResult.newResult().error( error ).build();
    }
}