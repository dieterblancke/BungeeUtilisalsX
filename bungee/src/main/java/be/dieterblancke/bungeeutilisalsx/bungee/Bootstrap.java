package be.dieterblancke.bungeeutilisalsx.bungee;

import be.dieterblancke.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.common.BootstrapUtil;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Platform;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection.UrlLibraryClassLoader;
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
