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

package be.dieterblancke.bungeeutilisalsx.common.api.placeholder.placeholders;

import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.PlaceHolderEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.event.handler.PlaceHolderEventHandler;

public class DefaultPlaceHolder extends PlaceHolder
{

    public DefaultPlaceHolder( String placeHolder, boolean requiresUser, PlaceHolderEventHandler handler )
    {
        super( placeHolder, requiresUser, handler );
    }

    @Override
    public String format( User user, String message )
    {
        if ( placeHolderName == null || !message.contains( placeHolderName ) )
        {
            return message;
        }
        PlaceHolderEvent event = new PlaceHolderEvent( user, this, message );
        return message.replace( placeHolderName, Utils.c( eventHandler.getReplacement( event ) ) );
    }
}