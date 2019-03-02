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

package com.dbsoftwares.bungeeutilisals.storage.mongodb;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.storage.data.mongo.MongoDao;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;

@Getter
@Setter
public class MongoDBStorageManager extends AbstractStorageManager {

    private MongoClient client;
    private MongoDatabase database;

    public MongoDBStorageManager(Plugin plugin) {
        this(plugin, StorageType.MONGODB, BungeeUtilisals.getInstance().getConfig());
    }

    public MongoDBStorageManager(Plugin plugin, StorageType type, IConfiguration configuration) {
        super(plugin, type, new MongoDao());

        String user = configuration.getString("storage.username");
        String password = configuration.getString("storage.password");
        String database = configuration.getString("storage.database");

        MongoCredential credential = null;
        if (user != null && !user.isEmpty()) {
            credential = MongoCredential.createCredential(user, database,
                    (password == null || password.isEmpty() ? null : password.toCharArray()));
        }
        MongoClientOptions options = MongoClientOptions.builder()
                .applicationName("BungeeUtilisals")
                .connectionsPerHost(configuration.getInteger("storage.pool.max-pool-size"))
                .connectTimeout(configuration.getInteger("storage.pool.connection-timeout") * 1000)
                .maxConnectionLifeTime(configuration.getInteger("storage.pool.max-lifetime") * 1000)
                .build();

        if (credential == null) {
            client = new MongoClient(new ServerAddress(configuration.getString("storage.hostname"),
                    configuration.getInteger("storage.port")), options);
        } else {
            client = new MongoClient(new ServerAddress(configuration.getString("storage.hostname"),
                    configuration.getInteger("storage.port")), credential, options);
        }

        this.database = client.getDatabase(database);
    }

    @Override
    public Connection getConnection() {
        throw new UnsupportedOperationException("MongoDB does not support java.sql.Connection!");
    }

    @Override
    public void close() {
        client.close();
    }

    public MongoClient getClient() {
        return client;
    }

    public MongoDatabase getDatabase() {
        return database;
    }
}
