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

package be.dieterblancke.bungeeutilisalsx.common.api.friends;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class FriendSettings
{

    private final Map<FriendSetting, Object> settings;

    public FriendSettings()
    {
        this( new HashMap<>() );
    }

    public FriendSettings( final Map<FriendSetting, Object> settings )
    {
        this.settings = settings;
    }

    public void set( final FriendSetting key, final Object value )
    {
        settings.put( key, value );
    }

    public <T> T getSetting( final FriendSetting key, final T def )
    {
        return (T) settings.getOrDefault( key, def );
    }
}
