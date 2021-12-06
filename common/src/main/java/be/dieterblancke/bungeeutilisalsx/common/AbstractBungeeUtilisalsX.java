package be.dieterblancke.bungeeutilisalsx.common;

import be.dieterblancke.bungeeutilisalsx.common.announcers.actionbar.ActionBarAnnouncer;
import be.dieterblancke.bungeeutilisalsx.common.announcers.bossbar.BossBarAnnouncer;
import be.dieterblancke.bungeeutilisalsx.common.announcers.chat.ChatAnnouncer;
import be.dieterblancke.bungeeutilisalsx.common.announcers.tab.TabAnnouncer;
import be.dieterblancke.bungeeutilisalsx.common.announcers.title.TitleAnnouncer;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcer;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.IEventHandler;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.other.ProxyMotdPingEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.punishment.UserPunishmentFinishEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.staff.NetworkStaffJoinEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.staff.NetworkStaffLeaveEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.*;
import be.dieterblancke.bungeeutilisalsx.common.api.job.management.JobManager;
import be.dieterblancke.bungeeutilisalsx.common.api.language.Language;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.xml.XMLPlaceHolders;
import be.dieterblancke.bungeeutilisalsx.common.api.redis.RedisManager;
import be.dieterblancke.bungeeutilisalsx.common.api.scheduler.IScheduler;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.StorageType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.javascript.Script;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.chat.ChatProtections;
import be.dieterblancke.bungeeutilisalsx.common.commands.CommandManager;
import be.dieterblancke.bungeeutilisalsx.common.executors.*;
import be.dieterblancke.bungeeutilisalsx.common.job.MultiProxyJobManager;
import be.dieterblancke.bungeeutilisalsx.common.job.SingleProxyJobManager;
import be.dieterblancke.bungeeutilisalsx.common.migration.MigrationManager;
import be.dieterblancke.bungeeutilisalsx.common.migration.MigrationManagerFactory;
import be.dieterblancke.bungeeutilisalsx.common.permission.PermissionIntegration;
import be.dieterblancke.bungeeutilisalsx.common.permission.integrations.DefaultPermissionIntegration;
import be.dieterblancke.bungeeutilisalsx.common.permission.integrations.LuckPermsPermissionIntegration;
import be.dieterblancke.bungeeutilisalsx.common.placeholders.CenterPlaceHolder;
import be.dieterblancke.bungeeutilisalsx.common.placeholders.JavaScriptPlaceHolder;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.ProtocolizeManager;
import be.dieterblancke.bungeeutilisalsx.common.protocolize.SimpleProtocolizeManager;
import be.dieterblancke.bungeeutilisalsx.common.redis.RedisManagerFactory;
import be.dieterblancke.bungeeutilisalsx.common.scheduler.Scheduler;
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
    private PermissionIntegration activePermissionIntegration;
    private JobManager jobManager;
    private RedisManager redisManager;
    private ProtocolizeManager protocolizeManager;

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
        if ( !getDataFolder().exists() )
        {
            getDataFolder().mkdirs();
        }

        this.loadConfigs();
        ChatProtections.reloadAllProtections();

        this.loadPlaceHolders();
        this.loadScripts();
        this.loadDatabase();
        this.migrate();

        this.api = this.createBuXApi();

        final boolean useMultiProxy = ConfigFiles.CONFIG.getConfig().getBoolean( "multi-proxy.enabled" );
        this.redisManager = useMultiProxy ? RedisManagerFactory.create() : null;
        this.jobManager = useMultiProxy ? new MultiProxyJobManager() : new SingleProxyJobManager();

        this.detectPermissionIntegration();
        this.registerLanguages();
        this.registerListeners();
        this.registerExecutors();
        this.registerCommands();
        this.registerPluginSupports();
        this.registerProtocolizeSupport();

        Announcer.registerAnnouncers(
                ActionBarAnnouncer.class,
                ChatAnnouncer.class,
                TitleAnnouncer.class,
                BossBarAnnouncer.class,
                TabAnnouncer.class
        );

        this.setupTasks();
        this.registerMetrics();
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
        PlaceHolderAPI.addPlaceHolder( false, "javascript", new JavaScriptPlaceHolder() );
    }

    protected abstract void registerLanguages();

    protected abstract void registerListeners();

    protected void registerExecutors()
    {
        new UserExecutor().registerForEvents( UserLoadEvent.class, UserUnloadEvent.class, UserServerConnectedEvent.class );
        new StaffNetworkExecutor().registerForEvents( NetworkStaffJoinEvent.class, NetworkStaffLeaveEvent.class );
        new SpyEventExecutor().registerForEvents( UserPrivateMessageEvent.class, UserCommandEvent.class );

        this.api.getEventLoader().register( UserChatEvent.class, new UserChatExecutor() );
        this.api.getEventLoader().register( UserChatEvent.class, new StaffChatExecutor() );
        this.api.getEventLoader().register( UserPluginMessageReceiveEvent.class, new UserPluginMessageReceiveEventExecutor() );
        this.api.getEventLoader().register( UserServerConnectedEvent.class, new IngameMotdExecutor() );
        this.api.getEventLoader().register( UserCommandEvent.class, new UserCommandExecutor() );

        if ( ConfigFiles.MOTD.isEnabled() )
        {
            this.api.getEventLoader().register( ProxyMotdPingEvent.class, new ProxyMotdPingExecutor() );
        }

        if ( ConfigFiles.PUNISHMENT_CONFIG.isEnabled() )
        {
            new MuteCheckExecutor().registerForEvents( UserChatEvent.class, UserCommandEvent.class );

            this.api.getEventLoader().register( UserPunishmentFinishEvent.class, new UserPunishExecutor() );
        }

        if ( ConfigFiles.FRIENDS_CONFIG.isEnabled() )
        {
            new FriendsExecutor().registerForEvents( UserLoadEvent.class, UserUnloadEvent.class, UserServerConnectedEvent.class );
        }
    }

    protected void setupTasks()
    {
        // do nothing
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

        if ( isProtocolizeEnabled() )
        {
            protocolizeManager.getGuiManager().reload();
        }
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

    protected abstract void registerPluginSupports();

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
    }

    public boolean isRedisManagerEnabled()
    {
        return redisManager != null;
    }

    private void registerProtocolizeSupport()
    {
        if ( BuX.getInstance().proxyOperations().getPlugin( "Protocolize" ).isPresent() )
        {
            this.protocolizeManager = new SimpleProtocolizeManager();
        }
    }

    public boolean isProtocolizeEnabled()
    {
        return protocolizeManager != null;
    }

    protected abstract void registerMetrics();

    protected void migrate()
    {
        final MigrationManager migrationManager = MigrationManagerFactory.createMigrationManager();
        migrationManager.initialize();
        try
        {
            migrationManager.migrate();
        }
        catch ( Exception e )
        {
            BuX.getLogger().log( Level.SEVERE, "Could not execute migrations", e );
        }
    }
}
