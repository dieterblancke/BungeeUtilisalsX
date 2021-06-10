package be.dieterblancke.bungeeutilisalsx.common.migration.migrations;

import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.migration.FileMigration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class v1_migration extends FileMigration
{

    public v1_migration()
    {
        super( "migrations/v1_initial_migration.sql" );
    }

    @Override
    public boolean shouldRun( Connection connection ) throws SQLException
    {
        boolean shouldRun = true;

        final DatabaseMetaData metaData = connection.getMetaData();
        try ( ResultSet rs = metaData.getTables( null, null, PlaceHolderAPI.formatMessage( "{users-table}" ), null ) )
        {
            if ( rs.next() )
            {
                shouldRun = false;
            }
        }
        return shouldRun;
    }
}
