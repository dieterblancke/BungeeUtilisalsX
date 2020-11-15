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

package com.dbsoftwares.bungeeutilisalsx.common.motd.handlers;

import com.dbsoftwares.bungeeutilisalsx.common.api.utils.Version;
import com.dbsoftwares.bungeeutilisalsx.common.motd.ConditionHandler;
import com.dbsoftwares.bungeeutilisalsx.common.motd.MotdConnection;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VersionConditionHandler extends ConditionHandler
{

    public VersionConditionHandler( String condition )
    {
        super( condition.replaceFirst( "version ", "" ) );
    }

    @Override
    public boolean checkCondition( final MotdConnection connection )
    {
        final String[] args = condition.split( " " );
        final String operator = args[0];
        final Version version = formatVersion( args[1] );

        if ( version == null )
        {
            return false;
        }

        switch ( operator )
        {
            case "<":
                return connection.getVersion() < version.getVersionId();
            case "<=":
                return connection.getVersion() <= version.getVersionId();
            case "==":
                return connection.getVersion() == version.getVersionId();
            case "!=":
                return connection.getVersion() != version.getVersionId();
            case ">=":
                return connection.getVersion() >= version.getVersionId();
            case ">":
                return connection.getVersion() > version.getVersionId();
            default:
                return false;
        }
    }

    private Version formatVersion( String mcVersion )
    {
        try
        {
            return Version.valueOf( "MINECRAFT_" + mcVersion.replace( ".", "_" ) );
        }
        catch ( IllegalArgumentException e )
        {
            log.warn( "Found an invalid version in condition 'version " + condition + "'!" );
            log.warn( "Available versions:" );
            log.warn( listVersions() );
            return null;
        }
    }

    private String listVersions()
    {
        final StringBuilder builder = new StringBuilder();
        int length = Version.values().length;

        for ( int i = 0; i < length; i++ )
        {
            final Version version = Version.values()[i];

            builder.append( version.toString() );

            if ( i < length - 1 )
            {
                builder.append( ", " );
            }
        }

        return builder.toString();
    }
}