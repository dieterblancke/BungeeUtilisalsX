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

import static be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.punishments.BansDao.useServerPunishments;

public class SQLBansDao implements BansDao
{

    @Override
    public boolean isBanned( final UUID uuid, final String server )
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
    }

    @Override
    public boolean isIPBanned( final String ip, final String server )
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
    }

    @Override
    public boolean isBanned( final PunishmentType type, final UUID uuid, final String server )
    {
        if ( type.isIP() || !type.isBan() )
        {
            return false;
        }
        boolean exists = false;
        final String query;
        if ( useServerPunishments() )
        {
            query = "SELECT EXISTS(SELECT id FROM " + type.getTable() + " WHERE uuid = ?" +
                    " AND active = ? AND type = ? AND server = ?);";
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

            if ( useServerPunishments() )
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
    public boolean isIPBanned( final PunishmentType type, final String ip, final String server )
    {
        if ( !type.isBan() || !type.isIP() )
        {
            return false;
        }
        boolean exists = false;
        final String query;
        if ( useServerPunishments() )
        {
            query = "SELECT EXISTS(SELECT id FROM " + type.getTable() + " WHERE ip = ?" +
                    " AND active = ? AND type = ? AND server = ?);";
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

            if ( useServerPunishments() )
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
    public PunishmentInfo insertBan( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby )
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
        return PunishmentDao.buildPunishmentInfo( PunishmentType.BAN, uuid, user, ip, reason, server, executedby, new Date(), -1, active, null, punishmentUid );
    }

    @Override
    public PunishmentInfo insertIPBan( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby )
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
        return PunishmentDao.buildPunishmentInfo( PunishmentType.IPBAN, uuid, user, ip, reason, server, executedby, new Date(), -1, active, null, punishmentUid );
    }

    @Override
    public PunishmentInfo insertTempBan( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration )
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
        return PunishmentDao.buildPunishmentInfo( PunishmentType.TEMPBAN, uuid, user, ip, reason, server, executedby, new Date(), duration, active, null, punishmentUid );
    }

    @Override
    public PunishmentInfo insertTempIPBan( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration )
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
        return PunishmentDao.buildPunishmentInfo( PunishmentType.IPTEMPBAN, uuid, user, ip, reason, server, executedby, new Date(), duration, active, null, punishmentUid );
    }

    @Override
    public PunishmentInfo getCurrentBan( final UUID uuid, final String serverName )
    {
        PunishmentInfo info = new PunishmentInfo();
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
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.BAN );

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
    public PunishmentInfo getCurrentIPBan( final String ip, final String serverName )
    {
        PunishmentInfo info = new PunishmentInfo();
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
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.IPBAN );

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
    public void removeCurrentBan( final UUID uuid, final String removedBy, final String server )
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
    }

    @Override
    public void removeCurrentIPBan( final String ip, final String removedBy, final String server )
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
    }

    @Override
    public List<PunishmentInfo> getBans( final UUID uuid )
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
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.BAN );

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
    public List<PunishmentInfo> getBansExecutedBy( String name )
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
    public List<PunishmentInfo> getBans( final UUID uuid, final String serverName )
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
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.BAN );

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
    public List<PunishmentInfo> getIPBans( final String ip )
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
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.IPBAN );

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
    public List<PunishmentInfo> getIPBans( final String ip, final String serverName )
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
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.IPBAN );

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
                      "SELECT * FROM " + PunishmentType.BAN.getTable() + " WHERE id = ? LIMIT 1;"
              ) )
        {
            pstmt.setInt( 1, Integer.parseInt( id ) );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    final PunishmentType type = Utils.valueOfOr( rs.getString( "type" ), PunishmentType.BAN );

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
                      "SELECT * FROM " + PunishmentType.BAN.getTable() + " WHERE punishment_uid = ? LIMIT 1;"
              ) )
        {
            pstmt.setString( 1, punishmentUid );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
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
    public boolean isPunishmentUidFound( final String punishmentUid )
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
    }

    @Override
    public int softDeleteSince( final String user, final String removedBy, final Date date )
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
    }

    @Override
    public int hardDeleteSince( final String user, final Date date )
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
    }
}
