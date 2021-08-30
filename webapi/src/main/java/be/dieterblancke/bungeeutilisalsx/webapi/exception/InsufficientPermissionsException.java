package be.dieterblancke.bungeeutilisalsx.webapi.exception;

public class InsufficientPermissionsException extends AuthException
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
