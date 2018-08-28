package com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.UserDao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.Mapping;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/*
 * Created by DBSoftwares on 15 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class MongoUserDao implements UserDao {

    @Override
    public void createUser(UUID uuid, String username, String ip, Language language) {
        createUser(uuid.toString(), username, ip, language);
    }


    @Override
    public void createUser(String uuid, String username, String ip, Language language) {
        Mapping<String, Object> mapping = new Mapping<>(true);
        Date date = new Date(System.currentTimeMillis());

        mapping.append("uuid", uuid)
                .append("username", username)
                .append("ip", ip)
                .append("language", language.getName())
                .append("firstlogin", date)
                .append("lastlogout", date);

        getDatabase().getCollection(format("{users-table}")).insertOne(new Document(mapping.getMap()));
    }

    @Override
    public void updateUser(UUID uuid, String name, String ip, Language language, Date date) {
        List<Bson> updates = Lists.newArrayList();

        updates.add(Updates.set("name", name));
        updates.add(Updates.set("ip", ip));
        updates.add(Updates.set("language", language.getName()));
        updates.add(Updates.set("lastlogout", date));

        getDatabase().getCollection(format("{users-table}"))
                .findOneAndUpdate(Filters.eq("uuid", uuid.toString()), Updates.combine(updates));
    }

    @Override
    public boolean exists(String name) {
        if (name.contains(".")) {
            return getDatabase().getCollection(format("{users-table}"))
                    .find().iterator().hasNext();
        }
        return getDatabase().getCollection(format("{users-table}")).find(
                name.contains(".") ? Filters.eq("ip", name) : Filters.eq("name", name)
        ).iterator().hasNext();
    }

    @Override
    public boolean exists(UUID uuid) {
        return getDatabase().getCollection(format("{users-table}"))
                .find(Filters.eq("uuid", uuid.toString())).iterator().hasNext();
    }

    @Override
    public UserStorage getUserData(UUID uuid) {
        UserStorage storage = new UserStorage();

        Document document = getDatabase().getCollection(format("{users-table}"))
                .find(Filters.eq("uuid", uuid.toString())).first();

        if (document != null) {
            storage.setUuid(uuid);
            storage.setUserName(document.getString("username"));
            storage.setIp(document.getString("ip"));
            storage.setLanguage(
                    BUCore.getApi().getLanguageManager().getLangOrDefault(document.getString("language"))
            );
            storage.setFirstLogin(document.getDate("lastlogin"));
            storage.setLastLogout(document.getDate("lastlogout"));
        }

        return storage;
    }

    @Override
    public UserStorage getUserData(String name) {
        UserStorage storage = new UserStorage();

        Document document = getDatabase().getCollection(format("{users-table}"))
                .find(Filters.eq("username", name)).first();

        if (document != null) {
            storage.setUuid(UUID.fromString(document.getString("uuid")));
            storage.setUserName(name);
            storage.setIp(document.getString("ip"));
            storage.setLanguage(
                    BUCore.getApi().getLanguageManager().getLangOrDefault(document.getString("language"))
            );
            storage.setFirstLogin(document.getDate("lastlogin"));
            storage.setLastLogout(document.getDate("lastlogout"));
        }

        return storage;
    }

    @Override
    public List<String> getUsersOnIP(String ip) {
        List<String> users = Lists.newArrayList();

        for (Document document : getDatabase().getCollection(format("{users-table}")).find(Filters.eq("ip", ip))) {
            users.add(document.getString("username"));
        }

        return users;
    }

    @Override
    public Language getLanguage(UUID uuid) {
        Language language = null;

        Document document = getDatabase().getCollection(format("{users-table}")).find(Filters.eq("uuid", uuid.toString())).first();

        if (document != null) {
            language = BUCore.getApi().getLanguageManager().getLangOrDefault(document.getString("language"));
        }

        return language;
    }

    @Override
    public void setName(UUID uuid, String name) {
        getDatabase().getCollection(format("{users-table}"))
                .findOneAndUpdate(Filters.eq("uuid", uuid.toString()), Updates.set("name", name));
    }

    @Override
    public void setIP(UUID uuid, String ip) {
        getDatabase().getCollection(format("{users-table}"))
                .findOneAndUpdate(Filters.eq("uuid", uuid.toString()), Updates.set("ip", ip));
    }

    @Override
    public void setLanguage(UUID uuid, Language language) {
        getDatabase().getCollection(format("{users-table}"))
                .findOneAndUpdate(Filters.eq("uuid", uuid.toString()), Updates.set("language", language.getName()));
    }

    @Override
    public void setLogout(UUID uuid, Date logout) {
        getDatabase().getCollection(format("{users-table}"))
                .findOneAndUpdate(Filters.eq("uuid", uuid.toString()), Updates.set("lastlogout", logout));
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
