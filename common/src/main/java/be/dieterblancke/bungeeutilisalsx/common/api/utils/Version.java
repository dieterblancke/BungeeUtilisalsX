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

package be.dieterblancke.bungeeutilisalsx.common.api.utils;

import lombok.Getter;

public enum Version
{

    LEGACY( -1 ),
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
    MINECRAFT_1_14_4( 498 ),
    MINECRAFT_1_15( 573 ),
    MINECRAFT_1_15_1( 575 ),
    MINECRAFT_1_15_2( 578 ),
    MINECRAFT_1_16( 735 ),
    MINECRAFT_1_16_1( 736 ),
    MINECRAFT_1_16_2( 751 ),
    MINECRAFT_1_16_3( 753 ),
    MINECRAFT_1_16_4( 754 ),
    MINECRAFT_1_17( 755 ),
    MINECRAFT_1_17_1( 756 );

    @Getter
    private final int versionId;

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