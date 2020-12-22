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

package be.dieterblancke.bungeeutilisalsx.common.library;

import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
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
    MYSQL(
            "com.mysql.cj.jdbc.Driver",
            "https://repo1.maven.org/maven2/mysql/mysql-connector-java/{version}/mysql-connector-java-{version}.jar",
            "8.0.22",
            checkType( "MYSQL" )
    ),
    HIKARICP(
            "com.zaxxer.hikari.HikariDataSource",
            "https://repo1.maven.org/maven2/com/zaxxer/HikariCP/{version}/HikariCP-{version}.jar",
            "3.4.2",
            checkType( "MYSQL", "MARIADB", "POSTGRESQL" )
    ),
    SLF4J(
            "org.slf4j.LoggerFactory",
            "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/{version}/slf4j-api-{version}.jar",
            "1.7.30",
            checkType( "MYSQL", "MARIADB", "POSTGRESQL" )
    ),
    LETTUCE(
            "io.lettuce.core.RedisClient",
            "https://repo1.maven.org/maven2/io/lettuce/lettuce-core/{version}/lettuce-core-{version}.jar",
            "6.0.1.RELEASE",
            ConfigFiles.CONFIG.getConfig().exists( "bridging.enabled" ) && ConfigFiles.CONFIG.getConfig().getBoolean( "bridging.enabled" )
    ),
    REACTOR_CORE(
            "reactor.core.scheduler.Schedulers",
            "https://repo1.maven.org/maven2/io/projectreactor/reactor-core/{version}/reactor-core-{version}.jar",
            "3.3.10.RELEASE",
            ConfigFiles.CONFIG.getConfig().exists( "bridging.enabled" ) && ConfigFiles.CONFIG.getConfig().getBoolean( "bridging.enabled" )
    ),
    REACTIVE_STREAMS(
            "org.reactivestreams.Processor",
            "https://repo1.maven.org/maven2/org/reactivestreams/reactive-streams/{version}/reactive-streams-{version}.jar",
            "1.0.3",
            ConfigFiles.CONFIG.getConfig().exists( "bridging.enabled" ) && ConfigFiles.CONFIG.getConfig().getBoolean( "bridging.enabled" )
    ),
    GUAVA(
            "com.google.common.collect.Lists",
            "https://repo1.maven.org/maven2/com/google/guava/guava/{version}/guava-{version}.jar",
            "30.0-jre",
            true
    ),
    GSON(
            "com.google.gson.Gson",
            "https://repo1.maven.org/maven2/com/google/code/gson/gson/{version}/gson-{version}.jar",
            "2.8.6",
            true
    ),
    TEXTCOMPONENT(
            "net.md_5.bungee.api.ChatColor",
            "https://repo1.maven.org/maven2/net/md-5/bungeecord-chat/{version}/bungeecord-chat-{version}.jar",
            "1.16-R0.3",
            true
    ),
    RHINO(
            "org.mozilla.javascript.Context",
            "https://repo1.maven.org/maven2/org/mozilla/rhino/{version}/rhino-{version}.jar",
            "1.7.13",
            ConfigFiles.CONFIG.getConfig().get( "scripting", false )
    ),
    RHINO_SCRIPT_ENGINE(
            "de.christophkraemer.rhino.javascript.RhinoScriptEngine",
            "https://repo1.maven.org/maven2/de/christophkraemer/rhino-script-engine/{version}/rhino-script-engine-{version}.jar",
            "1.1.1",
            ConfigFiles.CONFIG.getConfig().get( "scripting", false )
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