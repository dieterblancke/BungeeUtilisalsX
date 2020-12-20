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

package be.dieterblancke.bungeeutilisalsx.common.storage.file;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.StorageType;
import be.dieterblancke.bungeeutilisalsx.common.storage.data.sql.SQLDao;
import be.dieterblancke.bungeeutilisalsx.common.storage.sql.SQLStorageManager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLiteStorageManager extends SQLStorageManager
{

    private final File database;
    private UnclosableConnection connection;

    public SQLiteStorageManager() throws SQLException
    {
        this( new File( BuX.getInstance().getDataFolder(), "data.db" ) );
    }

    public SQLiteStorageManager( final File database ) throws SQLException
    {
        super( StorageType.SQLITE, new SQLDao() );
        this.database = database;

        try
        {
            if ( database != null && !database.exists() && !database.createNewFile() )
            {
                return;
            }
        }
        catch ( IOException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        try
        {
            Class.forName( "org.sqlite.JDBC" );
        }
        catch ( ClassNotFoundException e )
        {
            throw new RuntimeException( e );
        }
        initializeConnection();
    }

    private UnclosableConnection initializeConnection() throws SQLException
    {
        return UnclosableConnection.wrap( DriverManager.getConnection(
                database == null
                        ? "jdbc:sqlite::memory:"
                        : "jdbc:sqlite:" + database.getPath()
        ) );
    }

    @Override
    public synchronized Connection getConnection() throws SQLException
    {
        if ( connection == null || connection.isClosed() )
        {
            connection = initializeConnection();
        }

        if ( connection == null || connection.isClosed() )
        {
            throw new SQLException( "Unable to create a connection to " + database.getPath() + "." );
        }

        return connection;
    }

    @Override
    public void close() throws SQLException
    {
        if ( connection != null && !connection.isClosed() )
        {
            connection.shutdown();
        }
    }
}