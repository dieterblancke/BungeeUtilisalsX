package com.dbsoftwares.bungeeutilisals.bungee.storage.connection.file;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.bungee.storage.StorageType;
import com.dbsoftwares.bungeeutilisals.bungee.storage.connection.AbstractConnection;

import java.sql.ResultSet;

public class SQLiteConnection extends AbstractConnection {

    public SQLiteConnection() {
        super(StorageType.SQLITE);
    }

    @Override
    public void createTable(Object table) {

    }

    @Override
    public void executeInsert(Object table) {

    }

    @Override
    public ResultSet executeQuery(String statement, String... replacements) {
        return null;
    }

    @Override
    public void executeUpdate(String statement, String replacements) {

    }
}
