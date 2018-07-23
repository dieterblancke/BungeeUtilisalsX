package com.dbsoftwares.bungeeutilisals.storage.mongodb;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;

@EqualsAndHashCode(callSuper = true)
@Data
public class MongoDBStorageManager extends AbstractStorageManager {

    private MongoClient client;
    private MongoDatabase database;

    public MongoDBStorageManager(Plugin plugin) {
        this(plugin, StorageType.MONGODB, BungeeUtilisals.getInstance().getConfig());
    }

    public MongoDBStorageManager(Plugin plugin, StorageType type, IConfiguration configuration) {
        super(plugin, type, new MongoDataManager());

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
