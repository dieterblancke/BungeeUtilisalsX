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

package be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection;

import be.dieterblancke.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.common.BuX;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Optional;
import java.util.logging.Level;

public class UrlLibraryClassLoader implements LibraryClassLoader
{

    static Method ADD_URL;

    static
    {
        try
        {
            ADD_URL = URLClassLoader.class.getDeclaredMethod( "addURL", URL.class );
            io.github.karlatemp.unsafeaccessor.Root.openAccess( ADD_URL );
        }
        catch ( NoSuchMethodException e )
        {
            e.printStackTrace();
        }
    }

    final URLClassLoader classLoader;

    public UrlLibraryClassLoader()
    {
        final ClassLoader loader = AbstractBungeeUtilisalsX.class.getClassLoader();

        if ( loader instanceof URLClassLoader )
        {
            this.classLoader = (URLClassLoader) loader;
        }
        else
        {
            this.classLoader = Optional.ofNullable( DynamicClassLoader.findAncestor( loader ) )
                    .orElseGet( () -> new DynamicClassLoader( ClassLoader.getSystemClassLoader() ) );
        }
    }

    @Override
    public void loadJar( File file )
    {
        try
        {
            ADD_URL.invoke( this.classLoader, file.toURI().toURL() );
        }
        catch ( MalformedURLException | IllegalAccessException | InvocationTargetException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }
}
