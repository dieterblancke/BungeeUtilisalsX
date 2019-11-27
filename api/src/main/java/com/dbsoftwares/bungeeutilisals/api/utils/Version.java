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

import lombok.Getter;

public enum Version
{

    MINECRAFT_1_8( 47 ),
    MINECRAFT_1_9( 107 ),
    MINECRAFT_1_9_1( 108 ),
    MINECRAFT_1_9_2( 109 ),
    MINECRAFT_1_9_3( 110 ),
    MINECRAFT_1_10( 210 ),
    MINECRAFT_1_11( 315 ),
    MINECRAFT_1_11_2( 316 ),
    MINECRAFT_1_12( 335 ),
    MINECRAFT_1_12_1( 338 ),
    MINECRAFT_1_12_2( 340 ),
    MINECRAFT_1_13( 393 ),
    MINECRAFT_1_13_1( 401 ),
    MINECRAFT_1_13_2( 404 ),
    MINECRAFT_1_14( 477 ),
    MINECRAFT_1_14_1( 480 ),
    MINECRAFT_1_14_2( 485 ),
    MINECRAFT_1_14_3( 490 ),
    MINECRAFT_1_14_4( 498 );

    @Getter
    private int versionId;

    Version( final int version )
    {
        this.versionId = version;
    }

    public static Version getVersion( int version )
    {
        for ( Version v : values() )
        {
            if ( v.getVersionId() == version )
            {
                return v;
            }
        }
        return MINECRAFT_1_8;
    }

    public static Version latest()
    {
        Version version = null;
        for ( Version v : values() )
        {
            if ( version == null || version.getVersionId() < v.getVersionId() )
            {
                version = v;
            }
        }
        return version;
    }

    public boolean isNewerThen( final Version version )
    {
        return this.versionId >= version.getVersionId();
    }

    public boolean isOlderThen( final Version version )
    {
        return this.versionId < version.getVersionId();
    }

    @Override
    public String toString()
    {
        return super.toString().replace( "MINECRAFT_", "" ).replace( "_", "." );
    }
}