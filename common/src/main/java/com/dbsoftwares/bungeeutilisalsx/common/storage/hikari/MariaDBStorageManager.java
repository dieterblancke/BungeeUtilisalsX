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
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.Utils;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.zaxxer.hikari.HikariConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class MariaDBStorageManager extends HikariStorageManager
{

    public MariaDBStorageManager()
    {
        super( StorageType.MARIADB, ConfigFiles.CONFIG.getConfig(), getProperties() );
    }

    private static HikariConfig getProperties()
    {
        return new HikariConfig();
    }

    @Override
    protected String getDataSourceClass()
    {
        return Utils.classFound( "org.mariadb.jdbc.MariaDbDataSource" ) ? "org.mariadb.jdbc.MariaDbDataSource"
                : "org.mariadb.jdbc.MySQLDataSource";
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return dataSource.getConnection();
    }
}