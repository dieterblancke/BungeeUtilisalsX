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
import java.util.concurrent.CompletableFuture;

public interface FriendsDao
{

    CompletableFuture<Void> addFriend( UUID user, UUID uuid );

    CompletableFuture<Void> removeFriend( UUID user, UUID uuid );

    CompletableFuture<List<FriendData>> getFriends( UUID uuid );

    CompletableFuture<Long> getAmountOfFriends( UUID uuid );

    CompletableFuture<Void> addFriendRequest( UUID user, UUID uuid );

    CompletableFuture<Void> removeFriendRequest( UUID user, UUID uuid );

    CompletableFuture<List<FriendRequest>> getIncomingFriendRequests( UUID uuid );

    CompletableFuture<List<FriendRequest>> getOutgoingFriendRequests( UUID uuid );

    CompletableFuture<Boolean> hasIncomingFriendRequest( UUID user, UUID uuid );

    CompletableFuture<Boolean> hasOutgoingFriendRequest( UUID user, UUID uuid );

    CompletableFuture<Void> setSetting( UUID uuid, FriendSetting type, boolean value );

    CompletableFuture<Boolean> getSetting( UUID uuid, FriendSetting type );

    CompletableFuture<FriendSettings> getSettings( UUID uuid );

}
