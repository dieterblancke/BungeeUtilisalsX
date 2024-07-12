package dev.endoy.bungeeutilisalsx.common.storage.file;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

public interface UnclosableConnection extends Connection
{

    static UnclosableConnection wrap( final Connection connection )
    {
        return (UnclosableConnection) Proxy.newProxyInstance(
                UnclosableConnection.class.getClassLoader(),
                new Class[]{ UnclosableConnection.class },
                new Handler( connection )
        );
    }

    void shutdown();

    final class Handler implements InvocationHandler
    {

        private final Connection connection;

        Handler( final Connection connection )
        {
            this.connection = connection;
        }

        @Override
        public Object invoke( final Object proxy, final Method method, final Object[] args ) throws Throwable
        {
            if ( method.getName().equals( "close" ) )
            {
                return null;
            }

            if ( method.getName().equals( "shutdown" ) )
            {
                this.connection.close();
                return null;
            }

            return method.invoke( this.connection, args );
        }
    }
}