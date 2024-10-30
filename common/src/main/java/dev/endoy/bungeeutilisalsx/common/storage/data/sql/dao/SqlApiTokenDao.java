package dev.endoy.bungeeutilisalsx.common.storage.data.sql.dao;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.ApiTokenDao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.Dao;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.stream.Collectors;

public class SqlApiTokenDao implements ApiTokenDao
{

    @Override
    @SneakyThrows
    public void createApiToken( final ApiToken token )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                  "INSERT INTO bu_api_token(api_token, expire_date, permissions) VALUES (?, ?, ?);"
              ) )
        {
            pstmt.setString( 1, token.getApiToken() );
            pstmt.setString( 2, Dao.formatDateToString( token.getExpireDate() ) );
            pstmt.setString( 3, token.getPermissions()
                .stream()
                .map( ApiPermission::toString )
                .collect( Collectors.joining( "," ) )
            );
            pstmt.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public Optional<ApiToken> findApiToken( final String token )
    {
        ApiToken apiToken = null;

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                  "SELECT * FROM bu_api_token WHERE api_token = ? AND expire_date >= " + Dao.getInsertDateParameter() + ";"
              ) )
        {
            pstmt.setString( 1, token );
            pstmt.setString( 2, Dao.formatDateToString( new Date() ) );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    apiToken = new ApiToken(
                        rs.getString( "api_token" ),
                        Dao.formatStringToDate( rs.getString( "expire_date" ) ),
                        Arrays.stream( rs.getString( "permissions" ).split( "," ) )
                            .map( ApiPermission::valueOf )
                            .collect( Collectors.toList() )
                    );
                }
            }
        }
        return Optional.ofNullable( apiToken );
    }

    @Override
    @SneakyThrows
    public void removeApiToken( final String token )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                  "DELETE FROM bu_api_token WHERE api_token = ?;"
              ) )
        {
            pstmt.setString( 1, token );
            pstmt.executeUpdate();
        }
    }

    @Override
    @SneakyThrows
    public List<ApiToken> getApiTokens()
    {
        final List<ApiToken> apiTokens = new ArrayList<>();

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                  "SELECT * FROM bu_api_token WHERE expire_date >= " + Dao.getInsertDateParameter() + ";"
              ) )
        {
            pstmt.setString( 1, Dao.formatDateToString( new Date() ) );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                while ( rs.next() )
                {
                    apiTokens.add( new ApiToken(
                        rs.getString( "api_token" ),
                        Dao.formatStringToDate( rs.getString( "expire_date" ) ),
                        Arrays.stream( rs.getString( "permissions" ).split( "," ) )
                            .map( ApiPermission::valueOf )
                            .collect( Collectors.toList() )
                    ) );
                }
            }
        }
        return apiTokens;
    }
}
