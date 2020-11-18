package com.dbsoftwares.bungeeutilisalsx.common.api.storage;

import com.dbsoftwares.bungeeutilisalsx.common.api.utils.reflection.ReflectionUtils;
import lombok.Getter;

public enum StorageType
{
    MYSQL( ReflectionUtils.getClass( "com.dbsoftwares.bungeeutilisalsx.common.storage.hikari.MySQLStorageManager" ),
            "MySQL", "schemas/mysql.sql" ),
    POSTGRESQL( ReflectionUtils.getClass( "com.dbsoftwares.bungeeutilisalsx.common.storage.hikari.PostgreSQLStorageManager" ),
            "PostgreSQL", "schemas/postgresql.sql" ),
    MARIADB( ReflectionUtils.getClass( "com.dbsoftwares.bungeeutilisalsx.common.storage.hikari.MariaDBStorageManager" ),
            "MariaDB", "schemas/mysql.sql" ),
    SQLITE( ReflectionUtils.getClass( "com.dbsoftwares.bungeeutilisalsx.common.storage.file.SQLiteStorageManager" ),
            "SQLite", "schemas/sqlite.sql" ),
    H2( ReflectionUtils.getClass( "com.dbsoftwares.bungeeutilisalsx.common.storage.file.H2StorageManager" ),
            "H2", "schemas/mysql.sql" ),
    MONGODB( ReflectionUtils.getClass( "com.dbsoftwares.bungeeutilisalsx.common.storage.mongodb.MongoDBStorageManager" ),
            "MongoDB", null );

    @Getter
    private final Class<? extends AbstractStorageManager> manager;
    @Getter
    private final String name;
    @Getter
    private final String schema;

    StorageType( Class<?> manager, String name, String schema )
    {
        this.manager = (Class<? extends AbstractStorageManager>) manager;
        this.name = name;
        this.schema = schema;
    }
}
