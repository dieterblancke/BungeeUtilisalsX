package dev.endoy.bungeeutilisalsx.common.migration.sql.migrations;

import dev.endoy.bungeeutilisalsx.common.migration.FileMigration;

public class v6_api_tokens extends FileMigration
{

    public v6_api_tokens()
    {
        super( "/migrations/v6_api_tokens.sql" );
    }

    @Override
    public boolean shouldRun()
    {
        return true;
    }
}
