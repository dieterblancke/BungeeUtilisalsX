package be.dieterblancke.bungeeutilisalsx.common.migration.sql.migrations;

import be.dieterblancke.bungeeutilisalsx.common.migration.FileMigration;

public class v2_add_server_column_migration extends FileMigration
{

    public v2_add_server_column_migration()
    {
        super( "migrations/v2_add_server_column.sql" );
    }

    @Override
    public boolean shouldRun()
    {
        return true;
    }
}
