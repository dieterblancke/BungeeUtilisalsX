/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.library;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import lombok.Getter;

public enum StandardLibrary
{

    SQLITE(
            "org.sqlite.JDBC",
            "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/{version}/sqlite-jdbc-{version}.jar",
            "3.30.1",
            checkType( "SQLITE" )
    ),
    H2(
            "org.h2.jdbcx.JdbcDataSource",
            "https://repo1.maven.org/maven2/com/h2database/h2/{version}/h2-{version}.jar",
            "1.4.200",
            checkType( "H2" )
    ),
    MARIADB(
            "org.mariadb.jdbc.MariaDbDataSource",
            "https://repo1.maven.org/maven2/org/mariadb/jdbc/mariadb-java-client/{version}/mariadb-java-client-{version}.jar",
            "2.5.4",
            checkType( "MARIADB" )
    ),
    POSTGRESQL(
            "org.postgresql.ds.PGSimpleDataSource",
            "https://repo1.maven.org/maven2/org/postgresql/postgresql/{version}/postgresql-{version}.jar",
            "42.2.9",
            checkType( "POSTGRESQL" )
    ),
    MONGODB(
            "com.mongodb.MongoClient",
            "https://repo1.maven.org/maven2/org/mongodb/mongo-java-driver/{version}/mongo-java-driver-{version}.jar",
            "3.12.1",
            checkType( "MONGODB" )
    ),
    HIKARICP(
            "com.zaxxer.hikari.HikariDataSource",
            "https://repo1.maven.org/maven2/com/zaxxer/HikariCP/{version}/HikariCP-{version}.jar",
            "3.4.2",
            checkType( "MYSQL", "MARIADB", "POSTGRESQL" )
    ),
    LETTUCE(
            "io.lettuce.core.RedisClient",
            "https://repo1.maven.org/maven2/io/lettuce/lettuce-core/{version}/lettuce-core-{version}.jar",
            "6.0.0.RELEASE",
            ConfigFiles.CONFIG.getConfig().getBoolean( "bridging.enabled" )
    ),
    REACTOR_CORE(
            "reactor.core.scheduler.Schedulers",
            "https://repo1.maven.org/maven2/io/projectreactor/reactor-core/{version}/reactor-core-{version}.jar",
            "3.3.10.RELEASE",
            ConfigFiles.CONFIG.getConfig().getBoolean( "bridging.enabled" )
    ),
    REACTIVE_STREAMS(
            "org.reactivestreams.Processor",
            "https://repo1.maven.org/maven2/org/reactivestreams/reactive-streams/{version}/reactive-streams-{version}.jar",
            "1.0.3",
            ConfigFiles.CONFIG.getConfig().getBoolean( "bridging.enabled" )
    );

    @Getter
    private final Library library;

    StandardLibrary( String className, String downloadURL, String version, boolean load )
    {
        this.library = new Library( toString(), className, downloadURL, version, load );
    }

    private static boolean checkType( String... types )
    {
        for ( String type : types )
        {
            if ( ConfigFiles.CONFIG.getConfig().getString( "storage.type" ).equalsIgnoreCase( type ) )
            {
                return true;
            }
        }
        return false;
    }
}