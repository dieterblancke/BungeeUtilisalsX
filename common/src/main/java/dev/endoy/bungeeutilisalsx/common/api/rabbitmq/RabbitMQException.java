package dev.endoy.bungeeutilisalsx.common.api.rabbitmq;

public class RabbitMQException extends RuntimeException
{
    public RabbitMQException()
    {
    }

    public RabbitMQException( String message )
    {
        super( message );
    }

    public RabbitMQException( String message, Throwable cause )
    {
        super( message, cause );
    }

    public RabbitMQException( Throwable cause )
    {
        super( cause );
    }

    public RabbitMQException( String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace )
    {
        super( message, cause, enableSuppression, writableStackTrace );
    }
}
