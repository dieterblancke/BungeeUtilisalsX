package dev.endoy.bungeeutilisalsx.common;

import com.google.common.collect.Lists;
import dev.endoy.bungeeutilisalsx.common.announcers.actionbar.ActionBarAnnouncer;
import dev.endoy.bungeeutilisalsx.common.announcers.bossbar.BossBarAnnouncer;
import dev.endoy.bungeeutilisalsx.common.announcers.chat.ChatAnnouncer;
import dev.endoy.bungeeutilisalsx.common.announcers.tab.TabAnnouncer;
import dev.endoy.bungeeutilisalsx.common.announcers.title.TitleAnnouncer;
import dev.endoy.bungeeutilisalsx.common.api.announcer.Announcer;
import dev.endoy.bungeeutilisalsx.common.api.event.event.IEventHandler;
import dev.endoy.bungeeutilisalsx.common.api.event.events.other.ProxyMotdPingEvent;
import dev.endoy.bungeeutilisalsx.common.api.event.events.punishment.UserPunishmentFinishEvent;
import dev.endoy.bungeeutilisalsx.common.api.event.events.staff.NetworkStaffJoinEvent;
import dev.endoy.bungeeutilisalsx.common.api.event.events.staff.NetworkStaffLeaveEvent;
import dev.endoy.bungeeutilisalsx.common.api.event.events.user.*;
import dev.endoy.bungeeutilisalsx.common.api.job.management.JobManager;
import dev.endoy.bungeeutilisalsx.common.api.language.Language;
import dev.endoy.bungeeutilisalsx.common.api.party.PartyManager;
import dev.endoy.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import dev.endoy.bungeeutilisalsx.common.api.placeholder.xml.XMLPlaceHolders;
import dev.endoy.bungeeutilisalsx.common.api.pluginsupport.PluginSupport;
import dev.endoy.bungeeutilisalsx.common.api.redis.RedisManager;
import dev.endoy.bungeeutilisalsx.common.api.scheduler.IScheduler;
import dev.endoy.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import dev.endoy.bungeeutilisalsx.common.api.storage.StorageType;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.Platform;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.api.utils.javascript.Script;
import dev.endoy.bungeeutilisalsx.common.api.utils.other.StaffUser;
import dev.endoy.bungeeutilisalsx.common.chat.ChatProtections;
import dev.endoy.bungeeutilisalsx.common.commands.CommandManager;
import dev.endoy.bungeeutilisalsx.common.executors.*;
import dev.endoy.bungeeutilisalsx.common.job.MultiProxyJobManager;
import dev.endoy.bungeeutilisalsx.common.job.SingleProxyJobManager;
import dev.endoy.bungeeutilisalsx.common.migration.MigrationManager;
import dev.endoy.bungeeutilisalsx.common.migration.MigrationManagerFactory;
import dev.endoy.bungeeutilisalsx.common.party.SimplePartyManager;
import dev.endoy.bungeeutilisalsx.common.permission.PermissionIntegration;
import dev.endoy.bungeeutilisalsx.common.permission.integrations.DefaultPermissionIntegration;
import dev.endoy.bungeeutilisalsx.common.permission.integrations.LuckPermsPermissionIntegration;
import dev.endoy.bungeeutilisalsx.common.placeholders.*;
import dev.endoy.bungeeutilisalsx.common.pluginsupport.TritonPluginSupport;
import dev.endoy.bungeeutilisalsx.common.protocolize.ProtocolizeManager;
import dev.endoy.bungeeutilisalsx.common.protocolize.SimpleProtocolizeManager;
import dev.endoy.bungeeutilisalsx.common.redis.RedisManagerFactory;
import dev.endoy.bungeeutilisalsx.common.scheduler.Scheduler;
import dev.endoy.configuration.api.FileStorageType;
import dev.endoy.configuration.api.IConfiguration;
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
    private PartyManager partyManager;

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

        this.migrateConfigs();
        this.loadConfigs();
        ChatProtections.reloadAllProtections();

        this.loadPlaceHolders();
        this.loadScripts();
        this.loadDatabase();
        this.migrate();

        this.api = this.createBuXApi();

        final boolean useMultiProxy = ConfigFiles.CONFIG.getConfig().exists( "multi-proxy.enabled" ) && ConfigFiles.CONFIG.getConfig().getBoolean( "multi-proxy.enabled" );
        this.redisManager = useMultiProxy ? RedisManagerFactory.create() : null;
        this.jobManager = useMultiProxy ? new MultiProxyJobManager() : new SingleProxyJobManager();

        if ( ConfigFiles.PARTY_CONFIG.isEnabled() )
        {
            this.partyManager = new SimplePartyManager();
        }

        this.detectPermissionIntegration();
        this.registerProtocolizeSupport();
        this.registerLanguages();
        this.registerListeners();
        this.registerExecutors();
        this.registerCommands();
        this.registerPluginSupports();

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

        PlaceHolderAPI.loadPlaceHolderPack( new DefaultPlaceHolders() );
        PlaceHolderAPI.loadPlaceHolderPack( new InputPlaceHolders() );
        PlaceHolderAPI.loadPlaceHolderPack( new UserPlaceHolderPack() );
        PlaceHolderAPI.loadPlaceHolderPack( new PermissionPlaceHolderPack() );
    }

    protected void registerLanguages()
    {
        this.getApi().getLanguageManager().addPlugin( this.getName(), new File( getDataFolder(), "languages" ), FileStorageType.YAML );
        this.getApi().getLanguageManager().loadLanguages( this.getClass(), this.getName() );
    }

    protected abstract void registerListeners();

    @SuppressWarnings( "unchecked" )
    protected void registerExecutors()
    {
        this.api.getEventLoader().register( new UserExecutor(), UserLoadEvent.class, UserUnloadEvent.class, UserServerConnectedEvent.class );
        this.api.getEventLoader().register( new StaffNetworkExecutor(), NetworkStaffJoinEvent.class, NetworkStaffLeaveEvent.class );
        this.api.getEventLoader().register( new SpyEventExecutor(), UserPrivateMessageEvent.class, UserCommandEvent.class );
        this.api.getEventLoader().register( new UserChatExecutor(), UserChatEvent.class );
        this.api.getEventLoader().register( new StaffChatExecutor(), UserChatEvent.class );
        this.api.getEventLoader().register( new UserPluginMessageReceiveEventExecutor(), UserPluginMessageReceiveEvent.class );
        this.api.getEventLoader().register( new IngameMotdExecutor(), UserServerConnectedEvent.class );
        this.api.getEventLoader().register( new UserCommandExecutor(), UserCommandEvent.class );

        if ( ConfigFiles.CHAT_SYNC_CONFIG.isEnabled() )
        {
            this.api.getEventLoader().register( new ChatSyncExecutor(), UserChatEvent.class );
        }

        if ( ConfigFiles.MOTD.isEnabled() )
        {
            this.api.getEventLoader().register( new ProxyMotdPingExecutor(), ProxyMotdPingEvent.class );
        }

        if ( ConfigFiles.PUNISHMENT_CONFIG.isEnabled() )
        {
            this.api.getEventLoader().register( new MuteCheckExecutor(), UserChatEvent.class, UserCommandEvent.class );
            this.api.getEventLoader().register( new UserPunishExecutor(), UserPunishmentFinishEvent.class );
        }

        if ( ConfigFiles.FRIENDS_CONFIG.isEnabled() )
        {
            this.api.getEventLoader().register( new FriendsExecutor(), UserLoadEvent.class, UserUnloadEvent.class, UserServerConnectedEvent.class );
        }

        if ( ConfigFiles.SERVER_BALANCER_CONFIG.isEnabled() )
        {
            this.api.getEventLoader().register( new ServerBalancerExecutors( api.getServerBalancer() ), UserServerConnectEvent.class, UserServerKickEvent.class );
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

        if ( this.api.getServerBalancer() != null )
        {
            this.api.getServerBalancer().reload();
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
                    this.getClass().getResourceAsStream( "/configurations/scripts/hello.js" ),
                    new File( scriptsFolder, "hello.js" )
            );
            IConfiguration.createDefaultFile(
                    this.getClass().getResourceAsStream( "/configurations/scripts/coins.js" ),
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

    protected void registerPluginSupports()
    {
        PluginSupport.registerPluginSupport( TritonPluginSupport.class );
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

    public abstract ServerOperationsApi serverOperations();

    public abstract File getDataFolder();

    public abstract String getVersion();

    public abstract List<StaffUser> getStaffMembers();

    public abstract IPluginDescription getDescription();

    public abstract Logger getLogger();

    public abstract Platform getPlatform();

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
        if ( BuX.getInstance().serverOperations().getPlugin( "Protocolize" ).isPresent() )
        {
            this.protocolizeManager = new SimpleProtocolizeManager();
        }
    }

    public boolean isProtocolizeEnabled()
    {
        return protocolizeManager != null;
    }

    public boolean isPartyManagerEnabled()
    {
        return partyManager != null;
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

    protected void migrateConfigs()
    {
        final MigrationManager migrationManager = MigrationManagerFactory.createConfigMigrationManager();
        migrationManager.initialize();
        try
        {
            migrationManager.migrate();
        }
        catch ( Exception e )
        {
            BuX.getLogger().log( Level.SEVERE, "Could not execute config migrations", e );
        }
    }
}
