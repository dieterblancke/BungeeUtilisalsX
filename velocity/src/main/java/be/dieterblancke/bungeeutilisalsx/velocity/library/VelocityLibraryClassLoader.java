package be.dieterblancke.bungeeutilisalsx.velocity.library;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection.LibraryClassLoader;
import be.dieterblancke.bungeeutilisalsx.velocity.Bootstrap;

import java.io.File;

public class VelocityLibraryClassLoader implements LibraryClassLoader
{

    @Override
    public void loadJar( final File file )
    {
        Bootstrap.getInstance().getProxyServer().getPluginManager().addToClasspath(
                Bootstrap.getInstance(),
                file.toPath()
        );
    }
}
