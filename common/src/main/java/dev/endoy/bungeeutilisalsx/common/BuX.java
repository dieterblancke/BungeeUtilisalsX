package dev.endoy.bungeeutilisalsx.common;

import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.logging.Logger;

public class BuX
{

    public static <T extends AbstractBungeeUtilisalsX> T getInstance()
    {
        return (T) AbstractBungeeUtilisalsX.getInstance();
    }

    public static IBuXApi getApi()
    {
        return getInstance().getApi();
    }

    public static Logger getLogger( final String name )
    {
        return getLogger();
    }

    public static Logger getLogger()
    {
        return getInstance().getLogger();
    }

    public static void debug( final String message )
    {
        if ( ConfigFiles.CONFIG.isDebug() )
        {
            getLogger().info( message );
        }
    }
}
