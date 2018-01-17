package com.dbsoftwares.bungeeutilisals.bungee.storage.manager.hikari;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.storage.AbstractConnection;
import com.dbsoftwares.bungeeutilisals.api.storage.StorageType;
import com.dbsoftwares.bungeeutilisals.api.storage.exception.ConnectionException;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.storage.connection.hikari.MySQLConnection;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.SQLException;

public class MySQLManager extends HikariManager {

    public MySQLManager(Plugin plugin) {
        super(plugin, StorageType.MYSQL, BungeeUtilisals.getInstance().getConfig());
    }

    @Override
    protected String getDataSourceClass() {
        return "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
    }

    @Override
    public AbstractConnection getConnection() throws ConnectionException {
        try {
            return new MySQLConnection(dataSource.getConnection());
        } catch (SQLException e) {
            throw new ConnectionException("Could not create new connection for " + getName(), e);
        }
    }
}