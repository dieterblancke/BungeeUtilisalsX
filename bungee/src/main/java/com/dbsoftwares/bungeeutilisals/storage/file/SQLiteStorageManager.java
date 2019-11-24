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

package com.dbsoftwares.bungeeutilisals.storage.file;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.storage.data.sql.SQLDao;
import net.md_5.bungee.api.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteStorageManager extends AbstractStorageManager
{

    private Connection connection;
    private File database;

    public SQLiteStorageManager( Plugin plugin ) throws SQLException
    {
        super( plugin, StorageType.SQLITE, new SQLDao() );

        database = new File( BungeeUtilisals.getInstance().getDataFolder(), "data.db" );

        try
        {
            if ( !database.exists() && !database.createNewFile() )
            {
                return;
            }
        }
        catch ( IOException e )
        {
            BUCore.getLogger().error( "An error occured: ", e );
        }

        initializeConnection();
    }

    private void initializeConnection() throws SQLException
    {
        try
        {
            Class.forName( "org.sqlite.JDBC" );

            connection = DriverManager.getConnection( "jdbc:sqlite:" + database.getPath() );
        }
        catch ( ClassNotFoundException e )
        {
            // should never occur | library loaded before
            BUCore.getLogger().error( "An error occured: ", e );
        }
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        if ( connection.isClosed() )
        {
            initializeConnection();
        }
        return connection;
    }

    @Override
    public void close() throws SQLException
    {
        connection.close();
    }
}