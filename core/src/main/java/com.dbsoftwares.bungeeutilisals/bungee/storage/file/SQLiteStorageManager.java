package com.dbsoftwares.bungeeutilisals.bungee.storage.file;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteStorageManager extends AbstractStorageManager {

    private Connection connection;
    private File database;

    public SQLiteStorageManager(Plugin plugin) throws SQLException {
        super(plugin, StorageType.SQLITE);

        database = new File(BungeeUtilisals.getInstance().getDataFolder(), "data.db");
        if (!database.exists()) {
            try {
                database.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        initializeConnection();
    }

    private void initializeConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");

            connection = DriverManager.getConnection("jdbc:sqlite:" + database.getPath());
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // should never occur | library loaded before
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        // if connection timed out / closed for some reason -> reopening
        if (connection.isClosed()) {
            initializeConnection();
        }
        return connection;
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}