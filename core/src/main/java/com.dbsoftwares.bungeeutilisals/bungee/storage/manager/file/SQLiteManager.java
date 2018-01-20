package com.dbsoftwares.bungeeutilisals.bungee.storage.manager.file;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.storage.AbstractConnection;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractManager;
import com.dbsoftwares.bungeeutilisals.api.storage.StorageType;
import com.dbsoftwares.bungeeutilisals.api.storage.exception.ConnectionException;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.storage.connection.file.SQLiteConnection;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteManager extends AbstractManager {

    AbstractConnection connection;

    public SQLiteManager(Plugin plugin) throws ConnectionException {
        super(plugin, StorageType.SQLITE);

        File database = new File(BungeeUtilisals.getInstance().getDataFolder(), "data.db");
        if (!database.exists()) {
            try {
                database.createNewFile();
            } catch (IOException e) {
                throw new ConnectionException("Could not create SQLite connection.", e);
            }
        }
        try {
            Class.forName("org.sqlite.JDBC");

            try {
                connection = new SQLiteConnection(DriverManager.getConnection("jdbc:sqlite:" + database.getPath()));
            } catch (SQLException e) {
                throw new ConnectionException("Could not create SQLite connection.", e);
            }
        } catch (ClassNotFoundException e) {
            throw new ConnectionException("Could not create SQLite connection.", e);
        }
    }

    @Override
    public AbstractConnection getConnection() {
        return connection;
    }

    @Override
    public void close() {
        connection.close();
    }
}