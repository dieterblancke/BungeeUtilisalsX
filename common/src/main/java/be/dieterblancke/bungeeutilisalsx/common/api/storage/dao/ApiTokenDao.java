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

package be.dieterblancke.bungeeutilisalsx.common.api.storage.dao;

import lombok.Value;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ApiTokenDao
{

    void createApiToken( ApiToken token );

    Optional<ApiToken> findApiToken( String token );

    void removeApiToken( String token );

    List<ApiToken> getApiTokens();

    enum ApiPermission
    {
        ALL,
        FIND_USER,
        FIND_FRIENDS,
        FIND_BAN,
        FIND_MUTE,
        FIND_TRACK_DATA,
        FIND_KICK,
        FIND_WARN,
        FIND_REPORT,
        UPDATE_USER,
        CREATE_PUNISHMENT,
        REMOVE_PUNISHMENT
    }

    @Value
    class ApiToken
    {
        String apiToken;
        Date expireDate;
        List<ApiPermission> permissions;
    }
}
