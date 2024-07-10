package dev.endoy.bungeeutilisalsx.common.storage.file;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.storage.StorageType;
import dev.endoy.bungeeutilisalsx.common.storage.data.sql.SQLDao;
import dev.endoy.bungeeutilisalsx.common.storage.sql.SQLStorageManager;

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

    public SQLiteStorageManager()
    {
        this( new File( BuX.getInstance().getDataFolder(), "data.db" ) );
    }

    public SQLiteStorageManager( final File database )
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
        try
        {
            initializeConnection();
        }
        catch ( SQLException e )
        {
            throw new RuntimeException( e );
        }
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