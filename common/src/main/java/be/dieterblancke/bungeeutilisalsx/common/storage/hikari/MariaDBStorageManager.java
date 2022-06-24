package be.dieterblancke.bungeeutilisalsx.common.storage.hikari;

import be.dieterblancke.bungeeutilisalsx.common.api.storage.StorageType;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.zaxxer.hikari.HikariConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class MariaDBStorageManager extends HikariStorageManager
{

    public MariaDBStorageManager()
    {
        super( StorageType.MARIADB, ConfigFiles.CONFIG.getConfig(), getProperties() );
    }

    private static HikariConfig getProperties()
    {
        return new HikariConfig();
    }

    @Override
    protected String getDataSourceClass()
    {
        return Utils.classFound( "org.mariadb.jdbc.MariaDbDataSource" ) ? "org.mariadb.jdbc.MariaDbDataSource"
                : "org.mariadb.jdbc.MySQLDataSource";
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return dataSource.getConnection();
    }
}