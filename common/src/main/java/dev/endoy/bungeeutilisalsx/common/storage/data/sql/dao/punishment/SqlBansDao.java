package dev.endoy.bungeeutilisalsx.common.storage.data.sql.dao.punishment;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentType;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.Dao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.PunishmentDao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments.BansDao;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import com.google.common.collect.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

import static dev.endoy.bungeeutilisalsx.common.api.storage.dao.PunishmentDao.useServerPunishments;

public class SqlBansDao implements BansDao
{

    @Override
    public CompletableFuture<Boolean> isBanned( final UUID uuid, final String server )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            boolean exists = false;
            final String query;
            if ( useServerPunishments() )
            {
                query = "SELECT EXISTS(SELECT id FROM " + PunishmentType.BAN.getTable() + " WHERE uuid = ?" +
                        " AND active = ? AND server = ? AND type NOT LIKE 'IP%');";
            }
            else
            {
                query = "SELECT EXISTS(SELECT id FROM " + PunishmentType.BAN.getTable() + " WHERE uuid = ?" +
                        " AND active = ? AND type NOT LIKE 'IP%');";
            }

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement( query ) )
            {
                pstmt.setString( 1, uuid.toString() );
                pstmt.setBoolean( 2, true );

                if ( useServerPunishments() )
                {
                    pstmt.setString( 3, server );
                }

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    if ( rs.next() )
                    {
                        exists = rs.getBoolean( 1 );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }

            return exists;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Boolean> isIPBanned( final String ip, final String server )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            boolean exists = false;
            final String query;
            if ( useServerPunishments() )
            {
                query = "SELECT EXISTS(SELECT id FROM " + PunishmentType.BAN.getTable() + " WHERE ip = ?" +
                        " AND active = ? AND server = ? AND type LIKE 'IP%');";
            }
            else
            {
                query = "SELECT EXISTS(SELECT id FROM " + PunishmentType.BAN.getTable() + " WHERE ip = ?" +
                        " AND active = ? AND type LIKE 'IP%');";
            }

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement( query ) )
            {
                pstmt.setString( 1, ip );
                pstmt.setBoolean( 2, true );

                if ( useServerPunishments() )
                {
                    pstmt.setString( 3, server );
                }

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    if ( rs.next() )
                    {
                        exists = rs.getBoolean( 1 );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }

            return exists;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<PunishmentInfo> insertBan( final UUID uuid,
                                                        final String user,
                                                        final String ip,
                                                        final String reason,
                                                        final String server,
                                                        final boolean active,
                                                        final String executedby )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final String punishmentUid = this.createUniqueBanId();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "INSERT INTO " + PunishmentType.BAN.getTable() + " (uuid, user, ip, reason, server, " +
                                  "active, executed_by, duration, type, date, punishment_uid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, " + Dao.getInsertDateParameter() + ", ?);"
                  ) )
            {
                pstmt.setString( 1, uuid.toString() );
                pstmt.setString( 2, user );
                pstmt.setString( 3, ip );
                pstmt.setString( 4, reason );
                pstmt.setString( 5, server );
                pstmt.setBoolean( 6, active );
                pstmt.setString( 7, executedby );
                pstmt.setLong( 8, -1 );
                pstmt.setString( 9, PunishmentType.BAN.toString() );
                pstmt.setString( 10, Dao.formatDateToString( new Date() ) );
                pstmt.setString( 11, punishmentUid );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }
            return PunishmentDao.buildPunishmentInfo(
                    PunishmentType.BAN,
                    uuid,
                    user,
                    ip,
                    reason,
                    server,
                    executedby,
                    new Date(),
                    -1,
                    active,
                    null,
                    punishmentUid
            );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<PunishmentInfo> insertIPBan( final UUID uuid,
                                                          final String user,
                                                          final String ip,
                                                          final String reason,
                                                          final String server,
                                                          final boolean active,
                                                          final String executedby )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final String punishmentUid = this.createUniqueBanId();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "INSERT INTO " + PunishmentType.BAN.getTable() + " (uuid, user, ip, reason, server, " +
                                  "active, executed_by, duration, type, date, punishment_uid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, " + Dao.getInsertDateParameter() + ", ?);"
                  ) )
            {
                pstmt.setString( 1, uuid.toString() );
                pstmt.setString( 2, user );
                pstmt.setString( 3, ip );
                pstmt.setString( 4, reason );
                pstmt.setString( 5, server );
                pstmt.setBoolean( 6, active );
                pstmt.setString( 7, executedby );
                pstmt.setLong( 8, -1 );
                pstmt.setString( 9, PunishmentType.IPBAN.toString() );
                pstmt.setString( 10, Dao.formatDateToString( new Date() ) );
                pstmt.setString( 11, punishmentUid );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }
            return PunishmentDao.buildPunishmentInfo(
                    PunishmentType.IPBAN,
                    uuid,
                    user,
                    ip,
                    reason,
                    server,
                    executedby,
                    new Date(),
                    -1,
                    active,
                    null,
                    punishmentUid
            );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<PunishmentInfo> insertTempBan( final UUID uuid,
                                                            final String user,
                                                            final String ip,
                                                            final String reason,
                                                            final String server,
                                                            final boolean active,
                                                            final String executedby,
                                                            final long duration )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final String punishmentUid = this.createUniqueBanId();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "INSERT INTO " + PunishmentType.BAN.getTable() + " (uuid, user, ip, reason, server, " +
                                  "active, executed_by, duration, type, date, punishment_uid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, " + Dao.getInsertDateParameter() + ", ?);"
                  ) )
            {
                pstmt.setString( 1, uuid.toString() );
                pstmt.setString( 2, user );
                pstmt.setString( 3, ip );
                pstmt.setString( 4, reason );
                pstmt.setString( 5, server );
                pstmt.setBoolean( 6, active );
                pstmt.setString( 7, executedby );
                pstmt.setLong( 8, duration );
                pstmt.setString( 9, PunishmentType.TEMPBAN.toString() );
                pstmt.setString( 10, Dao.formatDateToString( new Date() ) );
                pstmt.setString( 11, punishmentUid );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }
            return PunishmentDao.buildPunishmentInfo(
                    PunishmentType.TEMPBAN,
                    uuid,
                    user,
                    ip,
                    reason,
                    server,
                    executedby,
                    new Date(),
                    duration,
                    active,
                    null,
                    punishmentUid
            );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<PunishmentInfo> insertTempIPBan( final UUID uuid,
                                                              final String user,
                                                              final String ip,
                                                              final String reason,
                                                              final String server,
                                                              final boolean active,
                                                              final String executedby,
                                                              final long duration )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final String punishmentUid = this.createUniqueBanId();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "INSERT INTO " + PunishmentType.BAN.getTable() + " (uuid, user, ip, reason, server, " +
                                  "active, executed_by, duration, type, date, punishment_uid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, " + Dao.getInsertDateParameter() + ", ?);"
                  ) )
            {
                pstmt.setString( 1, uuid.toString() );
                pstmt.setString( 2, user );
                pstmt.setString( 3, ip );
                pstmt.setString( 4, reason );
                pstmt.setString( 5, server );
                pstmt.setBoolean( 6, active );
                pstmt.setString( 7, executedby );
                pstmt.setLong( 8, duration );
                pstmt.setString( 9, PunishmentType.IPTEMPBAN.toString() );
                pstmt.setString( 10, Dao.formatDateToString( new Date() ) );
                pstmt.setString( 11, punishmentUid );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }
            return PunishmentDao.buildPunishmentInfo(
                    PunishmentType.IPTEMPBAN,
                    uuid,
                    user,
                    ip,
                    reason,
                    server,
                    executedby,
                    new Date(),
                    duration,
                    active,
                    null,
                    punishmentUid
            );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<PunishmentInfo> getCurrentBan( final UUID uuid, final String serverName )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            PunishmentInfo info = null;
            final String query;

            if ( useServerPunishments() )
            {
                query = "SELECT * FROM " + PunishmentType.BAN.getTable() + " WHERE uuid = ? AND active = ? AND server = ? AND type NOT LIKE 'IP%' LIMIT 1;";
            }
            else
            {
                query = "SELECT * FROM " + PunishmentType.BAN.getTable() + " WHERE uuid = ? AND active = ? AND type NOT LIKE 'IP%' LIMIT 1;";
            }

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement( query ) )
            {
                pstmt.setString( 1, uuid.toString() );
                pstmt.setBoolean( 2, true );

                if ( useServerPunishments() )
                {
                    pstmt.setString( 3, serverName );
                }

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    if ( rs.next() )
                    {
                        info = this.buildPunishmentInfo( rs );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }

            return info;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<PunishmentInfo> getCurrentIPBan( final String ip, final String serverName )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            PunishmentInfo info = null;
            final String query;

            if ( useServerPunishments() )
            {
                query = "SELECT * FROM " + PunishmentType.BAN.getTable() + " WHERE ip = ? AND active = ? AND server = ? AND type LIKE 'IP%' LIMIT 1;";
            }
            else
            {
                query = "SELECT * FROM " + PunishmentType.BAN.getTable() + " WHERE ip = ? AND active = ? AND type LIKE 'IP%' LIMIT 1;";
            }

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement( query ) )
            {
                pstmt.setString( 1, ip );
                pstmt.setBoolean( 2, true );

                if ( useServerPunishments() )
                {
                    pstmt.setString( 3, serverName );
                }

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    if ( rs.next() )
                    {
                        info = this.buildPunishmentInfo( rs );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }

            return info;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> removeCurrentBan( final UUID uuid, final String removedBy, final String server )
    {
        return CompletableFuture.runAsync( () ->
        {
            final String query;

            if ( useServerPunishments() )
            {
                query = "UPDATE " + PunishmentType.BAN.getTable() + " SET active = ?, removed = ?, removed_by = ?, removed_at = " + Dao.getInsertDateParameter() +
                        " WHERE uuid = ? AND active = ? AND server = ? AND type NOT LIKE 'IP%';";
            }
            else
            {
                query = "UPDATE " + PunishmentType.BAN.getTable() + " SET active = ?, removed = ?, removed_by = ?, removed_at = " + Dao.getInsertDateParameter() +
                        " WHERE uuid = ? AND active = ? AND type NOT LIKE 'IP%';";
            }

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement( query ) )
            {
                pstmt.setBoolean( 1, false );
                pstmt.setBoolean( 2, true );
                pstmt.setString( 3, removedBy );
                pstmt.setString( 4, Dao.formatDateToString( new Date() ) );
                pstmt.setString( 5, uuid.toString() );
                pstmt.setBoolean( 6, true );

                if ( useServerPunishments() )
                {
                    pstmt.setString( 7, server );
                }

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> removeCurrentIPBan( final String ip, final String removedBy, final String server )
    {
        return CompletableFuture.runAsync( () ->
        {
            final String query;

            if ( useServerPunishments() )
            {
                query = "UPDATE " + PunishmentType.BAN.getTable() + " SET active = ?, removed = ?, removed_by = ?, removed_at = " + Dao.getInsertDateParameter() +
                        " WHERE ip = ? AND active = ? AND server = ? AND type LIKE 'IP%';";
            }
            else
            {
                query = "UPDATE " + PunishmentType.BAN.getTable() + " SET active = ?, removed = ?, removed_by = ?, removed_at = " + Dao.getInsertDateParameter() +
                        " WHERE ip = ? AND active = ? AND type LIKE 'IP%';";
            }
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement( query ) )
            {
                pstmt.setBoolean( 1, false );
                pstmt.setBoolean( 2, true );
                pstmt.setString( 3, removedBy );
                pstmt.setString( 4, Dao.formatDateToString( new Date() ) );
                pstmt.setString( 5, ip );
                pstmt.setBoolean( 6, true );

                if ( useServerPunishments() )
                {
                    pstmt.setString( 7, server );
                }

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<PunishmentInfo>> getBans( final UUID uuid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.BAN.getTable() + " WHERE uuid = ? AND type NOT LIKE 'IP%';"
                  ) )
            {
                pstmt.setString( 1, uuid.toString() );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    while ( rs.next() )
                    {
                        punishments.add( this.buildPunishmentInfo( rs ) );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }

            return punishments;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<PunishmentInfo>> getBansExecutedBy( final String name )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.BAN.getTable() + " WHERE executed_by = ?;"
                  ) )
            {
                pstmt.setString( 1, name );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    while ( rs.next() )
                    {
                        punishments.add( this.buildPunishmentInfo( rs ) );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }

            return punishments;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<PunishmentInfo>> getBans( final UUID uuid, final String serverName )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.BAN.getTable() + " WHERE uuid = ? AND server = ? AND type NOT LIKE 'IP%';"
                  ) )
            {
                pstmt.setString( 1, uuid.toString() );
                pstmt.setString( 2, serverName );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    while ( rs.next() )
                    {
                        punishments.add( this.buildPunishmentInfo( rs ) );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }

            return punishments;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<PunishmentInfo>> getIPBans( final String ip )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.IPBAN.getTable() + " WHERE ip = ? AND type LIKE 'IP%';"
                  ) )
            {
                pstmt.setString( 1, ip );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    while ( rs.next() )
                    {
                        punishments.add( this.buildPunishmentInfo( rs ) );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }

            return punishments;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<PunishmentInfo>> getIPBans( final String ip, final String serverName )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.IPBAN.getTable() + " WHERE ip = ? AND server = ? AND type LIKE 'IP%';"
                  ) )
            {
                pstmt.setString( 1, ip );
                pstmt.setString( 2, serverName );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    while ( rs.next() )
                    {
                        punishments.add( this.buildPunishmentInfo( rs ) );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }

            return punishments;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<PunishmentInfo>> getRecentBans( final int limit )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.BAN.getTable() + " LIMIT ?;"
                  ) )
            {
                pstmt.setInt( 1, Math.min( limit, 200 ) );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    while ( rs.next() )
                    {
                        punishments.add( this.buildPunishmentInfo( rs ) );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }

            return punishments;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<PunishmentInfo> getById( final String id )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            PunishmentInfo info = null;

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.BAN.getTable() + " WHERE id = ? LIMIT 1;"
                  ) )
            {
                pstmt.setInt( 1, Integer.parseInt( id ) );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    if ( rs.next() )
                    {
                        info = this.buildPunishmentInfo( rs );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }

            return info;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<PunishmentInfo> getByPunishmentId( final String punishmentUid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            PunishmentInfo info = null;

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.BAN.getTable() + " WHERE punishment_uid = ? LIMIT 1;"
                  ) )
            {
                pstmt.setString( 1, punishmentUid );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    if ( rs.next() )
                    {
                        info = this.buildPunishmentInfo( rs );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }

            return info;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Boolean> isPunishmentUidFound( final String punishmentUid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            boolean uidFound = false;

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.BAN.getTable() + " WHERE punishment_uid = ?;"
                  ) )
            {
                pstmt.setString( 1, punishmentUid );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    if ( rs.next() )
                    {
                        uidFound = true;
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }
            return uidFound;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Integer> softDeleteSince( final String user, final String removedBy, final Date date )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            int records = 0;
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "update " + PunishmentType.BAN.getTable() + " set active = ?, removed = ?, removed_by = ?, removed_at = " + Dao.getInsertDateParameter() + " where executed_by = ? and date >= ?;"
                  ) )
            {
                pstmt.setBoolean( 1, false );
                pstmt.setBoolean( 2, true );
                pstmt.setString( 3, removedBy );
                pstmt.setString( 4, Dao.formatDateToString( new Date() ) );
                pstmt.setString( 5, user );
                pstmt.setString( 6, Dao.formatDateToString( date ) );
                records = pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }
            return records;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Integer> hardDeleteSince( final String user, final Date date )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            int records = 0;
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "delete from " + PunishmentType.BAN.getTable() + " where executed_by = ? and date >= ?;"
                  ) )
            {
                pstmt.setString( 1, user );
                pstmt.setString( 2, Dao.formatDateToString( date ) );
                records = pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }
            return records;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    private PunishmentInfo buildPunishmentInfo( final ResultSet rs ) throws SQLException
    {
        final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.BAN );

        final int id = rs.getInt( "id" );
        final UUID uuid = UUID.fromString( rs.getString( "uuid" ) );
        final String user = rs.getString( "user" );
        final String ip = rs.getString( "ip" );
        final String reason = rs.getString( "reason" );
        final String server = rs.getString( "server" );
        final String executedby = rs.getString( "executed_by" );
        final Date date = Dao.formatStringToDate( rs.getString( "date" ) );
        final long time = rs.getLong( "duration" );
        final boolean active = rs.getBoolean( "active" );
        final String removedby = rs.getString( "removed_by" );
        final String punishmentUid = rs.getString( "punishment_uid" );

        return PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, punishmentUid );
    }
}
