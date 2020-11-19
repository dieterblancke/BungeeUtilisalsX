package com.dbsoftwares.bungeeutilisalsx.spigot;

import com.dbsoftwares.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;
import com.dbsoftwares.bungeeutilisalsx.common.IBuXApi;
import com.dbsoftwares.bungeeutilisalsx.common.IPluginDescription;
import com.dbsoftwares.bungeeutilisalsx.common.ProxyOperationsApi;
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.IBridgeManager;
import com.dbsoftwares.bungeeutilisalsx.common.api.bridge.event.BridgeResponseEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.other.StaffUser;
import com.dbsoftwares.bungeeutilisalsx.common.bridge.BridgeManager;
import com.dbsoftwares.bungeeutilisalsx.common.bridge.handlers.BungeeBridgeResponseHandler;
import com.dbsoftwares.bungeeutilisalsx.common.event.EventLoader;
import com.dbsoftwares.bungeeutilisalsx.common.language.PluginLanguageManager;
import com.dbsoftwares.bungeeutilisalsx.common.manager.CommandManager;
import com.dbsoftwares.configuration.api.FileStorageType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class BungeeUtilisalsX extends AbstractBungeeUtilisalsX
{

    private final IPluginDescription pluginDescription = new SpigotPluginDescription();

    @Override
    public void initialize()
    {
        // Overriding initialize method to avoid the proxy-specific features to boot up.
        super.initialize();

        this.getApi().getEventLoader().register( BridgeResponseEvent.class, new BungeeBridgeResponseHandler() );
    }

    @Override
    protected IBuXApi createBuXApi()
    {
        final IBridgeManager bridgeManager = new BridgeManager();
        final IBuXApi api = new BuXApi(
                bridgeManager,
                new PluginLanguageManager(),
                new EventLoader()
        );
        bridgeManager.setup( api );

        return api;
    }

    @Override
    protected CommandManager getCommandManager()
    {
        throw new UnsupportedOperationException( "The CommandManager is currently only supported in the proxy versions of BungeeUtilisalsX!" );
    }

    @Override
    protected void loadPlaceHolders()
    {
        // todo
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
