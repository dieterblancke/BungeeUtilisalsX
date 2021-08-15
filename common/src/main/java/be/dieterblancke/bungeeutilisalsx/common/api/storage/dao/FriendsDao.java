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

import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequest;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSetting;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSettings;

import java.util.List;
import java.util.UUID;

public interface FriendsDao
{

    void addFriend( UUID user, UUID uuid );

    void removeFriend( UUID user, UUID uuid );

    List<FriendData> getFriends( UUID uuid );

    long getAmountOfFriends( UUID uuid );

    void addFriendRequest( UUID user, UUID uuid );

    void removeFriendRequest( UUID user, UUID uuid );

    List<FriendRequest> getIncomingFriendRequests( UUID uuid );

    List<FriendRequest> getOutgoingFriendRequests( UUID uuid );

    boolean hasIncomingFriendRequest( UUID user, UUID uuid );

    boolean hasOutgoingFriendRequest( UUID user, UUID uuid );

    void setSetting( UUID uuid, FriendSetting type, Object value );

    <T> T getSetting( UUID uuid, FriendSetting type );

    FriendSettings getSettings( UUID uuid );
}
