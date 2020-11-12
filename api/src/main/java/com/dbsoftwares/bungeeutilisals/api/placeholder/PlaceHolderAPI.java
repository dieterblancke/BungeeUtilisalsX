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

package com.dbsoftwares.bungeeutilisals.api.placeholder;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.handler.InputPlaceHolderEventHandler;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.handler.PlaceHolderEventHandler;
import com.dbsoftwares.bungeeutilisals.api.placeholder.placeholders.ClassPlaceHolder;
import com.dbsoftwares.bungeeutilisals.api.placeholder.placeholders.DefaultPlaceHolder;
import com.dbsoftwares.bungeeutilisals.api.placeholder.placeholders.InputPlaceHolder;
import com.dbsoftwares.bungeeutilisals.api.placeholder.placeholders.PlaceHolder;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.logging.Level;

public class PlaceHolderAPI
{

    private static List<PlaceHolder> placeholders = Lists.newArrayList();

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
            for ( PlaceHolder placeholder : placeholders )
            {
                message = placeholder.format( user, message );
            }
            return message;
        }
        catch ( Exception e )
        {
            BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
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
            for ( PlaceHolder placeholder : placeholders )
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
            BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
            return message;
        }
    }

    public static void loadPlaceHolderPack( PlaceHolderPack pack )
    {
        pack.loadPack();
    }

    public static void addPlaceHolder( ClassPlaceHolder placeholder )
    {
        placeholders.add( placeholder );
    }

    public static void addPlaceHolder( String placeholder, boolean requiresUser, PlaceHolderEventHandler handler )
    {
        placeholders.add( new DefaultPlaceHolder( placeholder, requiresUser, handler ) );
    }

    public static void addPlaceHolder( boolean requiresUser, String prefix, InputPlaceHolderEventHandler handler )
    {
        placeholders.add( new InputPlaceHolder( requiresUser, prefix, handler ) );
    }

    public static PlaceHolder getPlaceHolder( String placeholder )
    {
        for ( PlaceHolder ph : placeholders )
        {
            if ( ph.getPlaceHolderName().equalsIgnoreCase( placeholder ) )
            {
                return ph;
            }
        }
        return null;
    }
}