package be.dieterblancke.bungeeutilisalsx.common.util;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.Config;
import be.dieterblancke.configuration.api.IConfiguration;
import be.dieterblancke.configuration.json.JsonConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.function.Consumer;

public class TestInjectionUtil
{
    private TestInjectionUtil()
    {
        // emptry constructor
    }

    public static void injectStorageManager( final AbstractStorageManager abstractStorageManager )
    {
        BuX.getInstance().setAbstractStorageManager( abstractStorageManager );
    }

    public static void injectEmptyConfiguration( final Config config ) throws IOException, NoSuchFieldException, IllegalAccessException
    {
        injectConfiguration( config, new JsonConfiguration( (InputStream) null ) );
    }

    public static void injectConfiguration( final Config config,
                                            final IConfiguration configuration ) throws NoSuchFieldException, IllegalAccessException
    {
        Field field;
        try
        {
            field = config.getClass().getDeclaredField( "config" );
        }
        catch ( NoSuchFieldException e )
        {
            field = config.getClass().getSuperclass().getDeclaredField( "config" );
        }

        field.setAccessible( true );
        field.set( config, configuration );
    }
}
