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

package com.dbsoftwares.bungeeutilisals.utils;

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.BUCore;

import java.lang.reflect.Method;

public class APIHandler
{

    private static final Method REGISTER;

    static
    {
        Method register = null;
        try
        {
            register = BUCore.class.getDeclaredMethod( "initAPI", BUAPI.class );
            register.setAccessible( true );
        } catch ( Exception e )
        {
            BUCore.getLogger().error( "An error occured: ", e );
        }

        REGISTER = register;
    }

    public static void registerProvider( BUAPI api )
    {
        try
        {
            REGISTER.invoke( null, api );
        } catch ( Exception e )
        {
            BUCore.getLogger().error( "An error occured: ", e );
        }
    }
}