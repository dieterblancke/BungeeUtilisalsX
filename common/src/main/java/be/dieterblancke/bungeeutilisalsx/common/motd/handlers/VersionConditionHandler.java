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

package be.dieterblancke.bungeeutilisalsx.common.motd.handlers;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Version;
import be.dieterblancke.bungeeutilisalsx.common.motd.ConditionHandler;
import be.dieterblancke.bungeeutilisalsx.common.motd.MotdConnection;

public class VersionConditionHandler extends ConditionHandler
{

    public VersionConditionHandler( String condition )
    {
        super( condition.replaceFirst( "version ", "" ).replace( ".", "_" ) );
    }

    @Override
    public boolean checkCondition( final MotdConnection connection )
    {
        final Version version = formatVersion( value );

        if ( version == null )
        {
            return false;
        }

        return switch ( operator )
                {
                    case LT -> connection.getVersion() < version.getVersionId();
                    case LTE -> connection.getVersion() <= version.getVersionId();
                    case EQ -> connection.getVersion() == version.getVersionId();
                    case NOT_EQ -> connection.getVersion() != version.getVersionId();
                    case GTE -> connection.getVersion() >= version.getVersionId();
                    case GT -> connection.getVersion() > version.getVersionId();
                    default -> false;
                };
    }

    private Version formatVersion( String mcVersion )
    {
        try
        {
            return Version.valueOf( "MINECRAFT_" + mcVersion );
        }
        catch ( IllegalArgumentException e )
        {
            BuX.getLogger().warning( "Found an invalid version in condition 'version " + condition + "'!" );
            BuX.getLogger().warning( "Available versions:" );
            BuX.getLogger().warning( listVersions() );
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