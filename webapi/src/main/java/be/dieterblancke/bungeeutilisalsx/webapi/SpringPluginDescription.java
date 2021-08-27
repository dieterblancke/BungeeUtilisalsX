package be.dieterblancke.bungeeutilisalsx.webapi;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.IPluginDescription;
import be.dieterblancke.bungeeutilisalsx.webapi.Bootstrap;
import org.springframework.stereotype.Component;

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
            return new File( Bootstrap.class.getProtectionDomain().getCodeSource().getLocation().toURI() );
        }
        catch ( URISyntaxException e )
        {
            BuX.getLogger().log( Level.WARNING, "Could not invoke get executing file.", e );
            return null;
        }
    }
}
