package com.dbsoftwares.bungeeutilisals.friends;

/*
 * Created by DBSoftwares on 04/05/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisals.api.friends.IFriendManager;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;

import java.util.Collection;
import java.util.UUID;

public class FriendManager implements IFriendManager {

    @Override
    public void addFriend(User user, UUID identifier, String name) {

    }

    @Override
    public void removeFriend(User user, UUID identifier) {

    }

    @Override
    public void removeFriend(User user, String name) {

    }

    @Override
    public Collection<FriendData> getFriends(String user) {
        return null;
    }
}