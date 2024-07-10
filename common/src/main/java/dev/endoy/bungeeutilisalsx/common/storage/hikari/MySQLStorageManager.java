package dev.endoy.bungeeutilisalsx.common.storage.hikari;

import dev.endoy.bungeeutilisalsx.common.api.storage.StorageType;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.zaxxer.hikari.HikariConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class MySQLStorageManager extends HikariStorageManager
{

    public MySQLStorageManager()
    {
        super( StorageType.MYSQL, ConfigFiles.CONFIG.getConfig(), getProperties() );
    }

    private static HikariConfig getProperties()
    {
        final String hostname = ConfigFiles.CONFIG.getConfig().getString( "storage.hostname" );
        final int port = ConfigFiles.CONFIG.getConfig().getInteger( "storage.port" );
        final String database = ConfigFiles.CONFIG.getConfig().getString( "storage.database" );

        final HikariConfig config = new HikariConfig();
        config.setDriverClassName( "com.mysql.cj.jdbc.Driver" );
        config.setJdbcUrl( "jdbc:mysql://" + hostname + ":" + port + "/" + database );
        config.addDataSourceProperty( "cachePrepStmts", "true" );
        config.addDataSourceProperty( "alwaysSendSetIsolation", "false" );
        config.addDataSourceProperty( "cacheServerConfiguration", "true" );
        config.addDataSourceProperty( "elideSetAutoCommits", "true" );
        config.addDataSourceProperty( "useLocalSessionState", "true" );
        config.addDataSourceProperty( "useServerPrepStmts", "true" );
        config.addDataSourceProperty( "prepStmtCacheSize", "250" );
        config.addDataSourceProperty( "prepStmtCacheSqlLimit", "2048" );
        config.addDataSourceProperty( "cacheCallableStmts", "true" );
        return config;
    }

    @Override
    protected String getDataSourceClass()
    {
        return null;
    }

    @Override
    public Connection getConnection() throws SQLException
    {
        return dataSource.getConnection();
    }
}