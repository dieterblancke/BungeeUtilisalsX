package dev.endoy.bungeeutilisalsx.common.migration.sql.migrations;

import dev.endoy.bungeeutilisalsx.common.migration.FileMigration;

public class v2_add_server_column_migration extends FileMigration
{

    public v2_add_server_column_migration()
    {
        super( "/migrations/v2_add_server_column.sql" );
    }

    @Override
    public boolean shouldRun()
    {
        return true;
    }
}
