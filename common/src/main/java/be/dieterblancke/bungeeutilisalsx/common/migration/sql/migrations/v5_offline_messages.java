package be.dieterblancke.bungeeutilisalsx.common.migration.sql.migrations;

import be.dieterblancke.bungeeutilisalsx.common.migration.FileMigration;

public class v5_offline_messages extends FileMigration
{

    public v5_offline_messages()
    {
        super( "migrations/v5_offline_messages.sql" );
    }

    @Override
    public boolean shouldRun()
    {
        return true;
    }
}
