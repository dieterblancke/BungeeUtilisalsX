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

package com.dbsoftwares.bungeeutilisalsx.common.storage.hikari;

import com.dbsoftwares.bungeeutilisalsx.common.api.storage.StorageType;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.zaxxer.hikari.HikariConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLStorageManager extends HikariStorageManager
{

    public MySQLStorageManager()
    {
        super( StorageType.MYSQL, ConfigFiles.CONFIG.getConfig(), getProperties() );
    }

    private static HikariConfig getProperties()
    {
        final String hostname = ConfigFiles.CONFIG.getConfig().getString( "storage.hostname" );
        final int port = ConfigFiles.CONFIG.getConfig().getInteger( "storage.port" );
        final String database = ConfigFiles.CONFIG.getConfig().getString( "storage.database" );
        final String timezone = ConfigFiles.CONFIG.getConfig().getString( "storage.server-timezone" );

        final HikariConfig config = new HikariConfig();
        config.setDriverClassName( "com.mysql.cj.jdbc.Driver" );
        config.setJdbcUrl( "jdbc:mysql://" + hostname + ":" + port + "/" + database + "?serverTimezone=" + timezone );
        config.addDataSourceProperty( "cachePrepStmts", "true" );
        config.addDataSourceProperty( "alwaysSendSetIsolation", "false" );
        config.addDataSourceProperty( "cacheServerConfiguration", "true" );
        config.addDataSourceProperty( "elideSetAutoCommits", "true" );
        config.addDataSourceProperty( "useLocalSessionState", "true" );
        config.addDataSourceProperty( "useServerPrepStmts", "true" );
        config.addDataSourceProperty( "prepStmtCacheSize", "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit", "2048" );
        config.addDataSourceProperty( "cacheCallableStmts", "true" );
        return config;
    }

    @Override
    protected String getDataSourceClass()
    {
        return null;
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return dataSource.getConnection();
    }
}