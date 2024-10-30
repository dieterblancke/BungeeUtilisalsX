package dev.endoy.bungeeutilisalsx.common.migration.sql.migrations;

import dev.endoy.bungeeutilisalsx.common.migration.FileMigration;

public class v7_user_settings extends FileMigration
{

    public v7_user_settings()
    {
        super( "/migrations/v7_user_settings.sql" );
    }

    @Override
    public boolean shouldRun()
    {
        return true;
    }
}
