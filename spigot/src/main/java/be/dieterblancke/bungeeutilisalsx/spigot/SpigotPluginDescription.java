package be.dieterblancke.bungeeutilisalsx.spigot;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.IPluginDescription;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;

public class SpigotPluginDescription implements IPluginDescription
{
    @Override
    public String getName()
    {
        return Bootstrap.getInstance().getDescription().getName();
    }

    @Override
    public String getVersion()
    {
        return Bootstrap.getInstance().getDescription().getVersion();
    }

    @Override
    public File getFile()
    {
        try
        {
            final Method getFile = JavaPlugin.class.getDeclaredMethod( "getFile" );
            getFile.setAccessible( true );
            return (File) getFile.invoke( Bootstrap.getInstance() );
        }
        catch ( NoSuchMethodException | IllegalAccessException | InvocationTargetException e )
        {
            BuX.getLogger().log( Level.WARNING, "Could not invoke JavaPlugin#getFile method.", e );
            return null;
        }
    }
}
