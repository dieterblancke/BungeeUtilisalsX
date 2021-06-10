package be.dieterblancke.bungeeutilisalsx.velocity;

import be.dieterblancke.bungeeutilisalsx.common.IPluginDescription;
import com.velocitypowered.api.plugin.PluginContainer;
import com.velocitypowered.api.plugin.PluginDescription;

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;

public class VelocityPluginDescription implements IPluginDescription
{
    @Override
    public String getName()
    {
        return get( pluginDescription -> pluginDescription.getName().orElse( "" ), "" );
    }

    @Override
    public String getVersion()
    {
        return get( pluginDescription -> pluginDescription.getVersion().orElse( "" ), "" );
    }

    @Override
    public File getFile()
    {
        final Path path = get( pluginDescription -> pluginDescription.getSource().orElse( null ), null );

        if ( path != null )
        {
            return path.toFile();
        }
        else
        {
            return null;
        }
    }

    private <T> T get( final Function<PluginDescription, T> mapper, final T def )
    {
        return Bootstrap.getInstance().getProxyServer().getPluginManager()
                .getPlugin( "bungeeutilisalsx" )
                .map( PluginContainer::getDescription )
                .map( mapper )
                .orElse( def );
    }
}
