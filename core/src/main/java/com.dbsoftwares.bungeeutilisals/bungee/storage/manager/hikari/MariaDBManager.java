package com.dbsoftwares.bungeeutilisals.bungee.storage.manager.hikari;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.storage.AbstractConnection;
import com.dbsoftwares.bungeeutilisals.api.storage.StorageType;
import com.dbsoftwares.bungeeutilisals.api.storage.exception.ConnectionException;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.storage.connection.hikari.MariaDBConnection;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.SQLException;

public class MariaDBManager extends HikariManager {

    public MariaDBManager(Plugin plugin) {
        super(plugin, StorageType.MARIADB, BungeeUtilisals.getInstance().getConfig());
    }

    @Override
    protected String getDataSourceClass() {
        return Utils.classFound("org.mariadb.jdbc.MariaDbDataSource") ? "org.mariadb.jdbc.MariaDbDataSource"
                : "org.mariadb.jdbc.MySQLDataSource";
    }

    @Override
    public AbstractConnection getConnection() throws ConnectionException {
        try {
            return new MariaDBConnection(dataSource.getConnection());
        } catch (SQLException e) {
            throw new ConnectionException("Could not create new connection for " + getName(), e);
        }
    }
}