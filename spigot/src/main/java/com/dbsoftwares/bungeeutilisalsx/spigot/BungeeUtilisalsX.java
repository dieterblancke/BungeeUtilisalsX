package com.dbsoftwares.bungeeutilisalsx.spigot;

import com.dbsoftwares.bungeeutilisalsx.common.*;
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.IBridgeManager;
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.event.BridgeResponseEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.user.UserLoadEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.user.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.other.StaffUser;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.reflection.ReflectionUtils;
import com.dbsoftwares.bungeeutilisalsx.common.bridge.BridgeManager;
import com.dbsoftwares.bungeeutilisalsx.common.bridge.handlers.BungeeBridgeResponseHandler;
import com.dbsoftwares.bungeeutilisalsx.common.event.EventLoader;
import com.dbsoftwares.bungeeutilisalsx.common.executors.UserExecutor;
import com.dbsoftwares.bungeeutilisalsx.common.language.PluginLanguageManager;
import com.dbsoftwares.bungeeutilisalsx.common.manager.CommandManager;
import com.dbsoftwares.bungeeutilisalsx.common.updater.Updatable;
import com.dbsoftwares.bungeeutilisalsx.common.updater.Updater;
import com.dbsoftwares.bungeeutilisalsx.spigot.gui.GuiManager;
import com.dbsoftwares.bungeeutilisalsx.spigot.listeners.InventoryListener;
import com.dbsoftwares.bungeeutilisalsx.spigot.listeners.UserChatListener;
import com.dbsoftwares.bungeeutilisalsx.spigot.listeners.UserConnectionListener;
import com.dbsoftwares.bungeeutilisalsx.spigot.placeholders.DefaultPlaceHolders;
import com.dbsoftwares.configuration.api.FileStorageType;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Updatable( url = "https://api.dbsoftwares.eu/plugin/bungeeutilisalsx-spigot/" )
public class BungeeUtilisalsX extends AbstractBungeeUtilisalsX
{

    private final IPluginDescription pluginDescription = new SpigotPluginDescription();
    @Getter
    private GuiManager guiManager;

    @Override
    public void initialize()
    {
        if ( ReflectionUtils.getJavaVersion() < 8 )
        {
            BuX.getLogger().warning( "You are running a Java version lower then Java 8." );
            BuX.getLogger().warning( "Please upgrade to Java 8 or newer." );
            BuX.getLogger().warning( "BungeeUtilisalsX is not able to start up on Java versions lower then Java 8." );
            return;
        }

        if ( !getDataFolder().exists() )
        {
            getDataFolder().mkdirs();
        }

        this.loadConfigs();
        this.loadLibraries();
        this.loadPlaceHolders();
        this.loadDatabase();

        this.api = this.createBuXApi();

        this.registerLanguages();
        this.registerListeners();
        this.registerExecutors();

        if ( ConfigFiles.CONFIG.getConfig().getBoolean( "updater.enabled" ) )
        {
            Updater.initialize( this );
        }
        this.setupTasks();

        this.guiManager = new GuiManager();
        this.getApi().getEventLoader().register( BridgeResponseEvent.class, new BungeeBridgeResponseHandler() );
    }

    @Override
    protected void registerExecutors()
    {
        final UserExecutor userExecutor = new UserExecutor();
        this.getApi().getEventLoader().register( UserLoadEvent.class, userExecutor );
        this.getApi().getEventLoader().register( UserUnloadEvent.class, userExecutor );
    }

    @Override
    protected void loadConfigs()
    {
        ConfigFiles.CONFIG.load();
        ConfigFiles.LANGUAGES_CONFIG.load();
        ConfigFiles.FRIENDS_CONFIG.load();
    }

    @Override
    public void reload()
    {
        super.reload();
        this.guiManager.reload();
    }

    @Override
    protected IBuXApi createBuXApi()
    {
        final IBridgeManager bridgeManager = new BridgeManager();
        final IBuXApi api = new BuXApi(
                bridgeManager,
                new PluginLanguageManager(),
                new EventLoader()
        );
        bridgeManager.setup( api );

        return api;
    }

    @Override
    protected CommandManager getCommandManager()
    {
        throw new UnsupportedOperationException( "The CommandManager is currently only supported in the proxy versions of BungeeUtilisalsX!" );
    }

    @Override
    protected void loadPlaceHolders()
    {
        PlaceHolderAPI.loadPlaceHolderPack( new DefaultPlaceHolders() );
    }

    @Override
    protected void registerLanguages()
    {
        this.getApi().getLanguageManager().addPlugin( this.getName(), new File( getDataFolder(), "languages" ), FileStorageType.YAML );
        this.getApi().getLanguageManager().loadLanguages( this.getClass(), this.getName() );
    }

    @Override
    protected void registerListeners()
    {
        Bukkit.getPluginManager().registerEvents( new UserChatListener(), Bootstrap.getInstance() );
        Bukkit.getPluginManager().registerEvents( new UserConnectionListener(), Bootstrap.getInstance() );
        Bukkit.getPluginManager().registerEvents( new InventoryListener(), Bootstrap.getInstance() );
    }

    @Override
    public ProxyOperationsApi proxyOperations()
    {
        throw new UnsupportedOperationException( "These operations are only supported in the proxy versions of BungeeUtilisalsX!" );
    }

    @Override
    public File getDataFolder()
    {
        return Bootstrap.getInstance().getDataFolder();
    }

    @Override
    public String getVersion()
    {
        return Bootstrap.getInstance().getDescription().getVersion();
    }

    @Override
    public List<StaffUser> getStaffMembers()
    {
        return new ArrayList<>();
    }

    @Override
    public IPluginDescription getDescription()
    {
        return pluginDescription;
    }

    @Override
    public Logger getLogger()
    {
        return Bootstrap.getInstance().getLogger();
    }
}
