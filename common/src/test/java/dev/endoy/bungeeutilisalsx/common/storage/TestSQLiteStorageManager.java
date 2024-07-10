package dev.endoy.bungeeutilisalsx.common.storage;

import dev.endoy.bungeeutilisalsx.common.storage.file.SQLiteStorageManager;

import java.sql.SQLException;

public class TestSQLiteStorageManager extends SQLiteStorageManager
{

    public TestSQLiteStorageManager() throws SQLException
    {
        super( null );
    }
}
