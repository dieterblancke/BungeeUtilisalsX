package com.dbsoftwares.bungeeutilisals.bungee.library;

/*
 * Created by DBSoftwares on 16/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public enum Library {

    SQLITE("org.sqlite.JDBC", "http://central.maven.org/maven2/org/xerial/sqlite-jdbc/3.21.0.1/sqlite-jdbc-3.21.0.1.jar"),
    MARIADB("org.mariadb.jdbc.MariaDbDataSource", "http://central.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/2.2.1/mariadb-java-client-2.2.1.jar"),
    POSTGRESQL("org.postgresql.ds.PGSimpleDataSource", "http://repo1.maven.org/maven2/org/postgresql/postgresql/9.4.1212/postgresql-9.4.1212.jar");

    private String className;
    private String downloadURL;

    Library(String className, String downloadURL) {
        this.className = className;
        this.downloadURL = downloadURL;
    }

    public boolean isPresent() {
        return Utils.classFound(className);
    }

    public void load() {
        if (isPresent()) {
            return;
        }
        File path = new File(BungeeUtilisals.getInstance().getDataFolder(), "libs/" + toString().toLowerCase() + ".jar");
        if (!path.getParentFile().exists()) {
            path.getParentFile().mkdirs();
        }

        // Download libary if not present
        if (!path.exists()) {
            BungeeUtilisals.log("Downloading libary for " + toString().toLowerCase());

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
        BungeeUtilisals.log("Loaded libary with name: " + toString().toLowerCase());
    }
}