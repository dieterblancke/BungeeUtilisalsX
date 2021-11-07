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

package be.dieterblancke.bungeeutilisalsx.common.api.user;

import be.dieterblancke.bungeeutilisalsx.common.api.language.Language;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStorage
{

    private UUID uuid;
    private String userName;
    private String ip;
    private Language language;
    private Date firstLogin;
    private Date lastLogout;
    private List<String> ignoredUsers = Lists.newArrayList();
    private String joinedHost;

    private Map<String, Object> data = Maps.newHashMap();

    public boolean isLoaded()
    {
        return uuid != null && userName != null && ip != null && language != null && firstLogin != null && lastLogout != null;
    }

    public <T> T getData( final String key )
    {
        return !data.containsKey( key ) ? null : (T) data.get( key );
    }

    public <T> T getDataOrPut( final String key, Supplier<T> supplier )
    {
        if ( !this.hasData( key ) )
        {
            this.setData( key, supplier.get() );
        }
        return this.getData( key );
    }

    public void setData( final String key, final Object value )
    {
        data.put( key, value );
    }

    public boolean hasData( final String key )
    {
        return data.containsKey( key );
    }

    public void removeData( final String key )
    {
        this.data.remove( key );
    }

    public <T> T getData( final UserStorageKey key )
    {
        return this.getData( key.toString() );
    }

    public <T> T getDataOrPut( final UserStorageKey key, Supplier<T> supplier )
    {
        return this.getDataOrPut( key.toString(), supplier );
    }

    public void setData( final UserStorageKey key, final Object value )
    {
        this.setData( key.toString(), value );
    }

    public boolean hasData( final UserStorageKey key )
    {
        return this.hasData( key.toString() );
    }

    public void removeData( final UserStorageKey key )
    {
        this.removeData( key.toString() );
    }
}