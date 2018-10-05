/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *  *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *  *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.utils.redis.channeldata;

import lombok.Data;

@Data
public class APIAnnouncement {

    private String prefix;
    private String message;
    private String permission;
    private boolean language;
    private Object[] placeHolders;

    public APIAnnouncement(String prefix, String message, String permission, boolean language, Object... placeholders) {
        this.prefix = prefix;
        this.message = message;
        this.permission = permission;
        this.language = language;
        this.placeHolders = placeholders;
    }
}