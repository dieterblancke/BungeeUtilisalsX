package be.dieterblancke.bungeeutilisalsx.bungee;

import be.dieterblancke.bungeeutilisalsx.bungee.listeners.*;
import be.dieterblancke.bungeeutilisalsx.bungee.pluginsupports.PremiumVanishPluginSupport;
import be.dieterblancke.bungeeutilisalsx.bungee.pluginsupports.TritonBungeePluginSupport;
import be.dieterblancke.bungeeutilisalsx.bungee.utils.player.BungeePlayerUtils;
import be.dieterblancke.bungeeutilisalsx.bungee.utils.player.RedisPlayerUtils;
import be.dieterblancke.bungeeutilisalsx.common.*;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.AnnouncementType;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcer;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerConnectEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerKickEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.pluginsupport.PluginSupport;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Platform;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.commands.CommandManager;
import be.dieterblancke.bungeeutilisalsx.common.event.EventLoader;
import be.dieterblancke.bungeeutilisalsx.common.executors.ServerBalancerExecutors;
import be.dieterblancke.bungeeutilisalsx.common.language.PluginLanguageManager;
import be.dieterblancke.bungeeutilisalsx.common.punishment.PunishmentHelper;
import be.dieterblancke.bungeeutilisalsx.common.serverbalancer.SimpleServerBalancer;
import net.kyori.adventure.platform.bungeecord.BungeeAudiences;
import net.md_5.bungee.api.ProxyServer;
import org.bstats.bungeecord.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.SimplePie;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BungeeUtilisalsX extends AbstractBungeeUtilisalsX
{

    private final ServerOperationsApi serverOperationsApi = new BungeeOperationsApi();
    private final CommandManager commandManager = new CommandManager();
    private final IPluginDescription pluginDescription = new BungeePluginDescription();
    private final List<StaffUser> staffMembers = new ArrayList<>();
    private final BungeeAudiences bungeeAudiences;

    public BungeeUtilisalsX()
    {
        this.bungeeAudiences = BungeeAudiences.create( Bootstrap.getInstance() );
    }

    public static BungeeUtilisalsX getInstance()
    {
        return (BungeeUtilisalsX) AbstractBungeeUtilisalsX.getInstance();
    }

    @Override
    protected IBuXApi createBuXApi()
    {
        SimpleServerBalancer simpleServerBalancer = null;

        if ( ConfigFiles.SERVER_BALANCER_CONFIG.isEnabled() )
        {
            simpleServerBalancer = new SimpleServerBalancer();
            simpleServerBalancer.setup();
        }

        return new BuXApi(
                new PluginLanguageManager(),
                new EventLoader(),
                new PunishmentHelper(),
                ConfigFiles.CONFIG.getConfig().getBoolean( "multi-proxy.enabled" )
                        ? new RedisPlayerUtils()
                        : new BungeePlayerUtils(),
                simpleServerBalancer
        );
    }

    @Override
    public CommandManager getCommandManager()
    {
        return commandManager;
    }

    @Override
    protected void registerListeners()
    {
        ProxyServer.getInstance().getPluginManager().registerListener( Bootstrap.getInstance(), new UserChatListener() );
        ProxyServer.getInstance().getPluginManager().registerListener( Bootstrap.getInstance(), new UserConnectionListener() );
        ProxyServer.getInstance().getPluginManager().registerListener( Bootstrap.getInstance(), new PluginMessageListener() );

        if ( ConfigFiles.PUNISHMENT_CONFIG.isEnabled() )
        {
            ProxyServer.getInstance().getPluginManager().registerListener( Bootstrap.getInstance(), new PunishmentListener() );
        }

        if ( ConfigFiles.MOTD.isEnabled() )
        {
            ProxyServer.getInstance().getPluginManager().registerListener( Bootstrap.getInstance(), new MotdPingListener() );
        }

        if ( ConfigFiles.SERVER_BALANCER_CONFIG.isEnabled() )
        {
            BuX.getApi().getEventLoader().register( new ServerBalancerExecutors( api.getServerBalancer() ), UserServerConnectEvent.class, UserServerKickEvent.class );
        }
    }

    @Override
    protected void registerPluginSupports()
    {
        super.registerPluginSupports();

        PluginSupport.registerPluginSupport( PremiumVanishPluginSupport.class );
        PluginSupport.registerPluginSupport( TritonBungeePluginSupport.class );
    }

    @Override
    public ServerOperationsApi serverOperations()
    {
        return serverOperationsApi;
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
        return staffMembers;
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

    @Override
    public Platform getPlatform()
    {
        return Platform.BUNGEECORD;
    }

    public BungeeAudiences getBungeeAudiences()
    {
        return bungeeAudiences;
    }

    @Override
    protected void registerMetrics()
    {
        final Metrics metrics = new Metrics( Bootstrap.getInstance(), 5134 );

        metrics.addCustomChart( new SimplePie(
                "configurations/punishments",
                () -> ConfigFiles.PUNISHMENT_CONFIG.isEnabled() ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new SimplePie(
                "motds",
                () -> ConfigFiles.MOTD.isEnabled() ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new SimplePie(
                "ingame_motds",
                () -> ConfigFiles.MOTD.isEnabled() ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new SimplePie(
                "friends",
                () -> ConfigFiles.FRIENDS_CONFIG.isEnabled() ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new SimplePie(
                "actionbar_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.ACTIONBAR ) ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new SimplePie(
                "title_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.TITLE ) ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new SimplePie(
                "bossbar_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.BOSSBAR ) ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new SimplePie(
                "chat_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.CHAT ) ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new SimplePie(
                "tab_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.TAB ) ? "enabled" : "disabled"
        ) );
// TODO: add chart "serverbalancer"
//        metrics.addCustomChart( new SimplePie(
//                "hubbalancer",
//                () -> this.getApi().getHubBalancer() != null ? "enabled" : "disabled"
//        ) );
        metrics.addCustomChart( new SimplePie(
                "protocolize",
                () -> this.isProtocolizeEnabled() ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new AdvancedPie(
                "player_versions",
                () -> BuX.getApi().getUsers()
                        .stream()
                        .map( u -> u.getVersion().toString() )
                        .collect( Collectors.groupingBy( Function.identity(), Collectors.summingInt( it -> 1 ) ) )
        ) );
    }
}
