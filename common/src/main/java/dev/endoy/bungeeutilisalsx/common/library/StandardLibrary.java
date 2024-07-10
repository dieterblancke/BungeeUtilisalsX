package dev.endoy.bungeeutilisalsx.common.library;

import dev.endoy.bungeeutilisalsx.common.BootstrapUtil;
import dev.endoy.configuration.api.IConfiguration;
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
            "42.4.0",
            checkType( "POSTGRESQL" ),
            relocate( "org.postgresql" )
    ),
    MONGODB(
            "com.mongodb.MongoClient",
            "https://repo1.maven.org/maven2/org/mongodb/mongo-java-driver/{version}/mongo-java-driver-{version}.jar",
            "3.12.11",
            checkType( "MONGODB" ),
            relocate( "com.mongodb" )
    ),
    MYSQL(
            "com.mysql.cj.jdbc.Driver",
            "https://repo1.maven.org/maven2/mysql/mysql-connector-java/{version}/mysql-connector-java-{version}.jar",
            "8.0.28",
            checkType( "MYSQL" ),
            relocate( "com.mysql" )
    ),
    HIKARICP(
            "com.zaxxer.hikari.HikariDataSource",
            "https://repo1.maven.org/maven2/com/zaxxer/HikariCP/{version}/HikariCP-{version}.jar",
            "5.0.1",
            checkType( "MYSQL", "MARIADB", "POSTGRESQL" ),
            relocate( "com.zaxxer.hikari" )
    ),
    SLF4J_API(
            "org.slf4j.LoggerFactory",
            "https://repo1.maven.org/maven2/org/slf4j/slf4j-api/{version}/slf4j-api-{version}.jar",
            "1.7.32",
            checkType( "MYSQL", "MARIADB", "POSTGRESQL" )
    ),
    SLF4J_NOP(
            "org.slf4j.jul.JDK14LoggerAdapter",
            "https://repo1.maven.org/maven2/org/slf4j/slf4j-nop/{version}/slf4j-nop-{version}.jar",
            "1.7.32",
            checkType( "MYSQL", "MARIADB", "POSTGRESQL" )
    ),
    LETTUCE(
            "io.lettuce.core.RedisClient",
            "https://repo1.maven.org/maven2/io/lettuce/lettuce-core/{version}/lettuce-core-{version}.jar",
            "6.1.8.RELEASE",
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
    APACHE_COMMONS_POOL2(
            "org.apache.commons.pool2.impl.GenericObjectPool",
            "https://repo1.maven.org/maven2/org/apache/commons/commons-pool2/{version}/commons-pool2-{version}.jar",
            "2.11.1",
            configExistsAndTrue( getConfig(), "multi-proxy.enabled" ),
            relocate( "org.apache.commons.pool2" )
    ),
    JSOUP(
            "org.jsoup.nodes.Document",
            "https://repo1.maven.org/maven2/org/jsoup/jsoup/{version}/jsoup-{version}.jar",
            "1.14.3",
            true,
            relocate( "org.jsoup" )
    ),
    RABBIT_MQ(
            "com.rabbitmq.client.RpcClient",
            "https://repo1.maven.org/maven2/com/rabbitmq/amqp-client/{version}/amqp-client-{version}.jar",
            "5.14.2",
            configExistsAndTrue( getConfig(), "multi-proxy.enabled" ),
            relocate( "com.rabbitmq" )
    ),
    RHINO(
            "org.mozilla.javascript.Script",
            "https://repo1.maven.org/maven2/org/mozilla/rhino/{version}/rhino-{version}.jar",
            "1.7.14",
            configExistsAndTrue( getConfig(), "scripting" ),
            relocate( "org.mozilla" )
    ),
    RHINO_SCRIPT_ENGINE(
            "de.christophkraemer.rhino.javascript.RhinoScriptEngineFactory",
            "https://repo.endoy.dev/artifactory/endoy-public/de/christophkraemer/rhino-script-engine/{version}/rhino-script-engine-{version}.jar",
            "1.2.1",
            configExistsAndTrue( getConfig(), "scripting" ),
            relocate( "org.mozilla" ),
            relocate( "de.christophkraemer.rhino" )
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
                pkg.replace( "dev.endoy.bungeeutilisalsx.internal.", "" ),
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
        return config = IConfiguration.loadYamlConfiguration( BootstrapUtil.class.getResourceAsStream( "/configurations/config.yml" ) );
    }
}