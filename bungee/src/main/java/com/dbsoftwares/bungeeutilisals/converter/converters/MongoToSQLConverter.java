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

package com.dbsoftwares.bungeeutilisals.converter.converters;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.converter.Converter;
import com.dbsoftwares.bungeeutilisals.importer.ImporterCallback;
import com.dbsoftwares.bungeeutilisals.storage.data.mongo.MongoDao;
import com.google.common.collect.Maps;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.md_5.bungee.api.plugin.Plugin;
import org.bson.Document;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class MongoToSQLConverter extends Converter {

    private static final String ERROR_STRING = "An error occured: ";

    @Override
    protected void importData(final ImporterCallback<ConverterStatus> importerCallback, final Map<String, String> properties) {
        final MongoDBStorageManager storageManager = new MongoDBStorageManager(BungeeUtilisals.getInstance(), AbstractStorageManager.StorageType.MONGODB, properties);
        final Map<String, MongoCollection<Document>> collections = Maps.newHashMap();

        collections.put("users", storageManager.getDatabase().getCollection(PlaceHolderAPI.formatMessage("{users-table}")));
        for (PunishmentType type : PunishmentType.values()) {
            collections.put(type.toString(), storageManager.getDatabase().getCollection(PlaceHolderAPI.formatMessage(type.getTablePlaceHolder())));
        }

        final long count = collections.values().stream().mapToLong(MongoCollection::countDocuments).sum();
        status = new ConverterStatus(count);

        try (final Connection connection = BUCore.getApi().getStorageManager().getConnection()) {
            connection.setAutoCommit(false);

            for (Document document : collections.get("users").find()) {
                createUser(connection, document);
                status.incrementConvertedEntries(1);
                importerCallback.onStatusUpdate(status);

                if (status.getConvertedEntries() % 100 == 0) {
                    connection.commit();
                }
            }
            connection.commit();
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
        for (PunishmentType type : PunishmentType.values()) {
            try (final Connection connection = BUCore.getApi().getStorageManager().getConnection()) {
                connection.setAutoCommit(false);

                for (Document document : collections.get(type.toString()).find()) {
                    createPunishment(connection, type, document);
                    status.incrementConvertedEntries(1);
                    importerCallback.onStatusUpdate(status);

                    if (status.getConvertedEntries() % 100 == 0) {
                        connection.commit();
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                BUCore.getLogger().error(ERROR_STRING, e);
            }
        }
    }

    private void createUser(Connection connection, Document document) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                PlaceHolderAPI.formatMessage("INSERT INTO {users-table}(uuid, username, ip, language, firstlogin, lastlogout) VALUES (?, ?, ?, ?, ?, ?);")
        )) {
            preparedStatement.setString(1, document.getString("uuid"));
            preparedStatement.setString(2, document.getString("username"));
            preparedStatement.setString(3, document.getString("ip"));
            preparedStatement.setString(4, document.getString("language"));
            preparedStatement.setString(5, Dao.formatDateToString(document.getDate("firstlogin")));
            preparedStatement.setString(6, Dao.formatDateToString(document.getDate("lastlogout")));

            preparedStatement.executeUpdate();
        }
    }

    private void createPunishment(Connection connection, PunishmentType type, Document document) throws SQLException {
        if (!type.isActivatable()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    PlaceHolderAPI.formatMessage("INSERT INTO " + type.getTablePlaceHolder() + "(uuid, user, ip, reason, server, date, executed_by) VALUES (?, ?, ?, ?, ?, ?, ?);")
            )) {
                preparedStatement.setString(1, document.getString("uuid"));
                preparedStatement.setString(2, document.getString("user"));
                preparedStatement.setString(3, document.getString("ip"));
                preparedStatement.setString(4, document.getString("reason"));
                preparedStatement.setString(5, document.getString("server"));
                preparedStatement.setString(6, Dao.formatDateToString(document.getDate("date")));
                preparedStatement.setString(7, document.getString("executed_by"));

                preparedStatement.executeUpdate();
            }
        } else if (type.isTemporary()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    PlaceHolderAPI.formatMessage("INSERT INTO " + type.getTablePlaceHolder() + "(uuid, user, ip, time, reason, server, date, active, executed_by, removed_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);")
            )) {
                preparedStatement.setString(1, document.getString("uuid"));
                preparedStatement.setString(2, document.getString("user"));
                preparedStatement.setString(3, document.getString("ip"));
                preparedStatement.setLong(4, document.getLong("time"));
                preparedStatement.setString(5, document.getString("reason"));
                preparedStatement.setString(6, document.getString("server"));
                preparedStatement.setString(7, Dao.formatDateToString(document.getDate("date")));
                preparedStatement.setBoolean(8, document.getBoolean("active"));
                preparedStatement.setString(9, document.getString("executed_by"));
                preparedStatement.setString(10, document.getString("removed_by"));

                preparedStatement.executeUpdate();
            }
        } else {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    PlaceHolderAPI.formatMessage("INSERT INTO " + type.getTablePlaceHolder() + "(uuid, user, ip, reason, server, date, active, executed_by, removed_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);")
            )) {
                preparedStatement.setString(1, document.getString("uuid"));
                preparedStatement.setString(2, document.getString("user"));
                preparedStatement.setString(3, document.getString("ip"));
                preparedStatement.setString(4, document.getString("reason"));
                preparedStatement.setString(5, document.getString("server"));
                preparedStatement.setString(6, Dao.formatDateToString(document.getDate("date")));
                preparedStatement.setBoolean(7, document.getBoolean("active"));
                preparedStatement.setString(8, document.getString("executed_by"));
                preparedStatement.setString(9, document.getString("removed_by"));

                preparedStatement.executeUpdate();
            }
        }
    }

    public class MongoDBStorageManager extends AbstractStorageManager {
        private MongoClient client;
        private MongoDatabase database;

        public MongoDBStorageManager(Plugin plugin, AbstractStorageManager.StorageType type, Map<String, String> properties) {
            super(plugin, type, new MongoDao());

            String user = properties.get("username");
            String password = properties.get("password");
            String database = properties.get("database");

            MongoCredential credential = null;
            if (user != null && !user.isEmpty()) {
                credential = MongoCredential.createCredential(user, database,
                        (password == null || password.isEmpty() ? null : password.toCharArray()));
            }
            MongoClientOptions options = MongoClientOptions.builder().applicationName("BungeeUtilisals").build();

            if (credential == null) {
                client = new MongoClient(new ServerAddress(properties.get("host"), Integer.parseInt(properties.get("port"))), options);
            } else {
                client = new MongoClient(new ServerAddress(properties.get("host"), Integer.parseInt(properties.get("port"))), credential, options);
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
}
