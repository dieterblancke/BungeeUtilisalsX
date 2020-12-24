package be.dieterblancke.bungeeutilisalsx.velocity;

import be.dieterblancke.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.common.IBuXApi;
import be.dieterblancke.bungeeutilisalsx.common.IPluginDescription;
import be.dieterblancke.bungeeutilisalsx.common.ProxyOperationsApi;
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
import be.dieterblancke.bungeeutilisalsx.velocity.command.BungeeCommandManager;
import be.dieterblancke.bungeeutilisalsx.velocity.hubbalancer.HubBalancer;
import be.dieterblancke.bungeeutilisalsx.velocity.listeners.*;
import be.dieterblancke.bungeeutilisalsx.velocity.placeholder.DefaultPlaceHolders;
import be.dieterblancke.bungeeutilisalsx.velocity.placeholder.InputPlaceHolders;
import be.dieterblancke.bungeeutilisalsx.velocity.placeholder.UserPlaceHolderPack;
import be.dieterblancke.bungeeutilisalsx.velocity.placeholder.javascript.JavaScriptPlaceHolder;
import be.dieterblancke.bungeeutilisalsx.velocity.utils.player.VelocityPlayerUtils;
import com.dbsoftwares.configuration.api.FileStorageType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Updatable( url = "https://api.dbsoftwares.eu/plugin/bungeeutilisalsx-velocity/" )
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
                new VelocityPlayerUtils(),
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
        Bootstrap.getInstance().getProxyServer().getEventManager().register(
                Bootstrap.getInstance(), new UserChatListener()
        );
        Bootstrap.getInstance().getProxyServer().getEventManager().register(
                Bootstrap.getInstance(), new UserConnectionListener()
        );
        Bootstrap.getInstance().getProxyServer().getEventManager().register(
                Bootstrap.getInstance(), new PluginMessageListener()
        );

        if ( ConfigFiles.PUNISHMENTS.isEnabled() )
        {
            Bootstrap.getInstance().getProxyServer().getEventManager().register(
                    Bootstrap.getInstance(), new PunishmentListener()
            );
        }

        if ( ConfigFiles.MOTD.isEnabled() )
        {
            Bootstrap.getInstance().getProxyServer().getEventManager().register(
                    Bootstrap.getInstance(), new MotdPingListener()
            );
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
        return pluginDescription.getVersion();
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
}
