package com.dbsoftwares.bungeeutilisalsx.velocity;

import com.dbsoftwares.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;
import com.dbsoftwares.bungeeutilisalsx.common.IBuXApi;
import com.dbsoftwares.bungeeutilisalsx.common.IPluginDescription;
import com.dbsoftwares.bungeeutilisalsx.common.ProxyOperationsApi;
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.IBridgeManager;
import com.dbsoftwares.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.other.StaffUser;
import com.dbsoftwares.bungeeutilisalsx.common.bridge.BridgeManager;
import com.dbsoftwares.bungeeutilisalsx.common.event.EventLoader;
import com.dbsoftwares.bungeeutilisalsx.common.language.PluginLanguageManager;
import com.dbsoftwares.bungeeutilisalsx.common.manager.ChatManager;
import com.dbsoftwares.bungeeutilisalsx.common.manager.CommandManager;
import com.dbsoftwares.bungeeutilisalsx.velocity.command.BungeeCommandManager;
import com.dbsoftwares.bungeeutilisalsx.velocity.hubbalancer.HubBalancer;
import com.dbsoftwares.bungeeutilisalsx.velocity.listeners.MotdPingListener;
import com.dbsoftwares.bungeeutilisalsx.velocity.listeners.PunishmentListener;
import com.dbsoftwares.bungeeutilisalsx.velocity.listeners.UserChatListener;
import com.dbsoftwares.bungeeutilisalsx.velocity.listeners.UserConnectionListener;
import com.dbsoftwares.bungeeutilisalsx.velocity.placeholder.DefaultPlaceHolders;
import com.dbsoftwares.bungeeutilisalsx.velocity.placeholder.InputPlaceHolders;
import com.dbsoftwares.bungeeutilisalsx.velocity.placeholder.UserPlaceHolderPack;
import com.dbsoftwares.bungeeutilisalsx.velocity.placeholder.javascript.JavaScriptPlaceHolder;
import com.dbsoftwares.bungeeutilisalsx.velocity.utils.PunishmentExecutor;
import com.dbsoftwares.bungeeutilisalsx.velocity.utils.player.VelocityPlayerUtils;
import com.dbsoftwares.configuration.api.FileStorageType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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
                new PunishmentExecutor(),
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
