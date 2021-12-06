/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package be.dieterblancke.bungeeutilisalsx.common.storage.data.sql.dao.punishment;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.PunishmentDao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.punishments.MutesDao;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
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

public class SqlMutesDao implements MutesDao
{

    @Override
    public CompletableFuture<Boolean> isMuted( final UUID uuid, final String server )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            boolean exists = false;
            final String query;
            if ( PunishmentDao.useServerPunishments() )
            {
                query = "SELECT EXISTS(SELECT id FROM " + PunishmentType.MUTE.getTable() + " WHERE uuid = ?" +
                        " AND active = ? AND server = ? AND type NOT LIKE 'IP%');";
            }
            else
            {
                query = "SELECT EXISTS(SELECT id FROM " + PunishmentType.MUTE.getTable() + " WHERE uuid = ?" +
                        " AND active = ? AND type NOT LIKE 'IP%');";
            }

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement( query ) )
            {
                pstmt.setString( 1, uuid.toString() );
                pstmt.setBoolean( 2, true );

                if ( PunishmentDao.useServerPunishments() )
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
    public CompletableFuture<Boolean> isIPMuted( final String ip, final String server )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            boolean exists = false;
            final String query;
            if ( PunishmentDao.useServerPunishments() )
            {
                query = "SELECT EXISTS(SELECT id FROM " + PunishmentType.MUTE.getTable() + " WHERE ip = ?" +
                        " AND active = ? AND server = ? AND type LIKE 'IP%');";
            }
            else
            {
                query = "SELECT EXISTS(SELECT id FROM " + PunishmentType.MUTE.getTable() + " WHERE ip = ?" +
                        " AND active = ? AND type LIKE 'IP%');";
            }

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement( query ) )
            {
                pstmt.setString( 1, ip );
                pstmt.setBoolean( 2, true );

                if ( PunishmentDao.useServerPunishments() )
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
    public CompletableFuture<PunishmentInfo> insertMute( final UUID uuid,
                                                         final String user,
                                                         final String ip,
                                                         final String reason,
                                                         final String server,
                                                         final boolean active,
                                                         final String executedby )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final String punishmentUid = this.createUniqueMuteId();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "INSERT INTO " + PunishmentType.MUTE.getTable() + " (uuid, user, ip, reason, server, " +
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
                pstmt.setString( 9, PunishmentType.MUTE.toString() );
                pstmt.setString( 10, Dao.formatDateToString( new Date() ) );
                pstmt.setString( 11, punishmentUid );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }
            return PunishmentDao.buildPunishmentInfo(
                    PunishmentType.MUTE,
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
    public CompletableFuture<PunishmentInfo> insertIPMute( final UUID uuid,
                                                           final String user,
                                                           final String ip,
                                                           final String reason,
                                                           final String server,
                                                           final boolean active,
                                                           final String executedby )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final String punishmentUid = this.createUniqueMuteId();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "INSERT INTO " + PunishmentType.MUTE.getTable() + " (uuid, user, ip, reason, server, " +
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
                pstmt.setString( 9, PunishmentType.IPMUTE.toString() );
                pstmt.setString( 10, Dao.formatDateToString( new Date() ) );
                pstmt.setString( 11, punishmentUid );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }
            return PunishmentDao.buildPunishmentInfo(
                    PunishmentType.IPMUTE,
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
    public CompletableFuture<PunishmentInfo> insertTempMute( final UUID uuid,
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
            final String punishmentUid = this.createUniqueMuteId();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "INSERT INTO " + PunishmentType.MUTE.getTable() + " (uuid, user, ip, reason, server, " +
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
                pstmt.setString( 9, PunishmentType.TEMPMUTE.toString() );
                pstmt.setString( 10, Dao.formatDateToString( new Date() ) );
                pstmt.setString( 11, punishmentUid );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }
            return PunishmentDao.buildPunishmentInfo(
                    PunishmentType.TEMPMUTE,
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
    public CompletableFuture<PunishmentInfo> insertTempIPMute( final UUID uuid,
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
            final String punishmentUid = this.createUniqueMuteId();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "INSERT INTO " + PunishmentType.MUTE.getTable() + " (uuid, user, ip, reason, server, " +
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
                pstmt.setString( 9, PunishmentType.IPTEMPMUTE.toString() );
                pstmt.setString( 10, Dao.formatDateToString( new Date() ) );
                pstmt.setString( 11, punishmentUid );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }
            return PunishmentDao.buildPunishmentInfo(
                    PunishmentType.IPTEMPMUTE,
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
    public CompletableFuture<PunishmentInfo> getCurrentMute( final UUID uuid, final String serverName )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            PunishmentInfo info = null;
            final String query;
            if ( PunishmentDao.useServerPunishments() )
            {
                query = "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE uuid = ? AND active = ? AND server = ? AND type NOT LIKE 'IP%' LIMIT 1;";
            }
            else
            {
                query = "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE uuid = ? AND active = ? AND type NOT LIKE 'IP%' LIMIT 1;";
            }

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement( query ) )
            {
                pstmt.setString( 1, uuid.toString() );
                pstmt.setBoolean( 2, true );

                if ( PunishmentDao.useServerPunishments() )
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
    public CompletableFuture<PunishmentInfo> getCurrentIPMute( final String ip, final String serverName )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            PunishmentInfo info = null;
            final String query;
            if ( PunishmentDao.useServerPunishments() )
            {
                query = "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE ip = ? AND active = ? AND server = ? AND type LIKE 'IP%' LIMIT 1;";
            }
            else
            {
                query = "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE ip = ? AND active = ? AND type LIKE 'IP%' LIMIT 1;";
            }

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement( query ) )
            {
                pstmt.setString( 1, ip );
                pstmt.setBoolean( 2, true );

                if ( PunishmentDao.useServerPunishments() )
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
    public CompletableFuture<Void> removeCurrentMute( final UUID uuid, final String removedBy, final String server )
    {
        return CompletableFuture.runAsync( () ->
        {
            final String query;
            if ( PunishmentDao.useServerPunishments() )
            {
                query = "UPDATE " + PunishmentType.MUTE.getTable() + " SET active = ?, removed = ?, removed_by = ?, removed_at = " + Dao.getInsertDateParameter() +
                        " WHERE uuid = ? AND active = ? AND server = ? AND type NOT LIKE 'IP%';";
            }
            else
            {
                query = "UPDATE " + PunishmentType.MUTE.getTable() + " SET active = ?, removed = ?, removed_by = ?, removed_at = " + Dao.getInsertDateParameter() +
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

                if ( PunishmentDao.useServerPunishments() )
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
    public CompletableFuture<Void> removeCurrentIPMute( final String ip, final String removedBy, final String server )
    {
        return CompletableFuture.runAsync( () ->
        {
            final String query;
            if ( PunishmentDao.useServerPunishments() )
            {
                query = "UPDATE " + PunishmentType.MUTE.getTable() + " SET active = ?, removed = ?, removed_by = ?, removed_at = " + Dao.getInsertDateParameter() +
                        " WHERE ip = ? AND active = ? AND server = ? AND type LIKE 'IP%';";
            }
            else
            {
                query = "UPDATE " + PunishmentType.MUTE.getTable() + " SET active = ?, removed = ?, removed_by = ?, removed_at = " + Dao.getInsertDateParameter() +
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

                if ( PunishmentDao.useServerPunishments() )
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
    public CompletableFuture<List<PunishmentInfo>> getMutes( final UUID uuid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE uuid = ? AND type NOT LIKE 'IP%';"
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
    public CompletableFuture<List<PunishmentInfo>> getMutes( final UUID uuid, final String serverName )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE uuid = ? AND server = ? AND type NOT LIKE 'IP%';"
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
    public CompletableFuture<List<PunishmentInfo>> getMutesExecutedBy( final String name )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE executed_by = ?;"
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
    public CompletableFuture<List<PunishmentInfo>> getIPMutes( final String ip )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE ip = ? AND type LIKE 'IP%';"
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
    public CompletableFuture<List<PunishmentInfo>> getIPMutes( final String ip, final String serverName )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE ip = ? AND server = ? AND type LIKE 'IP%';"
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
    public CompletableFuture<List<PunishmentInfo>> getRecentMutes( final int limit )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.MUTE.getTable() + " LIMIT ?;"
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
                          "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE id = ? LIMIT 1;"
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
                          "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE punishment_uid = ? LIMIT 1;"
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
    public CompletableFuture<List<PunishmentInfo>> getActiveMutes( final UUID uuid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE uuid = ? AND active = ? AND type NOT LIKE 'IP%';"
                  ) )
            {
                pstmt.setString( 1, uuid.toString() );
                pstmt.setBoolean( 2, true );

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
    public CompletableFuture<List<PunishmentInfo>> getActiveIPMutes( final String ip )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentInfo> punishments = Lists.newArrayList();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE ip = ? AND active = ? AND type LIKE 'IP%';"
                  ) )
            {
                pstmt.setString( 1, ip );
                pstmt.setBoolean( 2, true );

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
    public CompletableFuture<Boolean> isPunishmentUidFound( final String punishmentUid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            boolean uidFound = false;

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE punishment_uid = ?;"
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
                          "update " + PunishmentType.MUTE.getTable() + " set active = ?, removed = ?, removed_by = ?, removed_at = " + Dao.getInsertDateParameter() + " where executed_by = ? and date >= ?;"
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
                          "delete from " + PunishmentType.MUTE.getTable() + " where executed_by = ? and date >= ?;"
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
        final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.MUTE );

        final int id = rs.getInt( "id" );
        final String user = rs.getString( "user" );
        final UUID uuid = UUID.fromString( rs.getString( "uuid" ) );
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
