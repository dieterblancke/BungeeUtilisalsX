package dev.endoy.bungeeutilisalsx.common.storage.data.sql.dao.punishment;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentTrackInfo;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.Dao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments.TracksDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments.TracksDao.useServerPunishments;

public class SqlTracksDao implements TracksDao
{

    @Override
    public CompletableFuture<List<PunishmentTrackInfo>> getTrackInfos( final UUID uuid, final String trackId, final String server )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentTrackInfo> trackInfos = new ArrayList<>();
            final String query;

            if ( useServerPunishments() )
            {
                query = "SELECT * FROM bu_punishmenttracks WHERE uuid = ? AND track_id = ? AND active = ? AND server = ?;";
            }
            else
            {
                query = "SELECT * FROM bu_punishmenttracks WHERE uuid = ? AND track_id = ? AND active = ?;";
            }

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement( query ) )
            {
                pstmt.setString( 1, uuid.toString() );
                pstmt.setString( 2, trackId );
                pstmt.setBoolean( 3, true );

                if ( useServerPunishments() )
                {
                    pstmt.setString( 4, server );
                }

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    while ( rs.next() )
                    {
                        trackInfos.add( new PunishmentTrackInfo(
                            UUID.fromString( rs.getString( "uuid" ) ),
                            rs.getString( "track_id" ),
                            rs.getString( "server" ),
                            rs.getString( "executed_by" ),
                            Dao.formatStringToDate( rs.getString( "date" ) ),
                            rs.getBoolean( "active" )
                        ) );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }

            return trackInfos;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> addToTrack( final PunishmentTrackInfo trackInfo )
    {
        return CompletableFuture.runAsync( () ->
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                      "INSERT INTO bu_punishmenttracks (uuid, track_id, server, executed_by, date, active) VALUES (?, ?, ?, ?, " + Dao.getInsertDateParameter() + ", ?);"
                  ) )
            {
                pstmt.setString( 1, trackInfo.getUuid().toString() );
                pstmt.setString( 2, trackInfo.getTrackId() );
                pstmt.setString( 3, trackInfo.getServer() );
                pstmt.setString( 4, trackInfo.getExecutedBy() );
                pstmt.setString( 5, Dao.formatDateToString( trackInfo.getDate() ) );
                pstmt.setBoolean( 6, trackInfo.isActive() );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> resetTrack( final UUID uuid, final String trackId, final String server )
    {
        return CompletableFuture.runAsync( () ->
        {
            final String query;

            if ( useServerPunishments() )
            {
                query = "DELETE FROM bu_punishmenttracks WHERE uuid = ? AND track_id = ? AND active = ? AND server = ?;";
            }
            else
            {
                query = "DELETE FROM bu_punishmenttracks WHERE uuid = ? AND track_id = ? AND active = ?;";
            }

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement( query ) )
            {
                pstmt.setString( 1, uuid.toString() );
                pstmt.setString( 2, trackId );
                pstmt.setBoolean( 3, true );

                if ( useServerPunishments() )
                {
                    pstmt.setString( 4, server );
                }

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }
}
