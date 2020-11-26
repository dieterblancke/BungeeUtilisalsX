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

package com.dbsoftwares.bungeeutilisalsx.common.updater;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.MathUtils;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.google.common.io.CharStreams;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@RequiredArgsConstructor
public class UpdateRunner implements Runnable
{

    private static final Gson GSON = new Gson();
    private static final String ERROR_STRING = "An error occured: ";

    private final Updater updater;
    private boolean updateFound;
    private String url;

    @Override
    public void run()
    {
        final UpdatableData data = updater.getUpdatable();

        try
        {
            final String response = sendGetRequest( data.getUrl() );
            final JsonObject object = GSON.fromJson( response, JsonObject.class );
            final String status = object.get( "status" ).getAsString();

            if ( status.equalsIgnoreCase( "success" ) )
            {
                final String version = object.get( "version" ).getAsString();

                if ( checkForUpdate( data.getCurrentVersion(), version ) )
                {
                    // update found
                    updateFound = true;
                    url = object.get( "downloadurl" ).getAsString();

                    BuX.getApi().langPermissionBroadcast(
                            "updater.update-found",
                            ConfigFiles.CONFIG.getConfig().getString( "updater.permission" ),
                            "{name}", data.getName(),
                            "{version}", data.getCurrentVersion(),
                            "{newVersion}", version
                    );
                }
            }
        }
        catch ( Exception e )
        {
            // ignore
        }
    }

    private String sendGetRequest( final String url )
    {
        String result = "";
        try
        {
            final HttpsURLConnection connection = (HttpsURLConnection) new URL( url ).openConnection();
            connection.setRequestMethod( "GET" );

            try ( InputStream is = connection.getInputStream();
                  InputStreamReader isr = new InputStreamReader( is );
                  BufferedReader in = new BufferedReader( isr ) )
            {
                result = CharStreams.toString( in );
            }
        }
        catch ( IOException e )
        {
            // ignore
        }
        return result;
    }

    void shutdown()
    {
        if ( updateFound && url != null )
        {
            try
            {
                final HttpsURLConnection connection = (HttpsURLConnection) new URL( url ).openConnection();
                connection.setRequestMethod( "GET" );

                try ( InputStream is = connection.getInputStream() )
                {
                    Files.copy( is, updater.getUpdatable().getFile().toPath(), StandardCopyOption.REPLACE_EXISTING );
                }
            }
            catch ( Exception e )
            {
                // ignore
            }
        }
    }

    private boolean checkForUpdate( final String version, final String newVersion )
    {
        if ( MathUtils.isInteger( version ) && MathUtils.isInteger( newVersion ) )
        {
            return Integer.parseInt( version ) < Integer.parseInt( newVersion );
        }
        try
        {
            return checkStringVersion( version, newVersion );
        }
        catch ( Exception e )
        {
            // if for some reason an exception pops up, it will default back to a simple equals check
            return !newVersion.equals( version );
        }
    }

    private boolean checkStringVersion( String version, String newVersion )
    {
        version = version.replaceAll( "[^\\d]", "" );
        newVersion = newVersion.replaceAll( "[^\\d]", "" );

        return Integer.parseInt( newVersion ) > Integer.parseInt( version );
    }
}
