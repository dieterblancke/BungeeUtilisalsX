package be.dieterblancke.bungeeutilisalsx.common.slf4j;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Slf4jLoggerFactory implements ILoggerFactory
{

    ConcurrentMap<String, Logger> loggerMap;

    public Slf4jLoggerFactory()
    {
        loggerMap = new ConcurrentHashMap<>();
        java.util.logging.Logger.getLogger( "" );
    }

    public Logger getLogger( String name )
    {
        // the root logger is called "" in JUL
        if ( name.equalsIgnoreCase( Logger.ROOT_LOGGER_NAME ) )
        {
            name = "";
        }

        Logger slf4jLogger = loggerMap.get( name );
        if ( slf4jLogger != null )
            return slf4jLogger;
        else
        {
            java.util.logging.Logger julLogger = java.util.logging.Logger.getLogger( name );
            Logger newInstance = new Slf4jLogger( julLogger );
            Logger oldInstance = loggerMap.putIfAbsent( name, newInstance );
            return oldInstance == null ? newInstance : oldInstance;
        }
    }

    // https://github.com/qos-ch/slf4j/blob/abad55509a99b39d82965769b552d6f3a9a535d3/slf4j-jdk14/src/main/java/org/slf4j/jul/JULServiceProvider.java
}