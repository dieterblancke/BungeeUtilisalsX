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

package com.dbsoftwares.bungeeutilisals.library;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public enum Library {

    SQLITE(
            "org.sqlite.JDBC",
            "http://central.maven.org/maven2/org/xerial/sqlite-jdbc/3.23.1/sqlite-jdbc-3.23.1.jar",
            checkType("SQLITE")
    ),
    MARIADB(
            "org.mariadb.jdbc.MariaDbDataSource",
            "http://central.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/2.2.5/mariadb-java-client-2.2.5.jar",
            checkType("MARIADB")
    ),
    POSTGRESQL(
            "org.postgresql.ds.PGSimpleDataSource",
            "http://central.maven.org/maven2/org/postgresql/postgresql/42.2.4/postgresql-42.2.4.jar",
            checkType("POSTGRESQL")
    ),
    MONGODB(
            "com.mongodb.MongoClient",
            "http://central.maven.org/maven2/org/mongodb/mongo-java-driver/3.8.0/mongo-java-driver-3.8.0.jar",
            checkType("MONGODB")
    ),
    SLF4J(
            "org.slf4j.LogginFactory",
            "http://central.maven.org/maven2/org/slf4j/slf4j-api/1.7.25/slf4j-api-1.7.25.jar",
            checkType("MYSQL", "MARIADB", "POSTGRESQL")
    ),
    HIKARIDB(
            "com.zaxxer.hikari.HikariDataSource",
            "http://central.maven.org/maven2/com/zaxxer/HikariCP/3.2.0/HikariCP-3.2.0.jar",
            checkType("MYSQL", "MARIADB", "POSTGRESQL")
    );

    private String className;
    private String downloadURL;
    private boolean load;

    Library(String className, String downloadURL, boolean load) {
        this.className = className;
        this.downloadURL = downloadURL;
        this.load = load;
    }

    public boolean shouldBeLoaded() {
        return load;
    }

    public boolean isPresent() {
        return Utils.classFound(className);
    }

    public void load() {
        if (isPresent()) {
            return;
        }
        File path = new File(BungeeUtilisals.getInstance().getDataFolder(), "libraries/" + toString().toLowerCase() + ".jar");
        if (!path.getParentFile().exists()) {
            path.getParentFile().mkdirs();
        }

        // Download libary if not present
        if (!path.exists()) {
            BUCore.log("Downloading libary for " + toString());

            InputStream input = null;
            FileOutputStream output = null;
            ReadableByteChannel channel = null;
            try {
                input = new URL(downloadURL).openStream();
                channel = Channels.newChannel(input);
                output = new FileOutputStream(path);

                output.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
            } catch (IOException e) {
                throw new RuntimeException("Failed downloading library for " + toString().toLowerCase(), e);
            } finally {
                if (channel != null) {
                    try {
                        channel.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (input != null) {
                    try {
                        input.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (output != null) {
                    try {
                        output.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        BungeeUtilisals.getInstance().getLibraryClassLoader().loadLibrary(path);
        BUCore.log("Loaded " + toString() + " libary!");
    }

    private static boolean checkType(String... types) {
        for (String type : types) {
            if (BungeeUtilisals.getInstance().getConfig().getString("storage.type").equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
}