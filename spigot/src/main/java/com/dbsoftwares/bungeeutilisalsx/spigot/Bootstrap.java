package com.dbsoftwares.bungeeutilisalsx.spigot;

import com.dbsoftwares.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Bootstrap extends JavaPlugin
{

    @Getter
    private static Bootstrap instance;
    private AbstractBungeeUtilisalsX abstractBungeeUtilisalsX;

    @Override
    public void onEnable()
    {
        instance = this;

        abstractBungeeUtilisalsX = new BungeeUtilisalsX();
        abstractBungeeUtilisalsX.initialize();
    }

    @Override
    public void onDisable()
    {
        abstractBungeeUtilisalsX.shutdown();
    }
}
