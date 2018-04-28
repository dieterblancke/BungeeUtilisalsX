package com.dbsoftwares.bungeeutilisals.api.storage;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */


import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.utils.ReflectionUtils;
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
    private DataManager dataManager;

    public AbstractStorageManager(Plugin plugin, StorageType type, DataManager dataManager) {
        manager = this;

        this.plugin = plugin;
        this.type = type;
        this.dataManager = dataManager;
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

        MYSQL(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.storage.hikari.MySQLStorageManager"),
                "MySQL", "schemas/mysql.sql"),
        POSTGRESQL(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.storage.hikari.PostgreSQLStorageManager"),
                "PostgreSQL", "schemas/postgresql.sql"),
        MARIADB(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.storage.hikari.MariaDBStorageManager"),
                "MariaDB", "schemas/mariadb.sql"),
        SQLITE(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.storage.file.SQLiteStorageManager"),
                "SQLite", "schemas/sqlite.sql"),
        MONGODB(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.storage.mongodb.MongoDBStorageManager"),
                "MongoDB", null);

        @Getter
        private Class<? extends AbstractStorageManager> manager;
        @Getter
        private String name;
        @Getter
        private String schema;

        @SuppressWarnings("unchecked")
            // We know the classes are always an instance of AbstractStorageManager
        StorageType(Class<?> manager, String name, String schema) {
            this.manager = (Class<? extends AbstractStorageManager>) manager;
            this.name = name;
            this.schema = schema;
        }
    }
}