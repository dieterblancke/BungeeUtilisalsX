package com.dbsoftwares.bungeeutilisalsx.common.api.utils.javascript;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.hash.Hashing;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Data
@Slf4j
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

        this.storage = IConfiguration.loadYamlConfiguration( storage );
        this.engine = loadEngine();
    }

    private static String hash( String str )
    {
        return Hashing.sha256().hashString( str, StandardCharsets.UTF_8 ).toString();
    }

    private ScriptEngine loadEngine() throws ScriptException
    {
        final ScriptEngine engine = new ScriptEngineManager().getEngineByName( "nashorn" );

        engine.put( "storage", storage );
        engine.put( "api", BuX.getApi() );

        engine.eval( "function isConsole() { return user === null || user.getClass().getSimpleName() !== 'BUser'; }" );

        return engine;
    }

    public String getReplacement( User user )
    {
        final String script = PlaceHolderAPI.formatMessage( user, this.script );

        engine.put( "user", user );

        try
        {
            return String.valueOf( engine.eval( script ) );
        }
        catch ( ScriptException e )
        {
            log.error( "An error occured:", e );
            return "SCRIPT ERROR";
        }
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
                log.warn( "Could not remove empty script storage.", e );
            }
        }
    }
}