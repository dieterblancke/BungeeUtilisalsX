package be.dieterblancke.bungeeutilisalsx.common.api.utils.config;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.FileUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.exception.InvalidConfigFileException;
import be.dieterblancke.configuration.api.IConfiguration;
import be.dieterblancke.configuration.yaml.YamlConfigurationOptions;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Config
{

    @Getter
    protected IConfiguration config;
    @Getter
    protected String location;

    public Config( final String location )
    {
        this.location = location;
    }

    public void load()
    {
        final File file = new File( BuX.getInstance().getDataFolder(), location );

        if ( !file.exists() )
        {
            final InputStream inputStream = FileUtils.getResourceAsStream( location );

            if ( inputStream == null )
            {
                throw new InvalidConfigFileException();
            }

            IConfiguration.createDefaultFile( inputStream, file );

            this.config = IConfiguration.loadYamlConfiguration(
                    file,
                    YamlConfigurationOptions.builder().useComments( true ).build()
            );
        }
        else
        {
            // update configurations ...
            this.config = IConfiguration.loadYamlConfiguration(
                    file,
                    YamlConfigurationOptions.builder().useComments( true ).build()
            );
        }

        this.setup();
        BuX.debug( "Successfully loaded config file: " + location.substring( 1 ) );
    }

    public void reload()
    {
        try
        {
            this.config.reload();
            this.purge();
            this.setup();
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    protected void purge()
    {
        // do nothing
    }

    protected void setup()
    {
        // do nothing
    }

    public boolean isEnabled()
    {
        if ( config == null )
        {
            return false;
        }
        if ( config.exists( "enabled" ) )
        {
            return config.getBoolean( "enabled" );
        }
        return true;
    }

    public boolean isEnabled( String path )
    {
        return isEnabled( path, true );
    }

    public boolean isEnabled( String path, boolean def )
    {
        if ( config.exists( path + ".enabled" ) )
        {
            return config.getBoolean( path + ".enabled" );
        }
        return def;
    }
}
