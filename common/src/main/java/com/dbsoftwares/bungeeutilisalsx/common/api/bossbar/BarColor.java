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

package com.dbsoftwares.bungeeutilisalsx.common.api.bossbar;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

public class BarColor
{

    public static final BarColor PINK;
    public static final BarColor BLUE;
    public static final BarColor RED;
    public static final BarColor GREEN;
    public static final BarColor YELLOW;
    public static final BarColor PURPLE;
    public static final BarColor WHITE;

    public static final List<BarColor> values;

    static
    {
        PINK = new BarColor( 0 );
        BLUE = new BarColor( 1 );
        RED = new BarColor( 2 );
        GREEN = new BarColor( 3 );
        YELLOW = new BarColor( 4 );
        PURPLE = new BarColor( 5 );
        WHITE = new BarColor( 6 );

        values = Lists.newArrayList( PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE );
    }

    @Getter
    private final int id;

    public BarColor( int id )
    {
        this.id = id;
    }

    public static BarColor[] values()
    {
        return values.toArray( new BarColor[0] );
    }

    public static BarColor fromId( int action )
    {
        return values.stream().filter( a -> a.id == action ).findFirst().orElse( PINK );
    }

    public static BarColor valueOf( String color )
    {
        if ( color == null )
        {
            return PINK;
        }
        switch ( color )
        {
            default:
            case "PINK":
                return PINK;
            case "BLUE":
                return BLUE;
            case "RED":
                return RED;
            case "GREEN":
                return GREEN;
            case "YELLOW":
                return YELLOW;
            case "PURPLE":
                return PURPLE;
            case "WHITE":
                return WHITE;
        }
    }

    @Override
    public boolean equals( Object obj )
    {
        return obj == this || ( obj instanceof BarColor && ( (BarColor) obj ).getId() == id );
    }
}