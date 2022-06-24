package be.dieterblancke.bungeeutilisalsx.bungee;

import be.dieterblancke.bungeeutilisalsx.bungee.command.BungeeCommandManager;
import be.dieterblancke.bungeeutilisalsx.bungee.hubbalancer.HubBalancer;
import be.dieterblancke.bungeeutilisalsx.bungee.listeners.*;
import be.dieterblancke.bungeeutilisalsx.bungee.pluginsupports.PremiumVanishPluginSupport;
import be.dieterblancke.bungeeutilisalsx.bungee.pluginsupports.TritonPluginSupport;
import be.dieterblancke.bungeeutilisalsx.bungee.utils.player.BungeePlayerUtils;
import be.dieterblancke.bungeeutilisalsx.bungee.utils.player.RedisPlayerUtils;
import be.dieterblancke.bungeeutilisalsx.common.*;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.AnnouncementType;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcer;
import be.dieterblancke.bungeeutilisalsx.common.api.pluginsupport.PluginSupport;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.commands.CommandManager;
import be.dieterblancke.bungeeutilisalsx.common.event.EventLoader;
import be.dieterblancke.bungeeutilisalsx.common.language.PluginLanguageManager;
import be.dieterblancke.bungeeutilisalsx.common.player.ProxySyncPlayerUtils;
import be.dieterblancke.bungeeutilisalsx.common.punishment.PunishmentHelper;
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

    private final ProxyOperationsApi proxyOperationsApi = new BungeeOperationsApi();
    private final CommandManager commandManager = new BungeeCommandManager();
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
        return new BuXApi(
                new PluginLanguageManager(),
                new EventLoader(),
                ConfigFiles.HUBBALANCER.isEnabled() ? new HubBalancer() : null,
                new PunishmentHelper(),
                ConfigFiles.CONFIG.getConfig().getBoolean( "multi-proxy.enabled" ) ?
                        this.proxyOperationsApi.getPlugin( "ProxySync" ).isPresent()
                                ? new ProxySyncPlayerUtils()
                                : new RedisPlayerUtils()
                        : new BungeePlayerUtils()
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
    }

    @Override
    protected void registerPluginSupports()
    {
        super.registerPluginSupports();

        PluginSupport.registerPluginSupport( PremiumVanishPluginSupport.class );
        PluginSupport.registerPluginSupport( TritonPluginSupport.class );
    }

    @Override
    public ProxyOperationsApi proxyOperations()
    {
        return proxyOperationsApi;
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

    public BungeeAudiences getBungeeAudiences()
    {
        return bungeeAudiences;
    }

    @Override
    protected void registerMetrics()
    {
        final Metrics metrics = new Metrics( Bootstrap.getInstance(), 5134 );

        metrics.addCustomChart( new SimplePie(
                "punishments",
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
        metrics.addCustomChart( new SimplePie(
                "hubbalancer",
                () -> this.getApi().getHubBalancer() != null ? "enabled" : "disabled"
        ) );
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
