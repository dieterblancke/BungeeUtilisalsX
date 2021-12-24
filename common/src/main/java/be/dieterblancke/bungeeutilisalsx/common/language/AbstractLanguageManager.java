/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package be.dieterblancke.bungeeutilisalsx.common.language;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.language.ILanguageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.language.Language;
import be.dieterblancke.bungeeutilisalsx.common.api.language.LanguageConfig;
import be.dieterblancke.bungeeutilisalsx.common.api.language.LanguageIntegration;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.configuration.api.FileStorageType;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

@Getter
public abstract class AbstractLanguageManager implements ILanguageManager
{

    protected Map<String, File> plugins = Maps.newHashMap();
    protected Map<String, FileStorageType> fileTypes = Maps.newHashMap();
    protected Map<File, LanguageConfig> configurations = Maps.newHashMap();
    protected List<Language> languages = Lists.newArrayList();
    protected LanguageIntegration integration;

    AbstractLanguageManager()
    {
        ISection section = ConfigFiles.LANGUAGES_CONFIG.getConfig().getSection( "languages" );

        for ( String key : section.getKeys() )
        {
            languages.add( new Language( key, section.getBoolean( key + ".default" ) ) );
        }
    }

    @Override
    public Language getLangOrDefault( String language )
    {
        return getLanguage( language ).orElse( getDefaultLanguage() );
    }

    @Override
    public LanguageIntegration getLanguageIntegration()
    {
        return integration;
    }

    @Override
    public void setLanguageIntegration( LanguageIntegration integration )
    {
        this.integration = integration;
    }

    @Override
    public Language getDefaultLanguage()
    {
        return languages.stream().filter( Language::isDefault ).findFirst().orElse( languages.stream().findFirst().orElse( null ) );
    }

    @Override
    public Optional<Language> getLanguage( String language )
    {
        return languages.stream().filter( lang -> lang.getName().equalsIgnoreCase( language ) ).findFirst();
    }

    @Override
    public void addPlugin( String plugin, File folder, FileStorageType type )
    {
        plugins.put( plugin, folder );
        fileTypes.put( plugin, type );
    }

    @Override
    public LanguageConfig getLanguageConfiguration( String plugin, User user )
    {
        LanguageConfig config = null;
        if ( user != null )
        {
            config = getConfig( plugin, user.getLanguage() );
        }
        if ( config == null )
        {
            config = getConfig( plugin, getDefaultLanguage() );
        }
        return config;
    }

    @Override
    public LanguageConfig getLanguageConfiguration( String plugin, String player )
    {
        return getLanguageConfiguration( plugin, BuX.getApi().getUser( player ).orElse( null ) );
    }

    @Override
    public File getFile( String plugin, Language language )
    {
        if ( !plugins.containsKey( plugin ) )
        {
            throw new RuntimeException( "The plugin " + plugin + " is not registered!" );
        }
        if ( fileTypes.get( plugin ).equals( FileStorageType.JSON ) )
        {
            return new File( plugins.get( plugin ), language.getName() + ".json" );
        }
        return new File( plugins.get( plugin ), language.getName() + ".yml" );
    }

    @Override
    public LanguageConfig getConfig( String plugin, Language language )
    {
        language = language == null ? getDefaultLanguage() : language;
        if ( !plugins.containsKey( plugin ) )
        {
            throw new RuntimeException( "The plugin " + plugin + " is not registered!" );
        }
        File lang = getFile( plugin, language );

        if ( !configurations.containsKey( lang ) )
        {
            BuX.getLogger().warning( "The plugin " + plugin + " did not register the language " + language.getName() + " yet!" );

            File deflang = getFile( plugin, getDefaultLanguage() );
            if ( configurations.containsKey( deflang ) )
            {
                return configurations.get( deflang );
            }
            return null;
        }
        return configurations.get( lang );
    }

    @Override
    public boolean isRegistered( String plugin, Language language )
    {
        if ( !plugins.containsKey( plugin ) )
        {
            throw new RuntimeException( "The plugin " + plugin + " is not registered!" );
        }
        return getConfig( plugin, language ) != null;
    }

    @Override
    public boolean saveLanguage( String plugin, Language language )
    {
        if ( !plugins.containsKey( plugin ) )
        {
            throw new RuntimeException( "The plugin " + plugin + " is not registered!" );
        }
        File lang = getFile( plugin, language );
        IConfiguration config = configurations.get( lang ).getConfig();

        try
        {
            config.save();
        }
        catch ( IOException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
        return true;
    }

    @Override
    public boolean reloadConfig( String plugin, Language language )
    {
        if ( !plugins.containsKey( plugin ) )
        {
            throw new RuntimeException( "The plugin " + plugin + " is not registered!" );
        }
        File lang = getFile( plugin, language );
        IConfiguration config = configurations.get( lang ).getConfig();
        try
        {
            config.reload();
        }
        catch ( IOException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
        return true;
    }

    @Override
    public boolean useCustomIntegration()
    {
        return integration != null;
    }

    protected abstract File loadResource( Class<?> resourceClass, String plugin, String source, File target );
}