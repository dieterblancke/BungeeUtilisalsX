package be.dieterblancke.bungeeutilisalsx.webapi.auth;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InsufficientPermissionsException extends RuntimeException
{
    public InsufficientPermissionsException()
    {
    }

    public InsufficientPermissionsException( String message )
    {
        super( message );
    }

    public InsufficientPermissionsException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public InsufficientPermissionsException( Throwable cause )
    {
        super( cause );
    }

    public InsufficientPermissionsException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace )
    {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
