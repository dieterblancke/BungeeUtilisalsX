package be.dieterblancke.bungeeutilisalsx.common.api.utils.javascript;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.hash.Hashing;
import lombok.Data;
import lombok.SneakyThrows;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import javax.script.ScriptContext;
import javax.script.ScriptException;
import javax.script.SimpleScriptContext;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.logging.Level;

@Data
public class Script
{

    private static final File cacheFolder;

    static
    {
        cacheFolder = new File( BuX.getInstance().getDataFolder(), "scripts" + File.separator + "cache" );

        if ( !cacheFolder.exists() )
        {
            cacheFolder.mkdirs();
        }
    }

    private final String file;
    private final String script;
    private Context context;
    private SimpleScriptContext simpleScriptContext;
    private Scriptable scriptable;
    private IConfiguration storage;

    public Script( String file, String script ) throws ScriptException, IOException
    {
        this.file = file;
        this.script = script;

        final File storage = new File( cacheFolder, hash( file ) );

        if ( !storage.exists() && !storage.createNewFile() )
        {
            return;
        }

        this.storage = IConfiguration.loadYamlConfiguration( storage );
        this.context = Context.enter();
        this.simpleScriptContext = new SimpleScriptContext();
        this.scriptable = new ExternalScriptable( simpleScriptContext );

        this.put( "storage", storage );
        this.put( "api", BuX.getApi() );

        this.eval( """
                function isConsole() {
                    return user === null || user.getClass().getSimpleName() === 'ConsoleUser';
                }
                """ );
    }

    private static String hash( String str )
    {
        return Hashing.sha256().hashString( str, StandardCharsets.UTF_8 ).toString();
    }

    public String getReplacement( User user )
    {
        final String script = PlaceHolderAPI.formatMessage( user, this.script );

        this.put( "user", user );

        return String.valueOf( this.eval( script ) );
    }

    @SneakyThrows
    private Object eval( final String str )
    {
        return context.evaluateReader( scriptable, new StringReader( str ), "<Unknown source>", 1, null );
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

    private void put( String key, Object value )
    {
        simpleScriptContext.getBindings( ScriptContext.ENGINE_SCOPE ).put( key, value );
    }
}