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

package com.dbsoftwares.bungeeutilisalsx.common.api.utils.other;

import lombok.Data;

@Data
public class QueuedMessage
{

    private final long id;
    private final String user;
    private final Message message;
    private final String type;

    public QueuedMessage( long id, String user, Message message, String type )
    {
        this.id = id;
        this.user = user;
        this.message = message;
        this.type = type;
    }

    @Data
    public static class Message
    {
        private final String languagePath;
        private final Object[] placeHolders;

        public Message( final String languagePath, final Object... placeHolders )
        {
            this.languagePath = languagePath;
            this.placeHolders = placeHolders;
        }
    }
}
