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

public class H2StorageManager extends SQLStorageManager
{

    private final File database;
    private UnclosableConnection connection;

    public H2StorageManager()
    {
        super( StorageType.H2, new SQLDao() );
        database = new File( BuX.getInstance().getDataFolder(), "h2-storage.db" );

        try
        {
            if ( !database.exists() )
            {
                database.createNewFile();
            }
        }
        catch ( IOException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
        }
        try
        {
            Class.forName( "org.h2.Driver" );
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
        return UnclosableConnection.wrap( DriverManager.getConnection( "jdbc:h2:./" + database.getPath() + ";mode=MySQL" ) );
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