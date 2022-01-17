package be.dieterblancke.bungeeutilisalsx.webapi.commands;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.ApiTokenDao.ApiPermission;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.ApiTokenDao.ApiToken;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ApiTokenCommandCall implements CommandCall
{

    @Override
    @SneakyThrows
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final SimpleDateFormat dateFormat = new SimpleDateFormat( "dd/MM/yyyy" );

        if ( args.size() > 0 )
        {
            final String action = args.get( 0 );

            switch ( action )
            {
                case "add":
                    if ( args.size() == 4 )
                    {
                        final String token = args.get( 1 );
                        final Date date = dateFormat.parse( args.get( 2 ) );
                        final List<ApiPermission> permissions = Arrays.stream( args.get( 3 ).split( "," ) )
                                .map( ApiPermission::valueOf )
                                .collect( Collectors.toList() );

                        BuX.getApi().getStorageManager().getDao().getApiTokenDao().createApiToken( new ApiToken(
                                token,
                                date,
                                permissions.contains( ApiPermission.ALL )
                                        ? Collections.singletonList( ApiPermission.ALL )
                                        : permissions
                        ) );
                        BuX.getLogger().info( "Successfully created API token: " + token );
                        return;
                    }
                case "remove":
                    if ( args.size() == 2 )
                    {
                        final String token = args.get( 1 );

                        BuX.getApi().getStorageManager().getDao().getApiTokenDao().removeApiToken( token );
                        BuX.getLogger().info( "Successfully deleted API token: " + token );
                        return;
                    }
                case "list":
                    final List<ApiToken> tokens = BuX.getApi().getStorageManager().getDao().getApiTokenDao().getApiTokens();

                    if ( tokens.isEmpty() )
                    {
                        BuX.getLogger().info( "There are currently no tokens registered." );
                    }
                    else
                    {
                        BuX.getLogger().info( "Registered tokens:" );

                        for ( ApiToken token : tokens )
                        {
                            BuX.getLogger().info( "- Token: " + token.getApiToken()
                                    + ", Expire date: " + dateFormat.format( token.getExpireDate() )
                                    + ", Permissions: " + token.getPermissions()
                                    .stream()
                                    .map( ApiPermission::toString )
                                    .collect( Collectors.joining( "," ) )
                            );
                        }
                    }

                    return;
            }
        }

        BuX.getLogger().info( "Api Token Command help reference:" );
        BuX.getLogger().info( "- api-token add (token) (date: dd/MM/yyyy) (permissions (comma separated))" );
        BuX.getLogger().info( "- api-token remove (token)" );
        BuX.getLogger().info( "- api-token list" );
        BuX.getLogger().info( "Available permissions: " + Arrays.stream( ApiPermission.values() )
                .map( ApiPermission::toString )
                .collect( Collectors.joining( "," ) ) );
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
