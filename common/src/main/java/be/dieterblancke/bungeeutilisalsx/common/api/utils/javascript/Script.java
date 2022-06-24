package be.dieterblancke.bungeeutilisalsx.common.api.utils.javascript;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.configuration.api.IConfiguration;
import be.dieterblancke.configuration.yaml.YamlConfigurationOptions;
import com.google.common.hash.Hashing;
import de.christophkraemer.rhino.javascript.RhinoScriptEngineFactory;
import lombok.Data;
import lombok.SneakyThrows;

import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Level;

@Data
public class Script
{

    private static final File cacheFolder;
    private static final RhinoScriptEngineFactory ENGINE_FACTORY = new RhinoScriptEngineFactory();

    static
    {
        cacheFolder = new File( BuX.getInstance().getDataFolder(), "scripts/cache" );

        if ( !cacheFolder.exists() )
        {
            cacheFolder.mkdirs();
        }
    }

    private final String file;
    private final String script;
    private IConfiguration storage;
    private ScriptEngine engine;

    public Script( String file, String script ) throws ScriptException, IOException
    {
        this.file = file;
        this.script = script;

        final File storage = new File( cacheFolder, hash( file ) );

        if ( !storage.exists() && !storage.createNewFile() )
        {
            return;
        }

        this.storage = IConfiguration.loadYamlConfiguration(
                storage,
                YamlConfigurationOptions.builder().useComments( true ).build()
        );
        this.engine = loadEngine();
    }

    private static String hash( String str )
    {
        return Hashing.sha256().hashString( str, StandardCharsets.UTF_8 ).toString();
    }

    private ScriptEngine loadEngine() throws ScriptException
    {
        final ScriptEngine engine = ENGINE_FACTORY.getScriptEngine();

        engine.put( "storage", storage );
        engine.put( "api", BuX.getApi() );

        engine.eval( """
                function isConsole() {
                    return user === null || user.getClass().getSimpleName() === 'ConsoleUser';
                }
                """ );

        return engine;
    }

    @SneakyThrows
    public String getReplacement( User user )
    {
        final String script = PlaceHolderAPI.formatMessage( user, this.script );

        engine.put( "user", user );

        return String.valueOf( engine.eval( script ) );
    }

    public void unload()
    {
        final File storage = new File( cacheFolder, hash( file ) );

        if ( storage.length() == 0 )
        {
            try
            {
                Files.delete( storage.toPath() );
            }
            catch ( IOException e )
            {
                BuX.getLogger().log( Level.WARNING, "Could not remove empty script storage.", e );
            }
        }
    }
}