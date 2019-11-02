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

package com.dbsoftwares.bungeeutilisals.api.utils;

import java.util.function.Consumer;

public class Validate
{

    private Validate()
    {
    }

    public static void checkNotNull( Object toCheck, String error )
    {
        if ( toCheck == null )
        {
            throw new NullPointerException( error );
        }
    }

    public static void ifTrue( boolean toCheck, String error )
    {
        if ( toCheck )
        {
            throw new RuntimeException( error );
        }
    }

    public static void ifFalse( boolean toCheck, String error )
    {
        if ( !toCheck )
        {
            throw new RuntimeException( error );
        }
    }

    public static <T> void ifNull( T toCheck, Consumer<T> consumer )
    {
        if ( toCheck == null )
        {
            consumer.accept( toCheck );
        }
    }

    public static <T> void ifNotNull( T toCheck, Consumer<T> consumer )
    {
        if ( toCheck != null )
        {
            consumer.accept( toCheck );
        }
    }

    public static void ifTrue( boolean toCheck, Consumer<Boolean> consumer )
    {
        if ( toCheck )
        {
            consumer.accept( toCheck );
        }
    }

    public static void ifFalse( boolean toCheck, Consumer<Boolean> consumer )
    {
        if ( !toCheck )
        {
            consumer.accept( toCheck );
        }
    }
}