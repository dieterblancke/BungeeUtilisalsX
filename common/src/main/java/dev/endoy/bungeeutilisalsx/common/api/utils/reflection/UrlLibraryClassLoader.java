package dev.endoy.bungeeutilisalsx.common.api.utils.reflection;

import dev.endoy.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;
import dev.endoy.bungeeutilisalsx.common.BuX;

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
