package be.dieterblancke.bungeeutilisalsx.common.migration.sql.migrations;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSetting;
import be.dieterblancke.bungeeutilisalsx.common.migration.FileMigration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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
