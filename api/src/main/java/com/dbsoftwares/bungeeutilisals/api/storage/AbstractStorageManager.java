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

package com.dbsoftwares.bungeeutilisals.api.storage;

import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.utils.reflection.ReflectionUtils;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractStorageManager {

    @Getter
    private static AbstractStorageManager manager;

    @Getter
    private Plugin plugin;

    @Getter
    private StorageType type;

    @Getter
    private Dao dao;

    public AbstractStorageManager(Plugin plugin, StorageType type, Dao dao) {
        manager = this;

        this.plugin = plugin;
        this.type = type;
        this.dao = dao;
    }

    public String getName() {
        return type.getName();
    }

    public abstract Connection getConnection() throws SQLException;

    public void initialize() throws Exception {
        if (type.equals(StorageType.MONGODB)) {
            return;
        }
        try (InputStream is = plugin.getResourceAsStream(type.getSchema())) {
            if (is == null) {
                throw new Exception("Could not find schema for " + type.toString() + ": " + type.getSchema() + "!");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                 Connection connection = getConnection(); Statement st = connection.createStatement()) {

                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);

                    if (line.endsWith(";")) {
                        builder.deleteCharAt(builder.length() - 1);

                        String statement = PlaceHolderAPI.formatMessage(builder.toString().trim());
                        if (!statement.isEmpty()) {
                            st.executeUpdate(statement);
                        }

                        builder = new StringBuilder();
                    }
                }
            }
        }
    }

    public abstract void close() throws SQLException;

    public enum StorageType {

        MYSQL(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.storage.hikari.MySQLStorageManager"),
                "MySQL", "schemas/mysql.sql"),
        POSTGRESQL(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.storage.hikari.PostgreSQLStorageManager"),
                "PostgreSQL", "schemas/postgresql.sql"),
        MARIADB(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.storage.hikari.MariaDBStorageManager"),
                "MariaDB", "schemas/mysql.sql"),
        SQLITE(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.storage.file.SQLiteStorageManager"),
                "SQLite", "schemas/sqlite.sql"),
        MONGODB(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager"),
                "MongoDB", null);

        @Getter
        private Class<? extends AbstractStorageManager> manager;
        @Getter
        private String name;
        @Getter
        private String schema;

        StorageType(Class<?> manager, String name, String schema) {
            this.manager = (Class<? extends AbstractStorageManager>) manager;
            this.name = name;
            this.schema = schema;
        }
    }
}