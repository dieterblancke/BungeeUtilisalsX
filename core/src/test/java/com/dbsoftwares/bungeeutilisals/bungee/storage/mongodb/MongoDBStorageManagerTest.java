package com.dbsoftwares.bungeeutilisals.bungee.storage.mongodb;

import com.dbsoftwares.bungeeutilisals.api.configuration.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/*
 * Created by DBSoftwares on 12/03/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class MongoDBStorageManagerTest {

    @Test
    public void testConnection() {
        try {
            IConfiguration config = IConfiguration.loadYamlConfiguration(getClass().getResourceAsStream("/mongo/settings.yml"));
            MongoDBStorageManager manager = new MongoDBStorageManager(null, AbstractStorageManager.StorageType.MONGODB, config);

            if (manager.getClient() == null || manager.getDatabase() == null) {
                System.out.println("Something went wrong while connecting!");
                return;
            }

            MongoCollection<Document> userColl = manager.getDatabase().getCollection("test_" + config.getString("storage.schema.users"));
            userColl.drop(); // clearing data for tests to work ...
            assertEquals((long) 0, userColl.count());

            userColl.insertOne(new Document(getUserMapping()));
            assertEquals((long) 1, userColl.count());
            assertTrue(userColl.find().first().toJson().endsWith(", \"uuid\" : \"50193e7f-6af9-36c0-9e5c-ca826cecd860\", \"username\" : \"didjee2\", \"ip\" : \"127.0.0.1\", \"language\" : \"en_US\" }"));

            userColl.deleteOne(Filters.eq("uuid", "50193e7f-6af9-36c0-9e5c-ca826cecd860"));
            assertEquals((long) 0, userColl.count());

            List<Document> documentList = Lists.newArrayList();
            for (int i = 0; i < 100; i++) {
                documentList.add(new Document(getRandomUserMapping()));
            }

            userColl.insertMany(documentList);
            assertEquals((long) 100, userColl.count());

            userColl.deleteMany(Filters.eq("username", "didjee2"));
            assertEquals((long) 0, userColl.count());
        } catch (Exception e) {
            // cancelling test, connection fail??
            System.out.println("Something went wrong while connecting: " + e.getMessage());
        }
    }

    private Map<String, Object> getUserMapping() {
        LinkedHashMap<String, Object> mapping = new LinkedHashMap<>();

        mapping.put("uuid", "50193e7f-6af9-36c0-9e5c-ca826cecd860");
        mapping.put("username", "didjee2");
        mapping.put("ip", "127.0.0.1");
        mapping.put("language", "en_US");

        return mapping;
    }

    private Map<String, Object> getRandomUserMapping() {
        LinkedHashMap<String, Object> mapping = new LinkedHashMap<>();

        mapping.put("uuid", UUID.randomUUID().toString());
        mapping.put("username", "didjee2");
        mapping.put("ip", "127.0.0.1");
        mapping.put("language", "en_US");

// testing map in map structure | working
//        Map<String, Object> settingsMap = Maps.newHashMap();
//        settingsMap.put("key1", "value1");
//        settingsMap.put("key2", 2);
//        settingsMap.put("key3", "value3");
//
//        mapping.put("settingsMap", settingsMap);

        return mapping;
    }
}