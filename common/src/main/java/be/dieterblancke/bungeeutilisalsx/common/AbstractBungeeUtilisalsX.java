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
import be.dieterblancke.bungeeutilisalsx.common.api.scheduler.IScheduler;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.StorageType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.javascript.Script;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection.JarClassLoader;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection.ReflectionUtils;
import be.dieterblancke.bungeeutilisalsx.common.executors.*;
import be.dieterblancke.bungeeutilisalsx.common.library.Library;
import be.dieterblancke.bungeeutilisalsx.common.library.StandardLibrary;
import be.dieterblancke.bungeeutilisalsx.common.manager.CommandManager;
import be.dieterblancke.bungeeutilisalsx.common.migration.MigrationManager;
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
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

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

        this.loadLibraries();
        this.loadPlaceHolders();
        this.loadScripts();
        this.loadDatabase();

        final MigrationManager migrationManager = new MigrationManager();
        migrationManager.initialize();
        try
        {
            migrationManager.migrate();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "Could not execute migrations", e );
        }

        this.api = this.createBuXApi();

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

    protected void loadConfigs()
    {
        ConfigFiles.loadAllConfigs();
    }

    protected abstract IBuXApi createBuXApi();

    protected abstract CommandManager getCommandManager();

    protected abstract void loadPlaceHolders();

    protected abstract void registerLanguages();

    protected abstract void registerListeners();

    protected void registerExecutors()
    {
        final UserExecutor userExecutor = new UserExecutor();
        this.getApi().getEventLoader().register( UserLoadEvent.class, userExecutor );
        this.getApi().getEventLoader().register( UserUnloadEvent.class, userExecutor );

        this.getApi().getEventLoader().register( UserChatEvent.class, new UserChatExecutor() );
        this.getApi().getEventLoader().register( UserChatEvent.class, new StaffChatExecutor() );

        final StaffNetworkExecutor staffNetworkExecutor = new StaffNetworkExecutor();
        this.getApi().getEventLoader().register( NetworkStaffJoinEvent.class, staffNetworkExecutor );
        this.getApi().getEventLoader().register( NetworkStaffLeaveEvent.class, staffNetworkExecutor );

        final SpyEventExecutor spyEventExecutor = new SpyEventExecutor();
        this.getApi().getEventLoader().register( UserPrivateMessageEvent.class, spyEventExecutor );
        this.getApi().getEventLoader().register( UserCommandEvent.class, spyEventExecutor );

        if ( ConfigFiles.PUNISHMENTS.isEnabled() )
        {
            this.getApi().getEventLoader().register( UserPunishmentFinishEvent.class, new UserPunishExecutor() );

            final MuteCheckExecutor muteCheckExecutor = new MuteCheckExecutor();
            this.getApi().getEventLoader().register( UserChatEvent.class, muteCheckExecutor );
            this.getApi().getEventLoader().register( UserCommandEvent.class, muteCheckExecutor );
        }

        if ( ConfigFiles.FRIENDS_CONFIG.isEnabled() )
        {
            final FriendsExecutor friendsExecutor = new FriendsExecutor();

            this.getApi().getEventLoader().register( UserLoadEvent.class, friendsExecutor );
            this.getApi().getEventLoader().register( UserUnloadEvent.class, friendsExecutor );
            this.getApi().getEventLoader().register( UserServerConnectedEvent.class, friendsExecutor );
        }
    }

    protected void setupTasks()
    {
        this.scheduler.runTaskDelayed( 1, TimeUnit.MINUTES, new UserMessageQueueTask() );
    }

    public void reload()
    {
        ConfigFiles.reloadAllConfigs();

        for ( Language language : this.getApi().getLanguageManager().getLanguages() )
        {
            this.getApi().getLanguageManager().reloadConfig( this.getName(), language );
        }

        if ( this.getApi().getHubBalancer() != null )
        {
            this.getApi().getHubBalancer().reload();
        }

        this.getCommandManager().load();
        Announcer.getAnnouncers().values().forEach( Announcer::reload );

        loadScripts();
        api.getChatManager().reload();
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
            abstractStorageManager = type.getManager().newInstance();
        }
        catch ( Exception e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    public abstract ProxyOperationsApi proxyOperations();

    public abstract File getDataFolder();

    public abstract String getVersion();

    public abstract List<StaffUser> getStaffMembers();

    public abstract IPluginDescription getDescription();

    public abstract Logger getLogger();

    public void shutdown()
    {
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
