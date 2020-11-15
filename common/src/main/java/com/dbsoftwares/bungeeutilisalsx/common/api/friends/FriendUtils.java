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

package com.dbsoftwares.bungeeutilisalsx.common.api.friends;

import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.configuration.api.ISection;

public class FriendUtils
{

    private FriendUtils()
    {
    }

    public static int getFriendLimit( final User user )
    {
        final String permission = ConfigFiles.FRIENDS_CONFIG.getConfig().getString( "friendlimits.permission" );
        int highestLimit = 0;

        for ( ISection section : ConfigFiles.FRIENDS_CONFIG.getConfig().getSectionList( "friendlimits.limits" ) )
        {
            final String name = section.getString( "name" );
            final int limit = section.getInteger( "limit" );

            if ( !name.equalsIgnoreCase( "default" ) && !user.hasPermission( permission + name ) )
            {
                continue;
            }

            if ( limit > highestLimit )
            {
                highestLimit = limit;
            }
        }

        return highestLimit;
    }
}
