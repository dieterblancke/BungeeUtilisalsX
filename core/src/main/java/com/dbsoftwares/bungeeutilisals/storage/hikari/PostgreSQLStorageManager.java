package com.dbsoftwares.bungeeutilisals.storage.hikari;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgreSQLStorageManager extends HikariStorageManager {

    public PostgreSQLStorageManager(Plugin plugin) {
        super(plugin, StorageType.POSTGRESQL, BungeeUtilisals.getInstance().getConfig(), null);
    }

    @Override
    protected String getDataSourceClass() {
        return "org.postgresql.ds.PGSimpleDataSource";
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}