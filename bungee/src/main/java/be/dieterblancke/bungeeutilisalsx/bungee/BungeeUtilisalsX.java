package be.dieterblancke.bungeeutilisalsx.bungee;

import be.dieterblancke.bungeeutilisalsx.bungee.command.BungeeCommandManager;
import be.dieterblancke.bungeeutilisalsx.bungee.hubbalancer.HubBalancer;
import be.dieterblancke.bungeeutilisalsx.bungee.listeners.*;
import be.dieterblancke.bungeeutilisalsx.bungee.placeholder.DefaultPlaceHolders;
import be.dieterblancke.bungeeutilisalsx.bungee.placeholder.InputPlaceHolders;
import be.dieterblancke.bungeeutilisalsx.bungee.placeholder.UserPlaceHolderPack;
import be.dieterblancke.bungeeutilisalsx.bungee.placeholder.javascript.JavaScriptPlaceHolder;
import be.dieterblancke.bungeeutilisalsx.bungee.utils.player.BungeePlayerUtils;
import be.dieterblancke.bungeeutilisalsx.bungee.utils.player.RedisPlayerUtils;
import be.dieterblancke.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.common.IBuXApi;
import be.dieterblancke.bungeeutilisalsx.common.IPluginDescription;
import be.dieterblancke.bungeeutilisalsx.common.ProxyOperationsApi;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.AnnouncementType;
import be.dieterblancke.bungeeutilisalsx.common.api.announcer.Announcer;
import be.dieterblancke.bungeeutilisalsx.common.api.bridge.IBridgeManager;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.bridge.BridgeManager;
import be.dieterblancke.bungeeutilisalsx.common.event.EventLoader;
import be.dieterblancke.bungeeutilisalsx.common.language.PluginLanguageManager;
import be.dieterblancke.bungeeutilisalsx.common.manager.ChatManager;
import be.dieterblancke.bungeeutilisalsx.common.manager.CommandManager;
import be.dieterblancke.bungeeutilisalsx.common.manager.PunishmentHelper;
import be.dieterblancke.bungeeutilisalsx.common.updater.Updatable;
import com.dbsoftwares.configuration.api.FileStorageType;
import net.md_5.bungee.api.ProxyServer;
import org.bstats.bungeecord.Metrics;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Updatable( url = "https://api.dbsoftwares.eu/plugin/bungeeutilisalsx-bungee/" )
public class BungeeUtilisalsX extends AbstractBungeeUtilisalsX
{

    private final ProxyOperationsApi proxyOperationsApi = new BungeeOperationsApi();
    private final CommandManager commandManager = new BungeeCommandManager();
    private final IPluginDescription pluginDescription = new BungeePluginDescription();
    private final List<StaffUser> staffMembers = new ArrayList<>();

    @Override
    public void initialize()
    {
        super.initialize();

        this.registerMetrics();
    }

    @Override
    protected IBuXApi createBuXApi()
    {
        final IBridgeManager bridgeManager = new BridgeManager();
        final IBuXApi api = new BuXApi(
                bridgeManager,
                new PluginLanguageManager(),
                new EventLoader(),
                ConfigFiles.HUBBALANCER.isEnabled() ? new HubBalancer() : null,
                new PunishmentHelper(),
                bridgeManager.useBridging() ? new RedisPlayerUtils() : new BungeePlayerUtils(),
                new ChatManager()
        );
        bridgeManager.setup( api );

        return api;
    }

    @Override
    protected CommandManager getCommandManager()
    {
        return commandManager;
    }

    @Override
    protected void loadPlaceHolders()
    {
        PlaceHolderAPI.loadPlaceHolderPack( new DefaultPlaceHolders() );
        PlaceHolderAPI.loadPlaceHolderPack( new InputPlaceHolders() );
        PlaceHolderAPI.loadPlaceHolderPack( new UserPlaceHolderPack() );

        if ( ConfigFiles.CONFIG.getConfig().getBoolean( "scripting" ) )
        {
            new JavaScriptPlaceHolder().register();
        }
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
        ProxyServer.getInstance().getPluginManager().registerListener( Bootstrap.getInstance(), new UserChatListener() );
        ProxyServer.getInstance().getPluginManager().registerListener( Bootstrap.getInstance(), new UserConnectionListener() );
        ProxyServer.getInstance().getPluginManager().registerListener( Bootstrap.getInstance(), new PluginMessageListener() );

        if ( ConfigFiles.PUNISHMENTS.isEnabled() )
        {
            ProxyServer.getInstance().getPluginManager().registerListener( Bootstrap.getInstance(), new PunishmentListener() );
        }

        if ( ConfigFiles.MOTD.isEnabled() )
        {
            ProxyServer.getInstance().getPluginManager().registerListener( Bootstrap.getInstance(), new MotdPingListener() );
        }
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

    private void registerMetrics()
    {
        final Metrics metrics = new Metrics( Bootstrap.getInstance(), 5134 );

        metrics.addCustomChart( new Metrics.SimplePie(
                "punishments",
                () -> ConfigFiles.PUNISHMENTS.isEnabled() ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new Metrics.SimplePie(
                "motds",
                () -> ConfigFiles.MOTD.isEnabled() ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new Metrics.SimplePie(
                "friends",
                () -> ConfigFiles.FRIENDS_CONFIG.isEnabled() ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new Metrics.SimplePie(
                "actionbar_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.ACTIONBAR ) ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new Metrics.SimplePie(
                "title_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.TITLE ) ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new Metrics.SimplePie(
                "bossbar_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.BOSSBAR ) ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new Metrics.SimplePie(
                "chat_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.CHAT ) ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new Metrics.SimplePie(
                "tab_announcers",
                () -> Announcer.getAnnouncers().containsKey( AnnouncementType.TAB ) ? "enabled" : "disabled"
        ) );
        metrics.addCustomChart( new Metrics.SimplePie(
                "hubbalancer",
                () -> this.getApi().getHubBalancer() != null ? "enabled" : "disabled"
        ) );
    }
}
