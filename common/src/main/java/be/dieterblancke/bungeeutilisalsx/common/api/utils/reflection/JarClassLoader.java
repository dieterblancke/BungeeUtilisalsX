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
import java.util.logging.Level;

public class JarClassLoader
{

    private static final Method ADD_URL;

    static
    {
        ADD_URL = ReflectionUtils.getMethod( URLClassLoader.class, "addURL", URL.class );
    }

    private final URLClassLoader classLoader;

    public JarClassLoader()
    {
        final ClassLoader loader = AbstractBungeeUtilisalsX.class.getClassLoader();

        if ( loader instanceof URLClassLoader )
        {
            this.classLoader = (URLClassLoader) loader;
        }
        else
        {
            throw new IllegalStateException( "Plugin ClassLoader is not instance of URLClassLoader" );
        }
    }

    public void loadJar( URL url )
    {
        try
        {
            ADD_URL.invoke( this.classLoader, url );
        }
        catch ( IllegalAccessException | InvocationTargetException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    public void loadJar( File file )
    {
        try
        {
            loadJar( file.toURI().toURL() );
        }
        catch ( MalformedURLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }
}
