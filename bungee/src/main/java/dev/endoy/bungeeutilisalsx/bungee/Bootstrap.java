package dev.endoy.bungeeutilisalsx.bungee;

import dev.endoy.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;
import dev.endoy.bungeeutilisalsx.common.BootstrapUtil;
import dev.endoy.bungeeutilisalsx.common.api.utils.Platform;
import dev.endoy.bungeeutilisalsx.common.api.utils.reflection.UrlLibraryClassLoader;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

public class Bootstrap extends Plugin
{

    @Getter
    private static Bootstrap instance;
    private AbstractBungeeUtilisalsX abstractBungeeUtilisalsX;

    @Override
    public void onEnable()
    {
        instance = this;

        Platform.setCurrentPlatform( Platform.BUNGEECORD );
        BootstrapUtil.loadLibraries( this.getDataFolder(), new UrlLibraryClassLoader(), getLogger() );

        abstractBungeeUtilisalsX = new BungeeUtilisalsX();
        abstractBungeeUtilisalsX.initialize();
    }

    @Override
    public void onDisable()
    {
        abstractBungeeUtilisalsX.shutdown();
    }
}
