package be.dieterblancke.bungeeutilisalsx.spigot;

import be.dieterblancke.bungeeutilisalsx.common.*;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserLoadEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserUnloadEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection.ReflectionUtils;
import be.dieterblancke.bungeeutilisalsx.common.event.EventLoader;
import be.dieterblancke.bungeeutilisalsx.common.executors.UserExecutor;
import be.dieterblancke.bungeeutilisalsx.common.language.PluginLanguageManager;
import be.dieterblancke.bungeeutilisalsx.common.manager.CommandManager;
import be.dieterblancke.bungeeutilisalsx.common.updater.Updatable;
import be.dieterblancke.bungeeutilisalsx.common.updater.Updater;
import be.dieterblancke.bungeeutilisalsx.spigot.api.user.UserServerHelper;
import be.dieterblancke.bungeeutilisalsx.spigot.gui.GuiManager;
import be.dieterblancke.bungeeutilisalsx.spigot.listeners.InventoryListener;
import be.dieterblancke.bungeeutilisalsx.spigot.listeners.UserChatListener;
import be.dieterblancke.bungeeutilisalsx.spigot.listeners.UserConnectionListener;
import be.dieterblancke.bungeeutilisalsx.spigot.listeners.pluginmessage.FriendPluginMessageListener;
import be.dieterblancke.bungeeutilisalsx.spigot.placeholders.DefaultPlaceHolders;
import be.dieterblancke.bungeeutilisalsx.spigot.user.DataStorageUserServerHelper;
import com.dbsoftwares.configuration.api.FileStorageType;
import lombok.Getter;
import lombok.Setter;
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

    @Getter
    @Setter
    private UserServerHelper userServerHelper;

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
        this.userServerHelper = new DataStorageUserServerHelper();

        this.registerLanguages();
        this.registerListeners();
        this.registerPluginMessageReceivers();
        this.registerExecutors();

        if ( ConfigFiles.CONFIG.getConfig().getBoolean( "updater.enabled" ) )
        {
            Updater.initialize( this );
        }
        this.setupTasks();

        this.guiManager = new GuiManager();
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
        return new BuXApi( new PluginLanguageManager(), new EventLoader() );
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

    private void registerPluginMessageReceivers()
    {
        Bukkit.getMessenger().registerOutgoingPluginChannel( Bootstrap.getInstance(), "bux:main" );
        Bukkit.getMessenger().registerIncomingPluginChannel( Bootstrap.getInstance(), "bux:main", new FriendPluginMessageListener() );
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
