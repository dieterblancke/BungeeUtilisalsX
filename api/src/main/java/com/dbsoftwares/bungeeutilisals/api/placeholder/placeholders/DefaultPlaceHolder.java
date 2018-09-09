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

import com.dbsoftwares.bungeeutilisals.api.placeholder.event.PlaceHolderEvent;
import com.dbsoftwares.bungeeutilisals.api.placeholder.event.handler.PlaceHolderEventHandler;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;

public class DefaultPlaceHolder extends PlaceHolder {

    public DefaultPlaceHolder(String placeHolder, boolean requiresUser, PlaceHolderEventHandler handler) {
        super(placeHolder, requiresUser, handler);
    }

    @Override
    public String format(User user, String message) {
        if (placeHolder == null || !message.contains(placeHolder)) {
            return message;
        }
        PlaceHolderEvent event = new PlaceHolderEvent(user, this, message);
        return message.replace(placeHolder, Utils.c(eventHandler.getReplacement(event)));
    }
}