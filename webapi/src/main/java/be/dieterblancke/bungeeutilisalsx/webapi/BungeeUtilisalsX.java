package be.dieterblancke.bungeeutilisalsx.webapi;

import be.dieterblancke.bungeeutilisalsx.common.*;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Platform;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection.ReflectionUtils;
import be.dieterblancke.bungeeutilisalsx.common.commands.CommandManager;
import be.dieterblancke.bungeeutilisalsx.common.event.EventLoader;
import be.dieterblancke.bungeeutilisalsx.common.language.PluginLanguageManager;
import be.dieterblancke.bungeeutilisalsx.webapi.util.SpringCommandManager;
import be.dieterblancke.bungeeutilisalsx.webapi.util.SpringPluginDescription;
import be.dieterblancke.bungeeutilisalsx.webapi.util.SpringProxyOperations;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Log
@Component
public class BungeeUtilisalsX extends AbstractBungeeUtilisalsX
{

    private final IPluginDescription pluginDescription = new SpringPluginDescription();
    private final CommandManager commandManager = new SpringCommandManager();
    private final ProxyOperationsApi proxyOperations = new SpringProxyOperations();

    public BungeeUtilisalsX()
    {
        this.initialize();
    }

    @Override
    public void initialize()
    {
        if ( !getDataFolder().exists() )
        {
            getDataFolder().mkdirs();
        }

        this.loadConfigs();
        this.loadDatabase();

        this.api = this.createBuXApi();

        this.registerLanguages();
        this.registerCommands();
        this.setupTasks();
    }

    @Override
    protected void loadConfigs()
    {
        ConfigFiles.CONFIG.load();
        ConfigFiles.LANGUAGES_CONFIG.load();
        ConfigFiles.PUNISHMENT_CONFIG.load();
    }

    @Override
    public void reload()
    {
    }

    @Override
    protected void registerPluginSupports()
    {
    }

    @Override
    protected IBuXApi createBuXApi()
    {
        return new BuXApi( new PluginLanguageManager(), new EventLoader() );
    }

    @Override
    public CommandManager getCommandManager()
    {
        return commandManager;
    }

    @Override
    protected void loadPlaceHolders()
    {
    }

    @Override
    protected void registerListeners()
    {
    }

    @Override
    public ProxyOperationsApi proxyOperations()
    {
        return proxyOperations;
    }

    @Override
    public File getDataFolder()
    {
        return new File( pluginDescription.getFile(), pluginDescription.getName() );
    }

    @Override
    public String getVersion()
    {
        return pluginDescription.getVersion();
    }

    @Override
    public List<StaffUser> getStaffMembers()
    {
        return Collections.emptyList();
    }

    @Override
    public IPluginDescription getDescription()
    {
        return pluginDescription;
    }

    @Override
    public Logger getLogger()
    {
        return log;
    }

    @Override
    public Platform getPlatform()
    {
        return Platform.SPRING;
    }

    @Override
    protected void registerMetrics()
    {
        // do nothing
    }
}
