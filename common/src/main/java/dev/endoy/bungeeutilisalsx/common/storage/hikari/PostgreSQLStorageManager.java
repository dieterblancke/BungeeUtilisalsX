package dev.endoy.bungeeutilisalsx.common.storage.hikari;

import dev.endoy.bungeeutilisalsx.common.api.storage.StorageType;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.sql.Connection;
import java.sql.SQLException;

public class PostgreSQLStorageManager extends HikariStorageManager
{

    public PostgreSQLStorageManager()
    {
        super( StorageType.POSTGRESQL, ConfigFiles.CONFIG.getConfig(), null );
    }

    @Override
    protected String getDataSourceClass()
    {
        return "org.postgresql.ds.PGSimpleDataSource";
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return dataSource.getConnection();
    }
}