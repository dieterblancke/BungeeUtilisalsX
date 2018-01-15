package com.dbsoftwares.bungeeutilisals.bungee.storage.connection.hikari;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.bungee.storage.StorageFactory;
import com.dbsoftwares.bungeeutilisals.bungee.storage.StorageType;
import com.dbsoftwares.bungeeutilisals.bungee.storage.connection.AbstractConnection;
import com.zaxxer.hikari.pool.ProxyConnection;
import lombok.Getter;

import java.sql.ResultSet;

public class MySQLConnection extends AbstractConnection {

    @Getter
    private ProxyConnection connection;

    public MySQLConnection() {
        super(StorageType.MYSQL);

        connection = StorageFactory.getConnection();
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

    @Override
    protected void finalize() throws Throwable {
        super.finalize();

        connection.close();
    }
}
