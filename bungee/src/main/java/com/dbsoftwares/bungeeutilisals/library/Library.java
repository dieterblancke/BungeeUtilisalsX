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
import com.google.common.collect.Lists;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Collection;
import java.util.List;

public enum Library {

    SQLITE(
            "org.sqlite.JDBC",
            "http://central.maven.org/maven2/org/xerial/sqlite-jdbc/{version}/sqlite-jdbc-{version}.jar",
            "3.23.1",
            checkType("SQLITE")
    ),
    MARIADB(
            "org.mariadb.jdbc.MariaDbDataSource",
            "http://central.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/{version}/mariadb-java-client-{version}.jar",
            "2.3.0",
            checkType("MARIADB")
    ),
    POSTGRESQL(
            "org.postgresql.ds.PGSimpleDataSource",
            "http://central.maven.org/maven2/org/postgresql/postgresql/{version}/postgresql-{version}.jar",
            "42.2.5",
            checkType("POSTGRESQL")
    ),
    MONGODB(
            "com.mongodb.MongoClient",
            "http://central.maven.org/maven2/org/mongodb/mongo-java-driver/{version}/mongo-java-driver-{version}.jar",
            "3.8.2",
            checkType("MONGODB")
    ),
    SLF4J(
            "org.slf4j.LogginFactory",
            "http://central.maven.org/maven2/org/slf4j/slf4j-api/{version}/slf4j-api-{version}.jar",
            "1.7.25",
            checkType("MYSQL", "MARIADB", "POSTGRESQL")
    ),
    HIKARIDB(
            "com.zaxxer.hikari.HikariDataSource",
            "http://central.maven.org/maven2/com/zaxxer/HikariCP/{version}/HikariCP-{version}.jar",
            "3.2.0",
            checkType("MYSQL", "MARIADB", "POSTGRESQL")
    );

    private final String className;
    private final String downloadURL;
    private final String version;
    private final boolean load;

    Library(String className, String downloadURL, String version, boolean load) {
        this.className = className;
        this.downloadURL = downloadURL.replace("{version}", version);
        this.version = version;
        this.load = load;
    }

    private static boolean checkType(String... types) {
        for (String type : types) {
            if (BungeeUtilisals.getInstance().getConfig().getString("storage.type").equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
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
        final File folder = new File(BungeeUtilisals.getInstance().getDataFolder(), "libraries");
        if (!folder.exists()) {
            folder.mkdir();
        }

        final String name = toString();
        final File path = new File(folder, String.format("%s-v%s.jar", name.toLowerCase(), version));

        // Download libary if not present
        if (!path.exists()) {
            BUCore.log("Downloading libary for " + toString());

            try (final InputStream input = new URL(downloadURL).openStream();
                 final ReadableByteChannel channel = Channels.newChannel(input);
                 final FileOutputStream output = new FileOutputStream(path)) {

                output.getChannel().transferFrom(channel, 0, Long.MAX_VALUE);
                BUCore.log("Successfully downloaded libary for " + toString());

                BUCore.log("Removing older versions of " + toString());
                getOutdatedFiles(folder).forEach(File::delete);
                BUCore.log("Successfully removed older versions of " + toString());
            } catch (IOException e) {
                throw new RuntimeException("Failed downloading library for " + toString().toLowerCase(), e);
            }
        }

        BungeeUtilisals.getInstance().getJarClassLoader().loadJar(path);
        BUCore.log("Loaded " + name + " libary!");
    }

    private Collection<File> getOutdatedFiles(final File folder) {
        final List<File> outdatedFiles = Lists.newArrayList();
        final String name = toString().toLowerCase();

        for (File library : folder.listFiles()) {
            final String jarName = library.getName();

            if (jarName.startsWith(name) && !jarName.equals(String.format("%s-v%s.jar", name.toLowerCase(), version))) {
                outdatedFiles.add(library);
            }
        }

        return outdatedFiles;
    }
}