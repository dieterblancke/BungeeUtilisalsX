package dev.endoy.bungeeutilisalsx.velocity.library;

import dev.endoy.bungeeutilisalsx.common.api.utils.reflection.LibraryClassLoader;
import dev.endoy.bungeeutilisalsx.velocity.Bootstrap;

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
