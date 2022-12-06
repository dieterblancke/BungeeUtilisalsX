package be.dieterblancke.bungeeutilisalsx.spigot;

import be.dieterblancke.bungeeutilisalsx.common.IPluginDescription;

import java.io.File;

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
        throw new UnsupportedOperationException();
    }
}
