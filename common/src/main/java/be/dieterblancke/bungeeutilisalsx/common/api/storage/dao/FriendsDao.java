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
