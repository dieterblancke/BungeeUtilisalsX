package dev.endoy.bungeeutilisalsx.webapi.util;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.IPluginDescription;
import dev.endoy.bungeeutilisalsx.webapi.Bootstrap;

import java.io.File;
import java.net.URISyntaxException;
import java.util.logging.Level;

public class SpringPluginDescription implements IPluginDescription
{
    @Override
    public String getName()
    {
        return "BungeeUtilisalsX";
    }

    @Override
    public String getVersion()
    {
        return "";
    }

    @Override
    public File getFile()
    {
        try
        {
            final File jarFile = new File( Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI() );

            return jarFile.isDirectory() ? jarFile : jarFile.getParentFile();
        }
        catch ( URISyntaxException e )
        {
            BuX.getLogger().log( Level.WARNING, "Could not invoke get executing file.", e );
            return null;
        }
    }
}
