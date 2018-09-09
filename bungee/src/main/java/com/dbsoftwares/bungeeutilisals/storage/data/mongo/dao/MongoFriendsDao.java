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
