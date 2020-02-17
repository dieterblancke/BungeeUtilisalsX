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

package com.dbsoftwares.bungeeutilisals.placeholders;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderPack;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.InputPlaceHolderEvent;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.handler.InputPlaceHolderEventHandler;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.TimeUnit;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.dbsoftwares.configuration.api.IConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class InputPlaceHolders implements PlaceHolderPack
{

    @Override
    public void loadPack()
    {
        PlaceHolderAPI.addPlaceHolder( false, "timeleft", new InputPlaceHolderEventHandler()
        {
            @Override
            public String getReplacement( InputPlaceHolderEvent event )
            {
                IConfiguration configuration = getLanguageConfiguration( event.getUser() );
                SimpleDateFormat dateFormat = new SimpleDateFormat( "dd-MM-yyyy kk:mm:ss" );

                try
                {
                    Date date = dateFormat.parse( event.getArgument() );
                    long time = date.getTime() - System.currentTimeMillis();

                    return configuration.getString( "placeholders.timeleft" )
                            .replace( "%days%", String.valueOf( getDays( time ) ) )
                            .replace( "%hours%", String.valueOf( getHours( time ) ) )
                            .replace( "%minutes%", String.valueOf( getMinutes( time ) ) )
                            .replace( "%seconds%", String.valueOf( getSeconds( time ) ) );
                }
                catch ( ParseException e )
                {
                    return "";
                }
            }
        } );
        PlaceHolderAPI.addPlaceHolder( false, "getcount", new InputPlaceHolderEventHandler()
        {
            @Override
            public String getReplacement( InputPlaceHolderEvent event )
            {
                ServerGroup server = FileLocation.SERVERGROUPS.getData( event.getArgument() );

                if ( server == null )
                {
                    return "0";
                }

                return String.valueOf( server.getPlayers() );
            }
        } );
    }

    // Utility functions
    private IConfiguration getLanguageConfiguration( User user )
    {
        if ( user == null )
        {
            return BUCore.getApi().getLanguageManager().getConfig(
                    BUCore.getApi().getPlugin().getDescription().getName(),
                    BUCore.getApi().getLanguageManager().getDefaultLanguage()
            );
        }
        return user.getLanguageConfig();
    }

    private long getDays( long time )
    {
        return TimeUnit.MILLISECONDS.toDays( time );
    }

    private long getHours( long time )
    {
        time = time - TimeUnit.DAYS.toMillis( getDays( time ) );

        return TimeUnit.MILLISECONDS.toHours( time );
    }

    private long getMinutes( long time )
    {
        time = time - TimeUnit.DAYS.toMillis( getDays( time ) );
        time = time - TimeUnit.HOURS.toMillis( getHours( time ) );

        return TimeUnit.MILLISECONDS.toMinutes( time );
    }

    private long getSeconds( long time )
    {
        time = time - TimeUnit.DAYS.toMillis( getDays( time ) );
        time = time - TimeUnit.HOURS.toMillis( getHours( time ) );
        time = time - TimeUnit.MINUTES.toMillis( getMinutes( time ) );

        return TimeUnit.MILLISECONDS.toSeconds( time );
    }
}