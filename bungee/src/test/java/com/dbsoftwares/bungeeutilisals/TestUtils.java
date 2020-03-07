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

package com.dbsoftwares.bungeeutilisals;

import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.utils.APIHandler;

public class TestUtils
{

    public static void setup()
    {
        APIHandler.registerProvider( new MockBUtilisalsAPI() );
        setupPlaceHolders();
    }

    private static void setupPlaceHolders()
    {
        PlaceHolderAPI.addPlaceHolder( "{users-table}", false, event -> "users" );
        PlaceHolderAPI.addPlaceHolder( "{ignoredusers-table}", false, event -> "ignoredusers" );
        PlaceHolderAPI.addPlaceHolder( "{friends-table}", false, event -> "friends" );
        PlaceHolderAPI.addPlaceHolder( "{friendrequests-table}", false, event -> "friendrequests" );
        PlaceHolderAPI.addPlaceHolder( "{friendsettings-table}", false, event -> "friendsettings" );
        PlaceHolderAPI.addPlaceHolder( "{bans-table}", false, event -> "bans" );
        PlaceHolderAPI.addPlaceHolder( "{mutes-table}", false, event -> "mutes" );
        PlaceHolderAPI.addPlaceHolder( "{kicks-table}", false, event -> "kicks" );
        PlaceHolderAPI.addPlaceHolder( "{warns-table}", false, event -> "warns" );
        PlaceHolderAPI.addPlaceHolder( "{punishmentactions-table}", false, event -> "punishmentactions" );
        PlaceHolderAPI.addPlaceHolder( "{reports-table}", false, event -> "reports" );
        PlaceHolderAPI.addPlaceHolder( "{messagequeue-table}", false, event -> "messagequeue" );
    }
}
