package be.dieterblancke.bungeeutilisalsx.spigot;

import be.dieterblancke.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.common.BootstrapUtil;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Platform;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection.UrlLibraryClassLoader;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public class Bootstrap extends JavaPlugin {

    @Getter
    private static Bootstrap instance;
    private AbstractBungeeUtilisalsX abstractBungeeUtilisalsX;

    @Override
    public void onEnable()
    {
        instance = this;

        Platform.setCurrentPlatform( Platform.SPIGOT );
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