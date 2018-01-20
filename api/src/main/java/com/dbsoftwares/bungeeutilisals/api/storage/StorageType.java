package com.dbsoftwares.bungeeutilisals.api.storage;

/*
 * Created by DBSoftwares on 15/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.utils.ReflectionUtils;
import lombok.Getter;

public enum StorageType {

    MYSQL(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.storage.manager.hikari.MySQLManager"),
            "MySQL", "schemas/mysql.sql"),
    POSTGRESQL(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.storage.manager.hikari.PostgreSQLManager"),
            "PostgreSQL", "schemas/postgresql.sql"),
    MARIADB(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.storage.manager.hikari.MariaDBManager"),
            "MariaDB", "schemas/mariadb.sql"),
    SQLITE(ReflectionUtils.getClass("com.dbsoftwares.bungeeutilisals.bungee.storage.manager.file.SQLiteManager"),
            "SQLite", "schemas/sqlite.sql");

    @Getter
    private Class<? extends AbstractManager> manager;
    @Getter
    private String name;
    @Getter
    private String schema;

    @SuppressWarnings("unchecked")
        // We know the classes are always an instance of AbstractManager
    StorageType(Class<?> manager, String name, String schema) {
        this.manager = (Class<? extends AbstractManager>) manager;
        this.name = name;
        this.schema = schema;
    }
}