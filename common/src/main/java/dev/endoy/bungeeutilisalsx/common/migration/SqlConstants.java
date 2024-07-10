package dev.endoy.bungeeutilisalsx.common.migration;

import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

public enum SqlConstants
{

    DATA_TYPE_TINYINT( "TINYINT", "SMALLINT", "INTEGER" ),
    DATA_TYPE_SMALLINT( "SMALLINT", "SMALLINT", "INTEGER" ),
    DATA_TYPE_INT( "INT", "BIGINT", "INTEGER" ),
    DATA_TYPE_BIGINT( "BIGINT", "BIGINT", "INTEGER" ),
    DATA_TYPE_BIT( "BIT", "BIT", "INTEGER" ),
    DATA_TYPE_DOUBLE( "DOUBLE", "DOUBLE PRECISION", "REAL" ),
    DATA_TYPE_FLOAT( "FLOAT", "REAL", "REAL" ),
    DATA_TYPE_DECIMAL( "DECIMAL", "DECIMAL", "REAL" ),
    DATA_TYPE_NUMERIC( "NUMERIC", "NUMERIC", "REAL" ),
    DATA_TYPE_BOOLEAN( "BOOLEAN", "BOOLEAN", "INTEGER" ),
    DATA_TYPE_DATETIME( "DATETIME", "TIMESTAMP", "TEXT" ),
    DATA_TYPE_DATE( "DATE", "DATE", "TEXT" ),
    DATA_TYPE_TIME( "TIME", "TIME", "TEXT" ),
    DATA_TYPE_LONGTEXT( "LONGTEXT", "TEXT", "TEXT" ),
    DATA_TYPE_VARCHAR( "VARCHAR(255)", "VARCHAR(255)", "VARCHAR(255)" ),
    DATA_TYPE_SERIAL( "SERIAL", "BIGSERIAL", "INTEGER PRIMARY KEY AUTOINCREMENT" ),
    DEFAULT_VALUE_BOOLEAN_FALSE( "DEFAULT 0", "DEFAULT FALSE", "DEFAULT 0" ),
    DEFAULT_VALUE_BOOLEAN_TRUE( "DEFAULT 1", "DEFAULT TRUE", "DEFAULT 1" );

    private final String mysql;
    private final String postgresql;
    private final String sqlite;

    SqlConstants( final String mysql, final String postgresql, final String sqlite )
    {
        this.mysql = mysql;
        this.postgresql = postgresql;
        this.sqlite = sqlite;
    }

    public static String replaceConstants( String text )
    {
        final String storageType = ConfigFiles.CONFIG.getConfig().getString( "storage.type" );

        text = fixStatementForType( storageType, text );

        for ( SqlConstants constant : values() )
        {
            text = text.replace( constant.toString(), constant.getReplacement( storageType ) );
        }
        return text;
    }

    private static String fixStatementForType( final String storageType, String text )
    {
        if ( storageType.equalsIgnoreCase( "postgresql" ) )
        {
            text = text.replace( "`", "\"" );
        }
        if ( storageType.equalsIgnoreCase( "sqlite" ) )
        {
            // messy solution, but works for now

            if ( text.contains( "PRIMARY KEY (id)" ) )
            {
                text = text.replace( "PRIMARY KEY (id)", "" );
                text = Utils.replaceLast( text, ",", "" );
            }
        }
        return text;
    }

    public String getReplacement( final String type )
    {
        return switch ( type.toLowerCase() )
                {
                    case "postgresql" -> postgresql;
                    case "sqlite" -> sqlite;
                    default -> mysql;
                };
    }

    public String getMysql()
    {
        return mysql;
    }

    public String getPostgresql()
    {
        return postgresql;
    }

    public String getSqlite()
    {
        return sqlite;
    }
}
