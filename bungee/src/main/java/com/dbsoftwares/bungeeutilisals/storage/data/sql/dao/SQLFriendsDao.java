package com.dbsoftwares.bungeeutilisals.storage.data.sql.dao;

import com.dbsoftwares.bungeeutilisals.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.FriendsDao;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;

import java.util.List;
import java.util.UUID;

/*
 * Created by DBSoftwares on 30 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */
public class SQLFriendsDao implements FriendsDao {

    @Override
    public void addFriend(User user, UUID uuid, String s) {

    }

    @Override
    public void removeFriend(User user, UUID uuid) {

    }

    @Override
    public void removeFriend(User user, String s) {

    }

    @Override
    public List<FriendData> getFriends(String s) {
        return null;
    }
}
