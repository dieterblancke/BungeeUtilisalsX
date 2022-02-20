package be.dieterblancke.bungeeutilisalsx.common.storage.data.sql.dao;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.OfflineMessageDao;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;


public class SqlOfflineMessageDao implements OfflineMessageDao
{

    private static final Gson GSON = new Gson();

    @Override
    public CompletableFuture<List<OfflineMessage>> getOfflineMessages( final String username )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<OfflineMessage> messages = new ArrayList<>();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "select * from bu_offline_message where username = ? and active = ?;"
                  ) )
            {
                pstmt.setString( 1, username );
                pstmt.setBoolean( 2, true );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    while ( rs.next() )
                    {
                        messages.add( new OfflineMessage(
                                rs.getLong( "id" ),
                                rs.getString( "message" ),
                                GSON.fromJson( rs.getString( "parameters" ), Object[].class )
                        ) );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }

            return messages;
        } );
    }

    @Override
    public CompletableFuture<Void> sendOfflineMessage( final String username, final OfflineMessage message )
    {
        return CompletableFuture.runAsync( () ->
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "insert into bu_offline_message(username, message, parameters, active) values(?, ?, ?, ?);"
                  ) )
            {
                pstmt.setString( 1, username );
                pstmt.setString( 2, message.getLanguagePath() );
                pstmt.setString( 3, GSON.toJson( message.getPlaceholders() ) );
                pstmt.setBoolean( 4, true );

                pstmt.execute();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
        } );
    }

    @Override
    public CompletableFuture<Void> deleteOfflineMessage( final Long id )
    {
        return CompletableFuture.runAsync( () ->
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "delete from bu_offline_message where id = ?;"
                  ) )
            {
                pstmt.setLong( 1, id );

                pstmt.execute();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
        } );
    }
}
