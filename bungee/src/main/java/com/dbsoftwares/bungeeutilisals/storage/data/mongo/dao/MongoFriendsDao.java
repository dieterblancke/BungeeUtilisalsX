package com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.FriendsDao;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager;
import com.mongodb.client.MongoDatabase;

import java.util.List;
import java.util.UUID;

/*
 * Created by DBSoftwares on 30 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class MongoFriendsDao implements FriendsDao {

    // TODO: create mongo friends dao, maybe other storage structure?

    @Override
    public void addFriend(User user, UUID uuid) {

    }

    @Override
    public void removeFriend(User user, UUID uuid) {

    }

    @Override
    public List<FriendData> getFriends(UUID uuid) {
        return null;
    }

    private String format(String line) {
        return PlaceHolderAPI.formatMessage(line);
    }

    private String format(String line, Object... replacements) {
        return PlaceHolderAPI.formatMessage(String.format(line, replacements));
    }

    private MongoDatabase getDatabase() {
        return ((MongoDBStorageManager) BungeeUtilisals.getInstance().getDatabaseManagement()).getDatabase();
    }
}
