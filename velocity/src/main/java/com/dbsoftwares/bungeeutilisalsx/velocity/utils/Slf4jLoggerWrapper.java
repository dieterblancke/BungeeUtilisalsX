package com.dbsoftwares.bungeeutilisalsx.velocity.utils;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

@AllArgsConstructor
public class Slf4jLoggerWrapper extends Handler
{

    private static final int TRACE_LEVEL_THRESHOLD = Level.FINEST.intValue();
    private static final int DEBUG_LEVEL_THRESHOLD = Level.FINE.intValue();
    private static final int INFO_LEVEL_THRESHOLD = Level.INFO.intValue();
    private static final int WARN_LEVEL_THRESHOLD = Level.WARNING.intValue();
    private final Logger logger;

    @Override
    public void publish( final LogRecord record )
    {
        if ( record == null )
        {
            return;
        }
        if ( record.getMessage() == null )
        {
            record.setMessage( "" );
        }
        callPlainSLF4JLogger( logger, record );
    }

    @Override
    public void flush()
    {
        // do nothing
    }

    @Override
    public void close() throws SecurityException
    {
        // do nothing
    }

    private void callPlainSLF4JLogger( final Logger slf4jLogger, final LogRecord record )
    {
        final String message = record.getMessage();
        int julLevelValue = record.getLevel().intValue();

        if ( julLevelValue <= TRACE_LEVEL_THRESHOLD )
        {
            slf4jLogger.trace( message, record.getThrown() );
        }
        else if ( julLevelValue <= DEBUG_LEVEL_THRESHOLD )
        {
            slf4jLogger.debug( message, record.getThrown() );
        }
        else if ( julLevelValue <= INFO_LEVEL_THRESHOLD )
        {
            slf4jLogger.info( message, record.getThrown() );
        }
        else if ( julLevelValue <= WARN_LEVEL_THRESHOLD )
        {
            slf4jLogger.warn( message, record.getThrown() );
        }
        else
        {
            slf4jLogger.error( message, record.getThrown() );
        }
    }
}
