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

import lombok.Data;

@Data
public class FriendSettings
{

    private boolean requests;
    private boolean messages;

    public FriendSettings()
    {
        this( FriendSettingType.REQUESTS.getDefault(), FriendSettingType.MESSAGES.getDefault() );
    }

    public FriendSettings( final boolean requests, final boolean messages )
    {
        this.requests = requests;
        this.messages = messages;
    }

    public void set( final FriendSettingType type, final boolean value )
    {
        switch ( type )
        {
            case REQUESTS:
                setRequests( value );
                break;
            case MESSAGES:
                setMessages( value );
                break;
        }
    }

    public boolean check( final FriendSettingType type )
    {
        switch ( type )
        {
            case REQUESTS:
                return isRequests();
            case MESSAGES:
                return isMessages();
            default:
                return false;
        }
    }
}
