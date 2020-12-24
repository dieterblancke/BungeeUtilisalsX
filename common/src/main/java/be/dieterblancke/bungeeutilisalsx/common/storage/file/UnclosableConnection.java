/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package be.dieterblancke.bungeeutilisalsx.common.storage.file;

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