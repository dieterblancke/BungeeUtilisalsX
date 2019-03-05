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

package com.dbsoftwares.bungeeutilisals.api.storage.dao;

import com.dbsoftwares.bungeeutilisals.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendRequest;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;

import java.util.List;
import java.util.UUID;

public interface FriendsDao {

    void addFriend(User user, UUID uuid);

    void removeFriend(User user, UUID uuid);

    List<FriendData> getFriends(UUID uuid);

    void addFriendRequest(User user, UUID uuid);

    void removeFriendRequest(User user, UUID uuid);

    List<FriendRequest> getIncomingFriendRequests(UUID uuid);

    List<FriendRequest> getOutgoingFriendRequests(UUID uuid);

    boolean hasIncomingFriendRequest(User user, UUID uuid);

    boolean hasOutgoingFriendRequest(User user, UUID uuid);
}
