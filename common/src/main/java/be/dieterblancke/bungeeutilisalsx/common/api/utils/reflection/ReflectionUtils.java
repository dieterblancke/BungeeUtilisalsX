package be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection;

import be.dieterblancke.bungeeutilisalsx.common.BuX;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class ReflectionUtils
{

    private ReflectionUtils()
    {
    }

    public static Object getHandle( Class<?> clazz, Object o )
    {
        try
        {
            return clazz.getMethod( "getHandle" ).invoke( o );
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    public static Object getHandle( Object o )
    {
        try
        {
            final Method getHandle = getMethod( "getHandle", o.getClass() );

            if ( getHandle == null )
            {
                return null;
            }
            else
            {
                return getHandle.invoke( o );
            }
        }
        catch ( Exception e )
        {
            return null;
        }
    }

    public static Class<?> getClass( String name )
    {
        try
        {
            return Class.forName( name );
        }
        catch ( Exception e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
        return null;
    }

    public static Boolean isLoaded( String clazz )
    {
        try
        {
            Class.forName( clazz );
            return true;
        }
        catch ( Exception e )
        {
            return false;
        }
    }

    public static Method getMethod( String name, Class<?> clazz, Class<?>... paramTypes )
    {
        for ( Method m : clazz.getMethods() )
        {
            Class<?>[] types = m.getParameterTypes();
            if ( m.getName().equals( name ) && equalsTypeArray( types, paramTypes ) )
            {
                return m;
            }
        }
        return null;
    }

    public static Method getMethod( Class<?> clazz, String name, Class<?>... args )
    {
        for ( Method m : clazz.getDeclaredMethods() )
        {
            if ( m.getName().equals( name ) && ( args.length == 0 || classList( args, m.getParameterTypes() ) ) )
            {
                m.setAccessible( true );
                return m;
            }
        }
        for ( Method m : clazz.getMethods() )
        {
            if ( m.getName().equals( name ) && ( args.length == 0 || classList( args, m.getParameterTypes() ) ) )
            {
                m.setAccessible( true );
                return m;
            }
        }
        return null;
    }

    public static Field getField( Class<?> clazz, String name )
    {
        try
        {
            Field field = clazz.getDeclaredField( name );
            field.setAccessible( true );

            return field;
        }
        catch ( Exception e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
        return null;
    }

    public static Object getValue( Object instance, Class<?> clazz, String fieldName ) throws IllegalAccessException
    {
        final Field field = getField( clazz, fieldName );

        if ( field == null )
        {
            return null;
        }
        else
        {
            return field.get( instance );
        }
    }

    public static Object getValue( Object instance, String fieldName ) throws IllegalAccessException
    {
        return getValue( instance, instance.getClass(), fieldName );
    }

    public static void setValue( Object instance, Class<?> clazz, String fieldName, Object value ) throws IllegalAccessException
    {
        final Field field = getField( clazz, fieldName );

        if ( field != null )
        {
            field.set( instance, value );
        }
    }

    public static void setValue( Object instance, String fieldName, Object value ) throws IllegalAccessException
    {
        setValue( instance, instance.getClass(), fieldName, value );
    }

    public static Constructor<?> getConstructor( Class<?> clazz, Class<?>... parameterTypes ) throws NoSuchMethodException
    {
        return clazz.getConstructor( parameterTypes );
    }

    private static boolean equalsTypeArray( Class<?>[] a, Class<?>[] o )
    {
        if ( a.length != o.length )
        {
            return false;
        }
        for ( int i = 0; i < a.length; i++ )
        {
            if ( !a[i].equals( o[i] ) && !a[i].isAssignableFrom( o[i] ) )
            {
                return false;
            }
        }
        return true;
    }

    private static boolean classList( Class<?>[] l1, Class<?>[] l2 )
    {
        boolean equal = true;
        if ( l1.length != l2.length )
        {
            return false;
        }
        for ( int i = 0; i < l1.length; i++ )
        {
            if ( l1[i] != l2[i] )
            {
                equal = false;
                break;
            }
        }
        return equal;
    }

    public static int getJavaVersion()
    {
        String version = System.getProperty( "java.version" );
        if ( version.startsWith( "1." ) )
        {
            version = version.substring( 2 );
        }
        final int dotPos = version.indexOf( '.' );
        final int dashPos = version.indexOf( '-' );
        return Integer.parseInt( version.substring( 0, dotPos > -1 ? dotPos : dashPos > -1 ? dashPos : 1 ) );
    }
}