package be.dieterblancke.bungeeutilisalsx.common.storage;

import be.dieterblancke.bungeeutilisalsx.common.storage.file.SQLiteStorageManager;

import java.sql.SQLException;

public class TestSQLiteStorageManager extends SQLiteStorageManager
{

    public TestSQLiteStorageManager() throws SQLException
    {
        super( null );
    }
}
