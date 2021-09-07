package be.dieterblancke.bungeeutilisalsx.common.migration.sql.migrations;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.migration.FileMigration;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

public class v1_initial_migration extends FileMigration
{

    public v1_initial_migration()
    {
        super( "migrations/v1_initial_migration.sql" );
    }

    @Override
    public boolean shouldRun() throws SQLException
    {
        boolean shouldRun = true;

        try ( Connection connection = BuX.getInstance().getAbstractStorageManager().getConnection() )
        {
            final DatabaseMetaData metaData = connection.getMetaData();
            try ( ResultSet rs = metaData.getTables( null, null, "bu_users", null ) )
            {
                if ( rs.next() )
                {
                    shouldRun = false;
                }
            }
        }
        return shouldRun;
    }
}
