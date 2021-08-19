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

import be.dieterblancke.bungeeutilisalsx.common.BootstrapUtil;
import com.dbsoftwares.configuration.api.IConfiguration;
import lombok.Getter;
import me.lucko.jarrelocator.Relocation;

import java.io.File;
import java.util.Arrays;

public enum StandardLibrary
{

    SQLITE(
            "org.sqlite.JDBC",
            "https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/{version}/sqlite-jdbc-{version}.jar",
            "3.34.0",
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
            "2.7.2",
            checkType( "MARIADB" ),
            relocate( "org.mariadb.jdbc" )
    ),
    POSTGRESQL(
            "org.postgresql.ds.PGSimpleDataSource",
            "https://repo1.maven.org/maven2/org/postgresql/postgresql/{version}/postgresql-{version}.jar",
            "42.2.18",
            checkType( "POSTGRESQL" ),
            relocate( "org.postgresql" )
    ),
    MONGODB(
            "com.mongodb.MongoClient",
            "https://repo1.maven.org/maven2/org/mongodb/mongo-java-driver/{version}/mongo-java-driver-{version}.jar",
            "3.12.7",
            checkType( "MONGODB" ),
            relocate( "com.mongodb" )
    ),
    MYSQL(
            "com.mysql.cj.jdbc.Driver",
            "https://repo1.maven.org/maven2/mysql/mysql-connector-java/{version}/mysql-connector-java-{version}.jar",
            "8.0.23",
            checkType( "MYSQL" ),
            relocate( "com.mysql" )
    ),
    HIKARICP(
            "com.zaxxer.hikari.HikariDataSource",
            "https://repo1.maven.org/maven2/com/zaxxer/HikariCP/{version}/HikariCP-{version}.jar",
            "4.0.3",
            checkType( "MYSQL", "MARIADB", "POSTGRESQL" ),
            relocate( "com.zaxxer.hikari" ),
            relocate( "org.slf4j" )
    ),
    SLF4J_API(
            "org.slf4j.LoggerFactory",
            "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/{version}/slf4j-api-{version}.jar",
            "1.7.32",
            checkType( "MYSQL", "MARIADB", "POSTGRESQL" ),
            relocate( "org.slf4j" )
    ),
    SLF4J_JDK14(
            "org.slf4j.jul.JDK14LoggerAdapter",
            "https://repo1.maven.org/maven2/org/slf4j/slf4j-jdk14/{version}/slf4j-jdk14-{version}.jar",
            "1.7.32",
            checkType( "MYSQL", "MARIADB", "POSTGRESQL" ),
            relocate( "org.slf4j" )
    ),
    LETTUCE(
            "io.lettuce.core.RedisClient",
            "https://repo1.maven.org/maven2/io/lettuce/lettuce-core/{version}/lettuce-core-{version}.jar",
            "6.0.2.RELEASE",
            configExistsAndTrue( getConfig(), "multi-proxy.enabled" ),
            relocate( "io.lettuce.core" ),
            relocate( "reactor.core" ),
            relocate( "reactor.adapter" ),
            relocate( "reactor.util" ),
            relocate( "org.reactivestreams" ),
            relocate( "org.apache.commons.pool2" )
    ),
    REACTOR_CORE(
            "reactor.core.scheduler.Schedulers",
            "https://repo1.maven.org/maven2/io/projectreactor/reactor-core/{version}/reactor-core-{version}.jar",
            "3.3.10.RELEASE",
            configExistsAndTrue( getConfig(), "multi-proxy.enabled" ),
            relocate( "reactor.core" ),
            relocate( "reactor.adapter" ),
            relocate( "reactor.util" ),
            relocate( "org.reactivestreams" )
    ),
    REACTIVE_STREAMS(
            "org.reactivestreams.Processor",
            "https://repo1.maven.org/maven2/org/reactivestreams/reactive-streams/{version}/reactive-streams-{version}.jar",
            "1.0.3",
            configExistsAndTrue( getConfig(), "multi-proxy.enabled" ),
            relocate( "org.reactivestreams" )
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
            "1.16-R0.4",
            true
    ),
    RHINO(
            "org.mozilla.javascript.Context",
            "https://repo1.maven.org/maven2/org/mozilla/rhino/{version}/rhino-{version}.jar",
            "1.7.13",
            configExistsAndTrue( getConfig(), "scripting" ),
            relocate( "org.mozilla" )
    ),
    RHINO_SCRIPT_ENGINE(
            "de.christophkraemer.rhino.javascript.RhinoScriptEngine",
            "https://repo1.maven.org/maven2/de/christophkraemer/rhino-script-engine/{version}/rhino-script-engine-{version}.jar",
            "1.1.1",
            configExistsAndTrue( getConfig(), "scripting" ),
            relocate( "de.christophkraemer.rhino" )
    ),
    APACHE_COMMONS_POOL2(
            "org.apache.commons.pool2.impl.GenericObjectPool",
            "https://repo1.maven.org/maven2/org/apache/commons/commons-pool2/{version}/commons-pool2-{version}.jar",
            "2.9.0",
            configExistsAndTrue( getConfig(), "multi-proxy.enabled" ),
            relocate( "org.apache.commons.pool2" )
    ),
    JSOUP(
            "org.jsoup.nodes.Document",
            "https://repo1.maven.org/maven2/org/jsoup/jsoup/{version}/jsoup-{version}.jar",
            "1.14.1",
            true,
            relocate( "org.jsoup" )
    ),
    RABBIT_MQ(
            "com.rabbitmq.client.RpcClient",
            "https://repo1.maven.org/maven2/com/rabbitmq/amqp-client/{version}/amqp-client-{version}.jar",
            "5.13.0",
            configExistsAndTrue( getConfig(), "multi-proxy.enabled" ),
            relocate( "com.rabbitmq" )
    );

    private static IConfiguration config;
    @Getter
    private final Library library;

    StandardLibrary( final String className,
                     final String downloadURL,
                     final String version,
                     final boolean load,
                     final Relocation... relocations )
    {
        this.library = new Library( toString(), className, downloadURL, version, load, Arrays.asList( relocations ) );
    }

    private static boolean checkType( String... types )
    {
        for ( String type : types )
        {
            if ( getConfig().getString( "storage.type" ).equalsIgnoreCase( type ) )
            {
                return true;
            }
        }
        return false;
    }

    private static boolean configExistsAndTrue( final IConfiguration config, final String path )
    {
        return config.exists( path ) && config.getBoolean( path );
    }

    private static Relocation relocate( final String pkg )
    {
        return new Relocation(
                pkg.replace( "be.dieterblancke.bungeeutilisalsx.internal.", "" ),
                pkg
        );
    }

    private static IConfiguration getConfig()
    {
        if ( config != null )
        {
            return config;
        }
        final File configFile = new File( BootstrapUtil.getDataFolder(), "config.yml" );

        if ( configFile.exists() )
        {
            return config = IConfiguration.loadYamlConfiguration( configFile );
        }
        return config = IConfiguration.loadYamlConfiguration( BootstrapUtil.class.getResourceAsStream( "/config.yml" ) );
    }
}