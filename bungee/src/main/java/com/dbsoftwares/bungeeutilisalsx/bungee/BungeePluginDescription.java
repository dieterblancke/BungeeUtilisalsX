package com.dbsoftwares.bungeeutilisalsx.bungee;

import com.dbsoftwares.bungeeutilisalsx.common.IPluginDescription;

import java.io.File;

public class BungeePluginDescription implements IPluginDescription
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
        return Bootstrap.getInstance().getDescription().getFile();
    }
}
