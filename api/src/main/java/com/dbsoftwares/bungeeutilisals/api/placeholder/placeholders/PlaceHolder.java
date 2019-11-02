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

package com.dbsoftwares.bungeeutilisals.api.placeholder.placeholders;

import com.dbsoftwares.bungeeutilisals.api.placeholder.event.handler.PlaceHolderEventHandler;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@ToString
@EqualsAndHashCode
public abstract class PlaceHolder
{

    protected String placeHolderName;
    @Accessors(fluent = true)
    protected boolean requiresUser;
    protected PlaceHolderEventHandler eventHandler;

    public PlaceHolder( String placeHolderName, boolean requiresUser, PlaceHolderEventHandler eventHandler )
    {
        this.placeHolderName = placeHolderName;
        this.requiresUser = requiresUser;
        this.eventHandler = eventHandler;
    }

    public boolean requiresUser()
    {
        return requiresUser;
    }

    public abstract String format( User user, String message );
}
