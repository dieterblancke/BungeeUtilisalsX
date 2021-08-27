package be.dieterblancke.bungeeutilisalsx.webapi;

import be.dieterblancke.bungeeutilisalsx.common.*;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.StaffUser;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.reflection.ReflectionUtils;
import be.dieterblancke.bungeeutilisalsx.common.commands.CommandManager;
import be.dieterblancke.bungeeutilisalsx.common.event.EventLoader;
import be.dieterblancke.bungeeutilisalsx.common.language.PluginLanguageManager;
import com.dbsoftwares.configuration.api.FileStorageType;
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

    private final IPluginDescription pluginDescription = new WebPluginDescription();

    public BungeeUtilisalsX()
    {
        this.initialize();
    }

    @Override
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
        this.loadDatabase();

        this.api = this.createBuXApi();

        this.registerLanguages();
        this.setupTasks();
    }

    @Override
    protected void loadConfigs()
    {
        ConfigFiles.CONFIG.load();
        ConfigFiles.LANGUAGES_CONFIG.load();
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
        throw new UnsupportedOperationException( "The CommandManager is currently only supported in the proxy versions of BungeeUtilisalsX!" );
    }

    @Override
    protected void loadPlaceHolders()
    {
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
}