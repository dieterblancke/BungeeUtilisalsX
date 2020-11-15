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

package com.dbsoftwares.bungeeutilisalsx.common.api.placeholder;

import com.dbsoftwares.bungeeutilisalsx.common.api.placeholder.event.handler.InputPlaceHolderEventHandler;
import com.dbsoftwares.bungeeutilisalsx.common.api.placeholder.event.handler.PlaceHolderEventHandler;
import com.dbsoftwares.bungeeutilisalsx.common.api.placeholder.placeholders.ClassPlaceHolder;
import com.dbsoftwares.bungeeutilisalsx.common.api.placeholder.placeholders.DefaultPlaceHolder;
import com.dbsoftwares.bungeeutilisalsx.common.api.placeholder.placeholders.InputPlaceHolder;
import com.dbsoftwares.bungeeutilisalsx.common.api.placeholder.placeholders.PlaceHolder;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class PlaceHolderAPI
{

    private static final List<PlaceHolder> PLACEHOLDERS = Lists.newArrayList();

    private PlaceHolderAPI()
    {
    }

    public static String formatMessage( User user, String message )
    {
        if ( user == null )
        {
            return formatMessage( message );
        }
        try
        {
            for ( PlaceHolder placeholder : PLACEHOLDERS )
            {
                message = placeholder.format( user, message );
            }
            return message;
        }
        catch ( Exception e )
        {
            log.error( "An error occured: ", e );
            return message;
        }
    }

    public static String formatMessage( String message )
    {
        if ( message == null )
        {
            return "";
        }
        try
        {
            for ( PlaceHolder placeholder : PLACEHOLDERS )
            {
                if ( placeholder.requiresUser() )
                {
                    continue;
                }
                message = placeholder.format( null, message );
            }
            return message;
        }
        catch ( Exception e )
        {
            log.error( "An error occured: ", e );
            return message;
        }
    }

    public static void loadPlaceHolderPack( PlaceHolderPack pack )
    {
        pack.loadPack();
    }

    public static void addPlaceHolder( ClassPlaceHolder placeholder )
    {
        PLACEHOLDERS.add( placeholder );
    }

    public static void addPlaceHolder( String placeholder, boolean requiresUser, PlaceHolderEventHandler handler )
    {
        PLACEHOLDERS.add( new DefaultPlaceHolder( placeholder, requiresUser, handler ) );
    }

    public static void addPlaceHolder( boolean requiresUser, String prefix, InputPlaceHolderEventHandler handler )
    {
        PLACEHOLDERS.add( new InputPlaceHolder( requiresUser, prefix, handler ) );
    }

    public static PlaceHolder getPlaceHolder( String placeholder )
    {
        for ( PlaceHolder ph : PLACEHOLDERS )
        {
            if ( ph.getPlaceHolderName().equalsIgnoreCase( placeholder ) )
            {
                return ph;
            }
        }
        return null;
    }
}