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

import java.util.List;

public interface OfflineMessageDao
{

    List<OfflineMessage> getOfflineMessages( String username );

    void sendOfflineMessage( String username, OfflineMessage message );

    void deleteOfflineMessage( Long id );

    @Value
    class OfflineMessage
    {
        Long id;
        String languagePath;
        Object[] placeholders;

        public OfflineMessage( final Long id, final String languagePath, final Object... placeholders )
        {
            this.id = id;
            this.languagePath = languagePath;
            this.placeholders = placeholders;
        }
    }
}