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
import com.dbsoftwares.bungeeutilisals.bungee.storage.connection.hikari.PostgreSQLConnection;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.SQLException;

public class PostgreSQLManager extends HikariManager {

    public PostgreSQLManager(Plugin plugin) {
        super(plugin, StorageType.POSTGRESQL, BungeeUtilisals.getInstance().getConfig());
    }

    @Override
    protected String getDataSourceClass() {
        return Utils.classFound("org.postgresql.ds.PGSimpleDataSource") ? "org.postgresql.ds.PGSimpleDataSource"
                : "com.impossibl.postgres.jdbc.PGDataSource";
    }

    @Override
    public AbstractConnection getConnection() throws ConnectionException {
        try {
            return new PostgreSQLConnection(dataSource.getConnection());
        } catch (SQLException e) {
            throw new ConnectionException("Could not create new connection for " + getName(), e);
        }
    }
}