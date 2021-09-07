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

package be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event;

import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.placeholders.PlaceHolder;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode( callSuper = true )
public class InputPlaceHolderEvent extends PlaceHolderEvent
{

    private final String argument;

    public InputPlaceHolderEvent( User user, PlaceHolder placeHolder, String message, String argument )
    {
        super( user, placeHolder, message );

        this.argument = argument;
    }
}