
package be.dieterblancke.bungeeutilisalsx.webapi.exception;

public class InvalidApiKeyException extends AuthException
{
    public InvalidApiKeyException()
    {
    }

    public InvalidApiKeyException( String message )
    {
        super( message );
    }

    public InvalidApiKeyException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public InvalidApiKeyException( Throwable cause )
    {
        super( cause );
    }

    public InvalidApiKeyException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace )
    {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
