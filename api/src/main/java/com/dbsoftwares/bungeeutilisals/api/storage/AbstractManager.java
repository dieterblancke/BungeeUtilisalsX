package com.dbsoftwares.bungeeutilisals.api.storage;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */


import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.exception.ConnectionException;
import lombok.Getter;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class AbstractManager {

    @Getter
    private static AbstractManager manager;
    @Getter
    private Plugin plugin;
    @Getter
    private StorageType type;

    public AbstractManager(Plugin plugin, StorageType type) {
        manager = this;

        this.plugin = plugin;
        this.type = type;
    }

    public String getName() {
        return type.getName();
    }

    public abstract AbstractConnection getConnection() throws ConnectionException;

    public void initialize() throws Exception {
        switch (type) {
            case MYSQL:
            case POSTGRESQL:
            case MARIADB:
            case SQLITE: {
                try (InputStream is = plugin.getResourceAsStream(type.getSchema())) {
                    if (is == null) {
                        throw new Exception("Could not find schema for " + type.toString() + ": " + type.getSchema() + "!");
                    }
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                         AbstractConnection connection = getConnection(); Statement st = connection.createStatement()) {

                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);

                            if (line.endsWith(";")) {
                                builder.deleteCharAt(builder.length() - 1);

                                String statement = PlaceHolderAPI.formatMessage(builder.toString().trim());
                                if (!statement.isEmpty()) {
                                    st.addBatch(statement);
                                }

                                builder = new StringBuilder();
                            }
                        }
                        st.executeBatch();
                    }
                }
                break;
            }
        }
    }

    public abstract void close() throws SQLException;
}