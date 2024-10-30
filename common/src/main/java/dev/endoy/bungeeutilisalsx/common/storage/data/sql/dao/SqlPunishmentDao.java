package dev.endoy.bungeeutilisalsx.common.storage.data.sql.dao;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentType;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.Dao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.PunishmentDao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments.BansDao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments.KickAndWarnDao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments.MutesDao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments.TracksDao;
import dev.endoy.bungeeutilisalsx.common.storage.data.sql.dao.punishment.SqlBansDao;
import dev.endoy.bungeeutilisalsx.common.storage.data.sql.dao.punishment.SqlKickAndWarnDao;
import dev.endoy.bungeeutilisalsx.common.storage.data.sql.dao.punishment.SqlMutesDao;
import dev.endoy.bungeeutilisalsx.common.storage.data.sql.dao.punishment.SqlTracksDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;


public class SqlPunishmentDao implements PunishmentDao
{

    private final BansDao bansDao;
    private final MutesDao mutesDao;
    private final KickAndWarnDao kickAndWarnDao;
    private final TracksDao tracksDao;

    public SqlPunishmentDao()
    {
        this.bansDao = new SqlBansDao();
        this.mutesDao = new SqlMutesDao();
        this.kickAndWarnDao = new SqlKickAndWarnDao();
        this.tracksDao = new SqlTracksDao();
    }

    @Override
    public BansDao getBansDao()
    {
        return bansDao;
    }

    @Override
    public MutesDao getMutesDao()
    {
        return mutesDao;
    }

    @Override
    public KickAndWarnDao getKickAndWarnDao()
    {
        return kickAndWarnDao;
    }

    @Override
    public TracksDao getTracksDao()
    {
        return tracksDao;
    }

    @Override
    public CompletableFuture<Long> getPunishmentsSince( PunishmentType type, UUID uuid, Date date )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            long count = 0;

            if ( type.isActivatable() )
            {
                try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                      PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT COUNT(id) FROM " + type.getTable() + " WHERE uuid = ? AND date >= " + Dao.getInsertDateParameter() + " AND type = ? AND punishmentaction_status = ?;"
                      ) )
                {
                    pstmt.setString( 1, uuid.toString() );
                    pstmt.setString( 2, Dao.formatDateToString( date ) );
                    pstmt.setString( 3, type.toString() );
                    pstmt.setBoolean( 4, false );

                    try ( ResultSet rs = pstmt.executeQuery() )
                    {
                        if ( rs.next() )
                        {
                            count = rs.getLong( 1 );
                        }
                    }
                }
                catch ( SQLException e )
                {
                    BuX.getLogger().log( Level.SEVERE, "An error occured", e );
                }
            }
            else
            {
                try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                      PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT COUNT(id) FROM " + type.getTable() + " WHERE uuid = ? AND date >= " + Dao.getInsertDateParameter() + " AND punishmentaction_status = ?;"
                      ) )
                {
                    pstmt.setString( 1, uuid.toString() );
                    pstmt.setString( 2, Dao.formatDateToString( date ) );
                    pstmt.setBoolean( 3, false );

                    try ( ResultSet rs = pstmt.executeQuery() )
                    {
                        if ( rs.next() )
                        {
                            count = rs.getLong( 1 );
                        }
                    }
                }
                catch ( SQLException e )
                {
                    BuX.getLogger().log( Level.SEVERE, "An error occured", e );
                }
            }

            return count;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Long> getIPPunishmentsSince( PunishmentType type, String ip, Date date )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            long count = 0;

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT COUNT(id) FROM " + type.getTable() + " WHERE ip = ? AND date >= " + Dao.getInsertDateParameter() + " AND type = ? AND punishmentaction_status = ?;"
                  ) )
            {
                pstmt.setString( 1, ip );
                pstmt.setString( 2, Dao.formatDateToString( date ) );
                pstmt.setString( 3, type.toString() );
                pstmt.setBoolean( 4, false );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    if ( rs.next() )
                    {
                        count = rs.getLong( 1 );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
            return count;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> updateActionStatus( int limit, PunishmentType type, UUID uuid, Date date )
    {
        return CompletableFuture.runAsync( () ->
        {
            if ( type.isActivatable() )
            {
                try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                      PreparedStatement pstmt = connection.prepareStatement(
                          "UPDATE " + type.getTable() + " SET punishmentaction_status = ? WHERE uuid = ? AND date >= " + Dao.getInsertDateParameter() + " AND type = ? AND punishmentaction_status = ? LIMIT ?;"
                      ) )
                {
                    pstmt.setBoolean( 1, true );
                    pstmt.setString( 2, uuid.toString() );
                    pstmt.setString( 3, Dao.formatDateToString( date ) );
                    pstmt.setString( 4, type.toString() );
                    pstmt.setBoolean( 5, false );
                    pstmt.setInt( 6, limit );

                    pstmt.executeUpdate();
                }
                catch ( SQLException e )
                {
                    BuX.getLogger().log( Level.SEVERE, "An error occured", e );
                }
            }
            else
            {
                try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                      PreparedStatement pstmt = connection.prepareStatement(
                          "UPDATE " + type.getTable() + " SET punishmentaction_status = ? WHERE uuid = ? AND date >= " + Dao.getInsertDateParameter() + " AND punishmentaction_status = ? LIMIT ?;"
                      ) )
                {
                    pstmt.setBoolean( 1, true );
                    pstmt.setString( 2, uuid.toString() );
                    pstmt.setString( 3, Dao.formatDateToString( date ) );
                    pstmt.setBoolean( 4, false );
                    pstmt.setInt( 5, limit );

                    pstmt.executeUpdate();
                }
                catch ( SQLException e )
                {
                    BuX.getLogger().log( Level.SEVERE, "An error occured", e );
                }
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> updateIPActionStatus( int limit, PunishmentType type, String ip, Date date )
    {
        return CompletableFuture.runAsync( () ->
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                      "UPDATE " + type.getTable() + " SET punishmentaction_status = ? WHERE ip = ? AND date >= " + Dao.getInsertDateParameter() + " AND type = ? AND punishmentaction_status = ? LIMIT ?;"
                  ) )
            {
                pstmt.setBoolean( 1, true );
                pstmt.setString( 2, ip );
                pstmt.setString( 3, Dao.formatDateToString( date ) );
                pstmt.setString( 4, type.toString() );
                pstmt.setBoolean( 5, false );
                pstmt.setInt( 6, limit );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> savePunishmentAction( UUID uuid, String username, String ip, String uid )
    {
        return CompletableFuture.runAsync( () ->
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                      "INSERT INTO bu_punishmentactions (uuid, user, ip, actionid, date) VALUES (?, ?, ?, ?, " + Dao.getInsertDateParameter() + ");"
                  ) )
            {
                pstmt.setString( 1, uuid.toString() );
                pstmt.setString( 2, username );
                pstmt.setString( 3, ip );
                pstmt.setString( 4, uid );
                pstmt.setString( 5, Dao.formatDateToString( new Date() ) );

                pstmt.execute();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }
}
