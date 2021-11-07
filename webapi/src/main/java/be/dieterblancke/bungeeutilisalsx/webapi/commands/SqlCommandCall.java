package be.dieterblancke.bungeeutilisalsx.webapi.commands;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import com.google.common.base.Joiner;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.List;

public class SqlCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() > 0 )
        {
            final String action = args.get( 0 );

            switch ( action )
            {
                case "query":
                    if ( args.size() > 1 )
                    {
                        query( Joiner.on( " " ).join( args.subList( 1, args.size() ) ) );
                        return;
                    }
                case "exec":
                    if ( args.size() > 1 )
                    {
                        exec( Joiner.on( " " ).join( args.subList( 1, args.size() ) ) );
                        return;
                    }
            }
        }

        BuX.getLogger().info( "SQL Command help reference:" );
        BuX.getLogger().info( "- sql query (query)" );
        BuX.getLogger().info( "- sql exec (query)" );
    }

    @Override
    public String getDescription()
    {
        return null;
    }

    @Override
    public String getUsage()
    {
        return null;
    }

    @SneakyThrows
    private void query( final String query )
    {
        try ( Connection connection = BuX.getApi().getConnection();
              Statement statement = connection.createStatement() )
        {
            try ( ResultSet rs = statement.executeQuery( query ) )
            {
                printColumns( rs );
            }
        }
    }

    @SneakyThrows
    private void exec( final String query )
    {
        try ( Connection connection = BuX.getApi().getConnection();
              Statement statement = connection.createStatement() )
        {
            final int updated = statement.executeUpdate( query );

            System.out.println( "Records altered: " + updated );
        }
    }

    @SneakyThrows
    private void printColumns( final ResultSet resultSet )
    {
        final ResultSetMetaData rsmd = resultSet.getMetaData();
        final int columnsNumber = rsmd.getColumnCount();

        while ( resultSet.next() )
        {
            for ( int i = 1; i <= columnsNumber; i++ )
            {
                if ( i > 1 )
                {
                    System.out.print( ",  " );
                }
                final String columnValue = resultSet.getString( i );
                System.out.print( rsmd.getColumnName( i ) + ": " + columnValue );
            }
            System.out.println();
        }
    }
}
