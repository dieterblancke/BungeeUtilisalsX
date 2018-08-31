package com.dbsoftwares.bungeeutilisals.api.storage.dao;

import com.dbsoftwares.bungeeutilisals.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;

import java.util.List;
import java.util.UUID;

/*
 * Created by DBSoftwares on 10 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public interface FriendsDao {

    void addFriend(User user, UUID identifier, String name);

    void removeFriend(User user, UUID identifier);

    void removeFriend(User user, String name);

    List<FriendData> getFriends(String user);
}
