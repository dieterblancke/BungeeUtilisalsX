package be.dieterblancke.bungeeutilisalsx.common.migration.sql.migrations;

import be.dieterblancke.bungeeutilisalsx.common.migration.FileMigration;

import java.sql.Connection;

public class v3_add_punishment_tracks_migration extends FileMigration
{

    public v3_add_punishment_tracks_migration()
    {
        super( "migrations/v3_add_punishment_tracks.sql" );
    }

    @Override
    public boolean shouldRun()
    {
        return true;
    }
}
