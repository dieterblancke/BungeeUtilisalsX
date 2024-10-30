package dev.endoy.bungeeutilisalsx.common.language;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.language.Language;
import dev.endoy.bungeeutilisalsx.common.api.language.LanguageConfig;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.configuration.api.FileStorageType;
import dev.endoy.configuration.api.IConfiguration;
import dev.endoy.configuration.yaml.YamlConfigurationOptions;
import com.google.common.io.ByteStreams;

import java.io.*;
import java.util.logging.Level;

public class PluginLanguageManager extends AbstractLanguageManager
{

    @Override
    public void loadLanguages( final Class<?> resourceClass, final String pluginName )
    {
        final File folder = plugins.get( pluginName );
        final boolean autoUpdateFiles = ConfigFiles.LANGUAGES_CONFIG.getConfig().getBoolean( "auto-update", true );

        for ( Language language : languages )
        {
            final String name = language.getName();
            final File lang;

            if ( fileTypes.get( pluginName ).equals( FileStorageType.JSON ) )
            {
                lang = loadResource( resourceClass, pluginName, "/configurations/languages/" + name + ".json", new File( folder, name + ".json" ) );
            }
            else
            {
                lang = loadResource( resourceClass, pluginName, "/configurations/languages/" + name + ".yml", new File( folder, name + ".yml" ) );
            }

            if ( lang == null || !lang.exists() )
            {
                continue;
            }
            try
            {
                final IConfiguration configuration;

                if ( fileTypes.get( pluginName ).equals( FileStorageType.JSON ) )
                {
                    configuration = IConfiguration.loadJsonConfiguration( lang );

                    if ( autoUpdateFiles )
                    {
                        configuration.copyDefaults( IConfiguration.loadJsonConfiguration( resourceClass.getResourceAsStream( "/configurations/languages/" + name + ".json" ) ) );
                    }
                }
                else
                {
                    configuration = IConfiguration.loadYamlConfiguration(
                        lang,
                        YamlConfigurationOptions.builder().useComments( false ).build()
                    );

                    if ( autoUpdateFiles )
                    {
                        configuration.copyDefaults( IConfiguration.loadYamlConfiguration(
                            resourceClass.getResourceAsStream( "/configurations/languages/" + name + ".yml" ),
                            YamlConfigurationOptions.builder().useComments( false ).build()
                        ) );
                    }
                }

                configurations.put( lang, new LanguageConfig( language, configuration ) );

                if ( autoUpdateFiles )
                {
                    saveLanguage( pluginName, language );
                }
            }
            catch ( IOException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }
        }
    }

    @Override
    protected File loadResource( final Class<?> resourceClass, final String pluginName, final String source, final File target )
    {
        if ( !plugins.containsKey( pluginName ) )
        {
            return target;
        }
        File folder = plugins.get( pluginName );
        if ( !folder.exists() )
        {
            folder.mkdir();
        }
        try
        {
            if ( !target.exists() && target.createNewFile() )
            {
                try ( InputStream in = resourceClass.getResourceAsStream( source ); OutputStream out = new FileOutputStream( target ) )
                {
                    if ( in == null )
                    {
                        BuX.getLogger().info( "Could not find default language configuration configuration for " +
                            source.replace( "/configurations/languages/", "" ).replace( ".json", "" ) +
                            " for plugin " + pluginName );
                        return null;
                    }
                    ByteStreams.copy( in, out );
                    BuX.getLogger().info( "Loading default language configuration for "
                        + source.replace( "/configurations/languages/", "" ).replace( ".json", "" ) + " for plugin "
                        + pluginName );
                }
            }
        }
        catch ( Exception e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
        return target;
    }
}