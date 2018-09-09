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

package com.dbsoftwares.bungeeutilisals.storage.hikari;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.zaxxer.hikari.HikariConfig;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLStorageManager extends HikariStorageManager {

    public MySQLStorageManager(Plugin plugin) {
        super(plugin, StorageType.MYSQL, BungeeUtilisals.getInstance().getConfig(), getProperties());
    }

    private static HikariConfig getProperties() {
        HikariConfig config = new HikariConfig();
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("alwaysSendSetIsolation", "false");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("cacheCallableStmts", "true");
        return config;
    }

    @Override
    protected String getDataSourceClass() {
        return "com.mysql.jdbc.jdbc2.optional.MysqlDataSource";
    }

    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}