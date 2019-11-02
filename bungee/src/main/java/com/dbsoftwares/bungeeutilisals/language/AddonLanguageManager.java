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

package com.dbsoftwares.bungeeutilisals.language;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.addon.Addon;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.configuration.api.FileStorageType;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.io.ByteStreams;

import java.io.*;

public class AddonLanguageManager extends AbstractLanguageManager
{

    public AddonLanguageManager( BungeeUtilisals plugin )
    {
        super( plugin );
    }

    @Override
    public void loadLanguages( String addonName )
    {
        Addon addon = BUCore.getApi().getAddonManager().getAddon( addonName );
        if ( addon == null )
        {
            return;
        }
        File folder = plugins.get( addonName );

        for ( Language language : languages )
        {
            String name = language.getName();
            File lang;

            if ( fileTypes.get( addonName ).equals( FileStorageType.JSON ) )
            {
                lang = loadResource( addonName, "languages/" + name + ".json", new File( folder, name + ".json" ) );
            } else
            {
                lang = loadResource( addonName, "languages/" + name + ".yml", new File( folder, name + ".yml" ) );
            }

            if ( !lang.exists() )
            {
                continue;
            }
            try
            {
                IConfiguration configuration;

                if ( fileTypes.get( addonName ).equals( FileStorageType.JSON ) )
                {
                    configuration = IConfiguration.loadJsonConfiguration( lang );
                    configuration.copyDefaults( IConfiguration.loadJsonConfiguration( addon.getResource( "languages/" + name + ".json" ) ) );
                } else
                {
                    configuration = IConfiguration.loadYamlConfiguration( lang );
                    configuration.copyDefaults( IConfiguration.loadYamlConfiguration( addon.getResource( "languages/" + name + ".yml" ) ) );
                }

                configurations.put( lang, configuration );
                saveLanguage( addonName, language );
            } catch ( IOException e )
            {
                BUCore.getLogger().error( "An error occured: ", e );
            }
        }
    }

    @Override
    protected File loadResource( String addonName, String source, File target )
    {
        Addon addon = BUCore.getApi().getAddonManager().getAddon( addonName );
        if ( addon == null )
        {
            return target;
        }
        File folder = plugins.get( addonName );
        if ( !folder.exists() )
        {
            folder.mkdir();
        }
        try
        {
            if ( !target.exists() && target.createNewFile() )
            {
                try ( InputStream in = addon.getResource( source ); OutputStream out = new FileOutputStream( target ) )
                {
                    if ( in == null )
                    {
                        BUCore.getLogger().info( "Could not find default language configuration configuration for " +
                                source.replace( "languages/", "" ).replace( ".json", "" ) +
                                " for addon " + addonName );
                        return null;
                    }
                    ByteStreams.copy( in, out );
                    BUCore.getLogger().info( "Loading default language configuration for "
                            + source.replace( "languages/", "" ).replace( ".json", "" ) + " for addon "
                            + addonName );
                }
            }
        } catch ( Exception e )
        {
            BUCore.getLogger().error( "An error occured: ", e );
        }
        return target;
    }
}