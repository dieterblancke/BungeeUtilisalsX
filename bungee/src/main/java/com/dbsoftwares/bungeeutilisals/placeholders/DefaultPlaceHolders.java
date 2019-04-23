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

package com.dbsoftwares.bungeeutilisals.placeholders;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderPack;

public class DefaultPlaceHolders implements PlaceHolderPack {

    @Override
    public void loadPack() {
        PlaceHolderAPI.addPlaceHolder("{users-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.users"));
        PlaceHolderAPI.addPlaceHolder("{friends-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.friends"));
        PlaceHolderAPI.addPlaceHolder("{friendrequests-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.friendrequests"));
        PlaceHolderAPI.addPlaceHolder("{friendsettings-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.friendsettings"));
        PlaceHolderAPI.addPlaceHolder("{bans-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.bans"));
        PlaceHolderAPI.addPlaceHolder("{mutes-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.mutes"));
        PlaceHolderAPI.addPlaceHolder("{kicks-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.kicks"));
        PlaceHolderAPI.addPlaceHolder("{warns-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.warns"));
        PlaceHolderAPI.addPlaceHolder("{punishmentactions-table}", false,
                event -> BungeeUtilisals.getInstance().getConfig().getString("storage.schemas.punishmentactions"));
        PlaceHolderAPI.addPlaceHolder("{user}", true,
                event -> event.getUser().getName());
    }
}