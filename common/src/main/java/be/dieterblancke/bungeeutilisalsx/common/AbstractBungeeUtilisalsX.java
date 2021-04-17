package be.dieterblancke.bungeeutilisalsx.common;

import be.dieterblancke.bungeeutilisalsx.common.announcers.actionbar.ActionBarAnnouncer;
import be.dieterblancke.bungeeutilisalsx.common.announcers.bossbar.BossBarAnnouncer;
import be.dieterblancke.bungeeutilisalsx.common.announcers.chat.ChatAnnouncer;
import be.dieterblancke.bungeeutilisalsx.common.announcers.tab.TabAnnouncer;
import be.dieterblancke.bungeeutilisalsx.common.announcers.title.TitleAnnouncer;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcer;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.IEventHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.network.NetworkStaffJoinEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.network.NetworkStaffLeaveEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.punishment.UserPunishmentFinishEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.*;
import be.dieterblancke.bungeeutilisalsx.common.api.language.Language;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.xml.XMLPlaceHolders;
import be.dieterblancke.bungeeutilisalsx.common.api.scheduler.IScheduler;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.StorageType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.javascript.Script;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection.JarClassLoader;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection.ReflectionUtils;
import be.dieterblancke.bungeeutilisalsx.common.chat.ChatProtections;
import be.dieterblancke.bungeeutilisalsx.common.commands.CommandManager;
import be.dieterblancke.bungeeutilisalsx.common.executors.*;
import be.dieterblancke.bungeeutilisalsx.common.library.Library;
import be.dieterblancke.bungeeutilisalsx.common.library.StandardLibrary;
import be.dieterblancke.bungeeutilisalsx.common.migration.MigrationManager;
import be.dieterblancke.bungeeutilisalsx.common.permission.PermissionIntegration;
import be.dieterblancke.bungeeutilisalsx.common.permission.integrations.DefaultPermissionIntegration;
import be.dieterblancke.bungeeutilisalsx.common.permission.integrations.LuckPermsPermissionIntegration;
import be.dieterblancke.bungeeutilisalsx.common.placeholders.CenterPlaceHolder;
import be.dieterblancke.bungeeutilisalsx.common.scheduler.Scheduler;
import be.dieterblancke.bungeeutilisalsx.common.tasks.UserMessageQueueTask;
import be.dieterblancke.bungeeutilisalsx.common.updater.Updater;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.Lists;
import lombok.Data;

import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Data
public abstract class AbstractBungeeUtilisalsX
{

    private static AbstractBungeeUtilisalsX INSTANCE;
    private final IScheduler scheduler = new Scheduler();
    private final String name = "BungeeUtilisalsX";
    private final List<Script> scripts = new ArrayList<>();
    protected IBuXApi api;
    private AbstractStorageManager abstractStorageManager;
    private JarClassLoader jarClassLoader;
    private PermissionIntegration activePermissionIntegration;

    public AbstractBungeeUtilisalsX()
    {
        INSTANCE = this;
    }

    public static AbstractBungeeUtilisalsX getInstance()
    {
        return INSTANCE;
    }

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
        ChatProtections.reloadAllProtections();

        this.loadLibraries();
        this.loadPlaceHolders();
        this.loadScripts();
        this.loadDatabase();

        final MigrationManager migrationManager = new MigrationManager();
        if ( migrationManager.canMigrate() )
        {
            migrationManager.initialize();
            try
            {
                migrationManager.migrate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "Could not execute migrations", e );
            }
        }

        this.api = this.createBuXApi();

        this.detectPermissionIntegration();
        this.registerLanguages();
        this.registerListeners();
        this.registerExecutors();
        this.registerCommands();

        Announcer.registerAnnouncers(
                ActionBarAnnouncer.class,
                ChatAnnouncer.class,
                TitleAnnouncer.class,
                BossBarAnnouncer.class,
                TabAnnouncer.class
        );

        if ( ConfigFiles.CONFIG.getConfig().getBoolean( "updater.enabled" ) )
        {
            Updater.initialize( this );
        }
        this.setupTasks();
    }

    public PermissionIntegration getActivePermissionIntegration()
    {
        return activePermissionIntegration;
    }

    protected void loadConfigs()
    {
        ConfigFiles.loadAllConfigs();
    }

    protected abstract IBuXApi createBuXApi();

    public abstract CommandManager getCommandManager();

    protected void loadPlaceHolders()
    {
        final XMLPlaceHolders xmlPlaceHolders = new XMLPlaceHolders();

        xmlPlaceHolders.addXmlPlaceHolder( new CenterPlaceHolder() );

        PlaceHolderAPI.addPlaceHolder( xmlPlaceHolders );
    }

    protected abstract void registerLanguages();

    protected abstract void registerListeners();

    protected void registerExecutors()
    {
        final UserExecutor userExecutor = new UserExecutor();
        this.api.getEventLoader().register( UserLoadEvent.class, userExecutor );
        this.api.getEventLoader().register( UserUnloadEvent.class, userExecutor );
        this.api.getEventLoader().register( UserServerConnectedEvent.class, userExecutor );

        this.api.getEventLoader().register( UserChatEvent.class, new UserChatExecutor() );
        this.api.getEventLoader().register( UserChatEvent.class, new StaffChatExecutor() );

        final StaffNetworkExecutor staffNetworkExecutor = new StaffNetworkExecutor();
        this.api.getEventLoader().register( NetworkStaffJoinEvent.class, staffNetworkExecutor );
        this.api.getEventLoader().register( NetworkStaffLeaveEvent.class, staffNetworkExecutor );

        final SpyEventExecutor spyEventExecutor = new SpyEventExecutor();
        this.api.getEventLoader().register( UserPrivateMessageEvent.class, spyEventExecutor );
        this.api.getEventLoader().register( UserCommandEvent.class, spyEventExecutor );

        this.api.getEventLoader().register( UserPluginMessageReceiveEvent.class, new UserPluginMessageReceiveEventExecutor() );

        if ( ConfigFiles.PUNISHMENT_CONFIG.isEnabled() )
        {
            this.api.getEventLoader().register( UserPunishmentFinishEvent.class, new UserPunishExecutor() );

            final MuteCheckExecutor muteCheckExecutor = new MuteCheckExecutor();
            this.api.getEventLoader().register( UserChatEvent.class, muteCheckExecutor );
            this.api.getEventLoader().register( UserCommandEvent.class, muteCheckExecutor );
        }

        if ( ConfigFiles.FRIENDS_CONFIG.isEnabled() )
        {
            final FriendsExecutor friendsExecutor = new FriendsExecutor();

            this.api.getEventLoader().register( UserLoadEvent.class, friendsExecutor );
            this.api.getEventLoader().register( UserUnloadEvent.class, friendsExecutor );
            this.api.getEventLoader().register( UserServerConnectedEvent.class, friendsExecutor );
        }
    }

    protected void setupTasks()
    {
        this.scheduler.runTaskDelayed( 1, TimeUnit.MINUTES, new UserMessageQueueTask() );
    }

    public void reload()
    {
        ConfigFiles.reloadAllConfigs();

        for ( Language language : this.api.getLanguageManager().getLanguages() )
        {
            this.api.getLanguageManager().reloadConfig( this.getName(), language );
        }

        if ( this.api.getHubBalancer() != null )
        {
            this.api.getHubBalancer().reload();
        }

        this.getCommandManager().load();
        Announcer.getAnnouncers().values().forEach( Announcer::reload );

        loadScripts();
        ChatProtections.reloadAllProtections();
    }

    private void loadScripts()
    {
        scripts.forEach( Script::unload );
        scripts.clear();
        if ( !ConfigFiles.CONFIG.getConfig().getBoolean( "scripting" ) )
        {
            return;
        }
        final File scriptsFolder = new File( getDataFolder(), "scripts" );

        if ( !scriptsFolder.exists() )
        {
            scriptsFolder.mkdir();

            IConfiguration.createDefaultFile(
                    this.getClass().getResourceAsStream( "/scripts/hello.js" ),
                    new File( scriptsFolder, "hello.js" )
            );
            IConfiguration.createDefaultFile(
                    this.getClass().getResourceAsStream( "/scripts/coins.js" ),
                    new File( scriptsFolder, "coins.js" )
            );
        }

        for ( final File file : scriptsFolder.listFiles() )
        {
            if ( file.isDirectory() )
            {
                continue;
            }
            try
            {
                final String code = new String( Files.readAllBytes( file.toPath() ) );
                final Script script = new Script( file.getName(), code );

                this.scripts.add( script );
            }
            catch ( IOException | ScriptException e )
            {
                BuX.getLogger().log( Level.SEVERE, "Could not load script " + file.getName(), e );
            }
        }
    }

    protected void registerCommands()
    {
        this.getCommandManager().load();
    }

    protected void loadLibraries()
    {
        BuX.getLogger().info( "Loading libraries ..." );
        jarClassLoader = new JarClassLoader();

        for ( StandardLibrary standardLibrary : StandardLibrary.values() )
        {
            final Library library = standardLibrary.getLibrary();

            if ( library.isToLoad() && !library.isPresent() )
            {
                library.load();
            }
        }
        BuX.getLogger().info( "Libraries have been loaded." );
    }

    protected void loadDatabase()
    {
        StorageType type;
        try
        {
            type = StorageType.valueOf( ConfigFiles.CONFIG.getConfig().getString( "storage.type" ).toUpperCase() );
        }
        catch ( IllegalArgumentException e )
        {
            type = StorageType.MYSQL;
        }
        try
        {
            this.abstractStorageManager = type.getStorageManagerSupplier().get();
        }
        catch ( Exception e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured while initializing the storage manager: ", e );
        }
    }

    protected void detectPermissionIntegration()
    {
        final List<PermissionIntegration> integrations = Lists.newArrayList(
                new LuckPermsPermissionIntegration()
        );

        for ( PermissionIntegration integration : integrations )
        {
            if ( integration.isActive() )
            {
                activePermissionIntegration = integration;
                return;
            }
        }
        activePermissionIntegration = new DefaultPermissionIntegration();
    }

    public abstract ProxyOperationsApi proxyOperations();

    public abstract File getDataFolder();

    public abstract String getVersion();

    public abstract List<StaffUser> getStaffMembers();

    public abstract IPluginDescription getDescription();

    public abstract Logger getLogger();

    public void shutdown()
    {
        if ( !Utils.isSpigot() )
        {
            BuX.getApi().getStorageManager().getDao().getUserDao().setCurrentServerBulk(
                    this.api.getUsers().stream().map( User::getUuid ).collect( Collectors.toList() ),
                    null
            );
        }

        Lists.newArrayList( this.api.getUsers() ).forEach( User::unload );
        try
        {
            abstractStorageManager.close();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        scripts.forEach( Script::unload );
        api.getEventLoader().getHandlers().forEach( IEventHandler::unregister );
        Updater.shutdownUpdaters();
    }
}
