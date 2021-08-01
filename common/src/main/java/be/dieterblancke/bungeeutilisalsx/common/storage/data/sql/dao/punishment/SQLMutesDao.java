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
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.punishments.BansDao;
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
import java.util.logging.Level;

public class SQLMutesDao implements MutesDao
{

    @Override
    public boolean isMuted( final UUID uuid, final String server )
    {
        boolean exists = false;
        final String query;
        if ( BansDao.useServerPunishments() )
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

            if ( BansDao.useServerPunishments() )
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
    }

    @Override
    public boolean isIPMuted( final String ip, final String server )
    {
        boolean exists = false;
        final String query;
        if ( BansDao.useServerPunishments() )
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

            if ( BansDao.useServerPunishments() )
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
    }

    @Override
    public boolean isMuted( final PunishmentType type, final UUID uuid, final String server )
    {
        if ( type.isIP() || !type.isMute() )
        {
            return false;
        }
        boolean exists = false;
        final String query;
        if ( BansDao.useServerPunishments() )
        {
            query = "SELECT EXISTS(SELECT id FROM " + type.getTable() + " WHERE uuid = ?" +
                    " AND active = ? AND server = ? AND type = ?);";
        }
        else
        {
            query = "SELECT EXISTS(SELECT id FROM " + type.getTable() + " WHERE uuid = ?" +
                    " AND active = ? AND type = ?);";
        }

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( query ) )
        {
            pstmt.setString( 1, uuid.toString() );
            pstmt.setBoolean( 2, true );
            pstmt.setString( 3, type.toString() );

            if ( BansDao.useServerPunishments() )
            {
                pstmt.setString( 4, server );
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
    }

    @Override
    public boolean isIPMuted( final PunishmentType type, final String ip, final String server )
    {
        if ( !type.isMute() || !type.isIP() )
        {
            return false;
        }
        boolean exists = false;
        final String query;
        if ( BansDao.useServerPunishments() )
        {
            query = "SELECT EXISTS(SELECT id FROM " + type.getTable() + " WHERE ip = ?" +
                    " AND active = ? AND server = ? AND type = ?);";
        }
        else
        {
            query = "SELECT EXISTS(SELECT id FROM " + type.getTable() + " WHERE ip = ?" +
                    " AND active = ? AND type = ?);";
        }

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( query ) )
        {
            pstmt.setString( 1, ip );
            pstmt.setBoolean( 2, true );
            pstmt.setString( 3, type.toString() );

            if ( BansDao.useServerPunishments() )
            {
                pstmt.setString( 4, server );
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
    }

    @Override
    public PunishmentInfo insertMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby )
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
        return PunishmentDao.buildPunishmentInfo( PunishmentType.MUTE, uuid, user, ip, reason, server, executedby, new Date(), -1, active, null, punishmentUid );
    }

    @Override
    public PunishmentInfo insertIPMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby )
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
        return PunishmentDao.buildPunishmentInfo( PunishmentType.IPMUTE, uuid, user, ip, reason, server, executedby, new Date(), -1, active, null, punishmentUid );
    }

    @Override
    public PunishmentInfo insertTempMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration )
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
        return PunishmentDao.buildPunishmentInfo( PunishmentType.TEMPMUTE, uuid, user, ip, reason, server, executedby, new Date(), duration, active, null, punishmentUid );
    }

    @Override
    public PunishmentInfo insertTempIPMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration )
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
        return PunishmentDao.buildPunishmentInfo( PunishmentType.IPTEMPMUTE, uuid, user, ip, reason, server, executedby, new Date(), duration, active, null, punishmentUid );
    }

    @Override
    public PunishmentInfo getCurrentMute( final UUID uuid, final String serverName )
    {
        PunishmentInfo info = new PunishmentInfo();
        final String query;
        if ( BansDao.useServerPunishments() )
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

            if ( BansDao.useServerPunishments() )
            {
                pstmt.setString( 3, serverName );
            }

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.MUTE );

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

                    info = PunishmentDao.buildPunishmentInfo( type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, punishmentUid );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
        }

        return info;
    }

    @Override
    public PunishmentInfo getCurrentIPMute( final String ip, final String serverName )
    {
        PunishmentInfo info = new PunishmentInfo();
        final String query;
        if ( BansDao.useServerPunishments() )
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

            if ( BansDao.useServerPunishments() )
            {
                pstmt.setString( 3, serverName );
            }

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.IPMUTE );

                    final UUID uuid = UUID.fromString( rs.getString( "uuid" ) );
                    final String user = rs.getString( "user" );
                    final String reason = rs.getString( "reason" );
                    final String server = rs.getString( "server" );
                    final String executedby = rs.getString( "executed_by" );
                    final Date date = Dao.formatStringToDate( rs.getString( "date" ) );
                    final long time = rs.getLong( "duration" );
                    final boolean active = rs.getBoolean( "active" );
                    final String removedby = rs.getString( "removed_by" );
                    final String punishmentUid = rs.getString( "punishment_uid" );

                    info = PunishmentDao.buildPunishmentInfo( type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, punishmentUid );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
        }

        return info;
    }

    @Override
    public void removeCurrentMute( final UUID uuid, final String removedBy, final String server )
    {
        final String query;
        if ( BansDao.useServerPunishments() )
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

            if ( BansDao.useServerPunishments() )
            {
                pstmt.setString( 7, server );
            }

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
        }
    }

    @Override
    public void removeCurrentIPMute( final String ip, final String removedBy, final String server )
    {
        final String query;
        if ( BansDao.useServerPunishments() )
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

            if ( BansDao.useServerPunishments() )
            {
                pstmt.setString( 7, server );
            }

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
        }
    }

    @Override
    public List<PunishmentInfo> getMutes( final UUID uuid )
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
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.MUTE );

                    final int id = rs.getInt( "id" );
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

                    punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, punishmentUid ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
        }

        return punishments;
    }

    @Override
    public List<PunishmentInfo> getMutes( final UUID uuid, final String serverName )
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
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.MUTE );

                    final int id = rs.getInt( "id" );
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

                    punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, punishmentUid ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
        }

        return punishments;
    }

    @Override
    public List<PunishmentInfo> getMutesExecutedBy( String name )
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
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.MUTE );

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

                    punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, punishmentUid ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
        }

        return punishments;
    }

    @Override
    public List<PunishmentInfo> getIPMutes( final String ip )
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
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.IPMUTE );

                    final int id = rs.getInt( "id" );
                    final UUID uuid = UUID.fromString( rs.getString( "uuid" ) );
                    final String user = rs.getString( "user" );
                    final String reason = rs.getString( "reason" );
                    final String server = rs.getString( "server" );
                    final String executedby = rs.getString( "executed_by" );
                    final Date date = Dao.formatStringToDate( rs.getString( "date" ) );
                    final long time = rs.getLong( "duration" );
                    final boolean active = rs.getBoolean( "active" );
                    final String removedby = rs.getString( "removed_by" );
                    final String punishmentUid = rs.getString( "punishment_uid" );

                    punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, punishmentUid ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
        }

        return punishments;
    }

    @Override
    public List<PunishmentInfo> getIPMutes( final String ip, final String serverName )
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
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.IPMUTE );

                    final int id = rs.getInt( "id" );
                    final UUID uuid = UUID.fromString( rs.getString( "uuid" ) );
                    final String user = rs.getString( "user" );
                    final String reason = rs.getString( "reason" );
                    final String server = rs.getString( "server" );
                    final String executedby = rs.getString( "executed_by" );
                    final Date date = Dao.formatStringToDate( rs.getString( "date" ) );
                    final long time = rs.getLong( "duration" );
                    final boolean active = rs.getBoolean( "active" );
                    final String removedby = rs.getString( "removed_by" );
                    final String punishmentUid = rs.getString( "punishment_uid" );

                    punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, punishmentUid ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
        }

        return punishments;
    }

    @Override
    public PunishmentInfo getById( String id )
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
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.MUTE );

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

                    info = PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, punishmentUid );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
        }

        return info;
    }

    @Override
    public PunishmentInfo getByPunishmentId( String punishmentUid )
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
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.MUTE );

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

                    info = PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, punishmentUid );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
        }

        return info;
    }

    @Override
    public List<PunishmentInfo> getActiveMutes( UUID uuid )
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
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.MUTE );

                    final int id = rs.getInt( "id" );
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

                    punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, punishmentUid ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
        }

        return punishments;
    }

    @Override
    public List<PunishmentInfo> getActiveIPMutes( String ip )
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
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.IPMUTE );

                    final int id = rs.getInt( "id" );
                    final UUID uuid = UUID.fromString( rs.getString( "uuid" ) );
                    final String user = rs.getString( "user" );
                    final String reason = rs.getString( "reason" );
                    final String server = rs.getString( "server" );
                    final String executedby = rs.getString( "executed_by" );
                    final Date date = Dao.formatStringToDate( rs.getString( "date" ) );
                    final long time = rs.getLong( "duration" );
                    final boolean active = rs.getBoolean( "active" );
                    final String removedby = rs.getString( "removed_by" );
                    final String punishmentUid = rs.getString( "punishment_uid" );

                    punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, punishmentUid ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
        }

        return punishments;
    }

    @Override
    public boolean isPunishmentUidFound( final String punishmentUid )
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
    }

    @Override
    public int softDeleteSince( final String user, final String removedBy, final Date date )
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
    }

    @Override
    public int hardDeleteSince( final String user, final Date date )
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
    }
}
