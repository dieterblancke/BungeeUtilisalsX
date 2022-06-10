package be.dieterblancke.bungeeutilisalsx.common.migration.sql.migrations;

import be.dieterblancke.bungeeutilisalsx.common.migration.FileMigration;

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
