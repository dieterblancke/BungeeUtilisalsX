package be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;

// -Djava.system.class.loader=be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection.DynamicClassLoader
public class DynamicClassLoader extends URLClassLoader
{

    static
    {
        registerAsParallelCapable();
    }

    public DynamicClassLoader( final ClassLoader parent )
    {
        super( new URL[0], parent );
    }

    public DynamicClassLoader()
    {
        this( Thread.currentThread().getContextClassLoader() );
    }

    public static DynamicClassLoader findAncestor( ClassLoader classLoader )
    {
        while ( classLoader != null )
        {
            if ( classLoader instanceof DynamicClassLoader )
            {
                return (DynamicClassLoader) classLoader;
            }

            classLoader = classLoader.getParent();
        }

        return null;
    }

    public void add( URL url )
    {
        addURL( url );
    }

    /*
     *  Required for Java Agents when this classloader is used as the system classloader
     */
    @SuppressWarnings( "unused" )
    private void appendToClassPathForInstrumentation( String jarfile ) throws IOException
    {
        add( Paths.get( jarfile ).toRealPath().toUri().toURL() );
    }
}
