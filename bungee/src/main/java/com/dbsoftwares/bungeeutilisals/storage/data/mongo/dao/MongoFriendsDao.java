package com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.FriendsDao;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.Mapping;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Lists;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/*
 * Created by DBSoftwares on 30 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class MongoFriendsDao implements FriendsDao {

    @Override
    public void addFriend(User user, UUID uuid) {
        final Mapping<String, Object> mapping = new Mapping<>(true);

        mapping.append("user", user.getUUID());
        mapping.append("friend", uuid);
        mapping.append("friendsince", new Date(System.currentTimeMillis()));

        getDatabase().getCollection(format("{friends-table}")).insertOne(new Document(mapping.getMap()));
    }

    @Override
    public void removeFriend(User user, UUID uuid) {
        final MongoCollection<Document> coll = getDatabase().getCollection(format("{friends-table}"));

        coll.deleteOne(
                Filters.and(
                        Filters.eq("user", user.getUUID()),
                        Filters.eq("friend", uuid)
                )
        );
    }

    @Override
    public List<FriendData> getFriends(UUID uuid) {
        final List<FriendData> friends = Lists.newArrayList();
        final MongoCollection<Document> coll = getDatabase().getCollection(format("{friends-table}"));
        final MongoCollection<Document> userColl = getDatabase().getCollection(format("{users-table}"));

        coll.find(Filters.eq("user", uuid)).forEach((Block<? super Document>) doc -> {
            final Document friend = userColl.find(Filters.eq("user", doc.getString("friend"))).first();

            friends.add(new FriendData(
                    uuid,
                    friend.getString("name"),
                    doc.getDate("friendsince"),
                    friend.getDate("lastlogout")
            ));
        });

        return friends;
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
