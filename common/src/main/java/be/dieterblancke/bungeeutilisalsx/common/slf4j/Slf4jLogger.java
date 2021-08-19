package be.dieterblancke.bungeeutilisalsx.common.slf4j;

import org.slf4j.Marker;
import org.slf4j.event.LoggingEvent;
import org.slf4j.helpers.FormattingTuple;
import org.slf4j.helpers.MarkerIgnoringBase;
import org.slf4j.helpers.MessageFormatter;
import org.slf4j.spi.LocationAwareLogger;

import java.util.logging.Level;
import java.util.logging.LogRecord;

public class Slf4jLogger extends MarkerIgnoringBase implements LocationAwareLogger
{

    private static final long serialVersionUID = -8053026990503422791L;

    transient final java.util.logging.Logger logger;

    Slf4jLogger( java.util.logging.Logger logger )
    {
        this.logger = logger;
        this.name = logger.getName();
    }

    public boolean isTraceEnabled()
    {
        return logger.isLoggable( Level.FINEST );
    }

    public void trace( String msg )
    {
        if ( logger.isLoggable( Level.FINEST ) )
        {
            log( Level.FINEST, msg, null );
        }
    }

    public void trace( String format, Object arg )
    {
        if ( logger.isLoggable( Level.FINEST ) )
        {
            FormattingTuple ft = MessageFormatter.format( format, arg );
            log( Level.FINEST, ft.getMessage(), ft.getThrowable() );
        }
    }

    public void trace( String format, Object arg1, Object arg2 )
    {
        if ( logger.isLoggable( Level.FINEST ) )
        {
            FormattingTuple ft = MessageFormatter.format( format, arg1, arg2 );
            log( Level.FINEST, ft.getMessage(), ft.getThrowable() );
        }
    }

    public void trace( String format, Object... argArray )
    {
        if ( logger.isLoggable( Level.FINEST ) )
        {
            FormattingTuple ft = MessageFormatter.arrayFormat( format, argArray );
            log( Level.FINEST, ft.getMessage(), ft.getThrowable() );
        }
    }

    public void trace( String msg, Throwable t )
    {
        if ( logger.isLoggable( Level.FINEST ) )
        {
            log( Level.FINEST, msg, t );
        }
    }

    public boolean isDebugEnabled()
    {
        return logger.isLoggable( Level.FINE );
    }

    public void debug( String msg )
    {
        if ( logger.isLoggable( Level.FINE ) )
        {
            log( Level.FINE, msg, null );
        }
    }

    public void debug( String format, Object arg )
    {
        if ( logger.isLoggable( Level.FINE ) )
        {
            FormattingTuple ft = MessageFormatter.format( format, arg );
            log( Level.FINE, ft.getMessage(), ft.getThrowable() );
        }
    }

    public void debug( String format, Object arg1, Object arg2 )
    {
        if ( logger.isLoggable( Level.FINE ) )
        {
            FormattingTuple ft = MessageFormatter.format( format, arg1, arg2 );
            log( Level.FINE, ft.getMessage(), ft.getThrowable() );
        }
    }

    public void debug( String format, Object... argArray )
    {
        if ( logger.isLoggable( Level.FINE ) )
        {
            FormattingTuple ft = MessageFormatter.arrayFormat( format, argArray );
            log( Level.FINE, ft.getMessage(), ft.getThrowable() );
        }
    }

    public void debug( String msg, Throwable t )
    {
        if ( logger.isLoggable( Level.FINE ) )
        {
            log( Level.FINE, msg, t );
        }
    }

    public boolean isInfoEnabled()
    {
        return logger.isLoggable( Level.INFO );
    }

    public void info( String msg )
    {
        if ( logger.isLoggable( Level.INFO ) )
        {
            log( Level.INFO, msg, null );
        }
    }

    public void info( String format, Object arg )
    {
        if ( logger.isLoggable( Level.INFO ) )
        {
            FormattingTuple ft = MessageFormatter.format( format, arg );
            log( Level.INFO, ft.getMessage(), ft.getThrowable() );
        }
    }

    public void info( String format, Object arg1, Object arg2 )
    {
        if ( logger.isLoggable( Level.INFO ) )
        {
            FormattingTuple ft = MessageFormatter.format( format, arg1, arg2 );
            log( Level.INFO, ft.getMessage(), ft.getThrowable() );
        }
    }

    public void info( String format, Object... argArray )
    {
        if ( logger.isLoggable( Level.INFO ) )
        {
            FormattingTuple ft = MessageFormatter.arrayFormat( format, argArray );
            log( Level.INFO, ft.getMessage(), ft.getThrowable() );
        }
    }

    public void info( String msg, Throwable t )
    {
        if ( logger.isLoggable( Level.INFO ) )
        {
            log( Level.INFO, msg, t );
        }
    }

    public boolean isWarnEnabled()
    {
        return logger.isLoggable( Level.WARNING );
    }

    public void warn( String msg )
    {
        if ( logger.isLoggable( Level.WARNING ) )
        {
            log( Level.WARNING, msg, null );
        }
    }

    public void warn( String format, Object arg )
    {
        if ( logger.isLoggable( Level.WARNING ) )
        {
            FormattingTuple ft = MessageFormatter.format( format, arg );
            log( Level.WARNING, ft.getMessage(), ft.getThrowable() );
        }
    }

    public void warn( String format, Object arg1, Object arg2 )
    {
        if ( logger.isLoggable( Level.WARNING ) )
        {
            FormattingTuple ft = MessageFormatter.format( format, arg1, arg2 );
            log( Level.WARNING, ft.getMessage(), ft.getThrowable() );
        }
    }

    public void warn( String format, Object... argArray )
    {
        if ( logger.isLoggable( Level.WARNING ) )
        {
            FormattingTuple ft = MessageFormatter.arrayFormat( format, argArray );
            log( Level.WARNING, ft.getMessage(), ft.getThrowable() );
        }
    }

    public void warn( String msg, Throwable t )
    {
        if ( logger.isLoggable( Level.WARNING ) )
        {
            log( Level.WARNING, msg, t );
        }
    }

    public boolean isErrorEnabled()
    {
        return logger.isLoggable( Level.SEVERE );
    }

    public void error( String msg )
    {
        if ( logger.isLoggable( Level.SEVERE ) )
        {
            log( Level.SEVERE, msg, null );
        }
    }

    public void error( String format, Object arg )
    {
        if ( logger.isLoggable( Level.SEVERE ) )
        {
            FormattingTuple ft = MessageFormatter.format( format, arg );
            log( Level.SEVERE, ft.getMessage(), ft.getThrowable() );
        }
    }

    public void error( String format, Object arg1, Object arg2 )
    {
        if ( logger.isLoggable( Level.SEVERE ) )
        {
            FormattingTuple ft = MessageFormatter.format( format, arg1, arg2 );
            log( Level.SEVERE, ft.getMessage(), ft.getThrowable() );
        }
    }

    public void error( String format, Object... arguments )
    {
        if ( logger.isLoggable( Level.SEVERE ) )
        {
            FormattingTuple ft = MessageFormatter.arrayFormat( format, arguments );
            log( Level.SEVERE, ft.getMessage(), ft.getThrowable() );
        }
    }

    public void error( String msg, Throwable t )
    {
        if ( logger.isLoggable( Level.SEVERE ) )
        {
            log( Level.SEVERE, msg, t );
        }
    }

    private void log( Level level, String msg, Throwable t )
    {
        LogRecord record = new LogRecord( level, msg );
        record.setLoggerName( getName() );
        record.setThrown( t );
        logger.log( record );
    }

    public void log( Marker marker, String callerFQCN, int level, String message, Object[] argArray, Throwable t )
    {
        Level julLevel = slf4jLevelIntToJULLevel( level );

        if ( logger.isLoggable( julLevel ) )
        {
            log( julLevel, message, t );
        }
    }

    private Level slf4jLevelIntToJULLevel( int slf4jLevelInt )
    {
        Level julLevel;
        switch ( slf4jLevelInt )
        {
            case LocationAwareLogger.TRACE_INT:
                julLevel = Level.FINEST;
                break;
            case LocationAwareLogger.DEBUG_INT:
                julLevel = Level.FINE;
                break;
            case LocationAwareLogger.INFO_INT:
                julLevel = Level.INFO;
                break;
            case LocationAwareLogger.WARN_INT:
                julLevel = Level.WARNING;
                break;
            case LocationAwareLogger.ERROR_INT:
                julLevel = Level.SEVERE;
                break;
            default:
                throw new IllegalStateException( "Level number " + slf4jLevelInt + " is not recognized." );
        }
        return julLevel;
    }

    public void log( LoggingEvent event )
    {
        Level julLevel = slf4jLevelIntToJULLevel( event.getLevel().toInt() );
        if ( logger.isLoggable( julLevel ) )
        {
            LogRecord record = eventToRecord( event, julLevel );
            logger.log( record );
        }
    }

    private LogRecord eventToRecord( LoggingEvent event, Level julLevel )
    {
        String format = event.getMessage();
        Object[] arguments = event.getArgumentArray();
        FormattingTuple ft = MessageFormatter.arrayFormat( format, arguments );
        if ( ft.getThrowable() != null && event.getThrowable() != null )
        {
            throw new IllegalArgumentException( "both last element in argument array and last argument are of type Throwable" );
        }

        Throwable t = event.getThrowable();
        if ( ft.getThrowable() != null )
        {
            t = ft.getThrowable();
            throw new IllegalStateException( "fix above code" );
        }

        LogRecord record = new LogRecord( julLevel, ft.getMessage() );
        record.setLoggerName( event.getLoggerName() );
        record.setMillis( event.getTimeStamp() );

        record.setThrown( t );
        return record;
    }
}