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

package com.dbsoftwares.bungeeutilisals.storage.data.sql.dao.punishment;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.MutesDao;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.google.api.client.util.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SQLMutesDao implements MutesDao
{

    @Override
    public boolean isMuted( UUID uuid )
    {
        boolean exists = false;

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT EXISTS(SELECT id FROM " + PunishmentType.MUTE.getTable() + " WHERE uuid = ?" +
                              " AND active = ? AND type NOT LIKE 'IP%');"
              ) )
        {
            pstmt.setString( 1, uuid.toString() );
            pstmt.setBoolean( 2, true );

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
            BUCore.logException( e );
        }

        return exists;
    }

    @Override
    public boolean isIPMuted( String ip )
    {
        boolean exists = false;

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT EXISTS(SELECT id FROM " + PunishmentType.MUTE.getTable() + " WHERE ip = ?" +
                              " AND active = ? AND type LIKE 'IP%');"
              ) )
        {
            pstmt.setString( 1, ip );
            pstmt.setBoolean( 2, true );

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
            BUCore.logException( e );
        }

        return exists;
    }

    @Override
    public boolean isMuted( PunishmentType type, UUID uuid )
    {
        if ( type.isIP() || !type.isMute() )
        {
            return false;
        }
        boolean exists = false;

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT EXISTS(SELECT id FROM " + type.getTable() + " WHERE uuid = ?" +
                              " AND active = ? AND type = ?);"
              ) )
        {
            pstmt.setString( 1, uuid.toString() );
            pstmt.setBoolean( 2, true );
            pstmt.setString( 3, type.toString() );

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
            BUCore.logException( e );
        }

        return exists;
    }

    @Override
    public boolean isIPMuted( PunishmentType type, String ip )
    {
        if ( !type.isMute() || !type.isIP() )
        {
            return false;
        }
        boolean exists = false;

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT EXISTS(SELECT id FROM " + type.getTable() + " WHERE ip = ?" +
                              " AND active = ? AND type = ?);"
              ) )
        {
            pstmt.setString( 1, ip );
            pstmt.setBoolean( 2, true );
            pstmt.setString( 3, type.toString() );

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
            BUCore.logException( e );
        }

        return exists;
    }

    @Override
    public PunishmentInfo insertMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby )
    {
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "INSERT INTO " + PunishmentType.MUTE.getTable() + " (uuid, user, ip, reason, server, " +
                              "active, executed_by, duration, type, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
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

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
        return PunishmentDao.buildPunishmentInfo( PunishmentType.MUTE, uuid, user, ip, reason, server, executedby, new Date(), -1, active, null );
    }

    @Override
    public PunishmentInfo insertIPMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby )
    {
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "INSERT INTO " + PunishmentType.MUTE.getTable() + " (uuid, user, ip, reason, server, " +
                              "active, executed_by, duration, type, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
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

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
        return PunishmentDao.buildPunishmentInfo( PunishmentType.IPMUTE, uuid, user, ip, reason, server, executedby, new Date(), -1, active, null );
    }

    @Override
    public PunishmentInfo insertTempMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration )
    {
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "INSERT INTO " + PunishmentType.MUTE.getTable() + " (uuid, user, ip, reason, server, " +
                              "active, executed_by, duration, type, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
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

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
        return PunishmentDao.buildPunishmentInfo( PunishmentType.TEMPMUTE, uuid, user, ip, reason, server, executedby, new Date(), duration, active, null );
    }

    @Override
    public PunishmentInfo insertTempIPMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration )
    {
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "INSERT INTO " + PunishmentType.MUTE.getTable() + " (uuid, user, ip, reason, server, " +
                              "active, executed_by, duration, type, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
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

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
        return PunishmentDao.buildPunishmentInfo( PunishmentType.IPTEMPMUTE, uuid, user, ip, reason, server, executedby, new Date(), duration, active, null );
    }

    @Override
    public PunishmentInfo getCurrentMute( UUID uuid )
    {
        PunishmentInfo info = new PunishmentInfo();

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE uuid = ? AND active = ? AND type NOT LIKE 'IP%' LIMIT 1;"
              ) )
        {
            pstmt.setString( 1, uuid.toString() );
            pstmt.setBoolean( 2, true );

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

                    info = PunishmentDao.buildPunishmentInfo( type, uuid, user, ip, reason, server, executedby, date, time, active, removedby );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }

        return info;
    }

    @Override
    public PunishmentInfo getCurrentIPMute( String ip )
    {
        PunishmentInfo info = new PunishmentInfo();

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE ip = ? AND active = ? AND type LIKE 'IP%' LIMIT 1;"
              ) )
        {
            pstmt.setString( 1, ip );
            pstmt.setBoolean( 2, true );

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

                    info = PunishmentDao.buildPunishmentInfo( type, uuid, user, ip, reason, server, executedby, date, time, active, removedby );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }

        return info;
    }

    @Override
    public void removeCurrentMute( UUID uuid, String removedBy )
    {
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "UPDATE " + PunishmentType.MUTE.getTable() + " SET active = ?, removed = ?, removed_by = ?" +
                              " WHERE uuid = ? AND active = ? AND type NOT LIKE 'IP%';"
              ) )
        {
            pstmt.setBoolean( 1, false );
            pstmt.setBoolean( 2, true );
            pstmt.setString( 3, removedBy );
            pstmt.setString( 4, uuid.toString() );
            pstmt.setBoolean( 5, true );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
    }

    @Override
    public void removeCurrentIPMute( String ip, String removedBy )
    {
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "UPDATE " + PunishmentType.MUTE.getTable() + " SET active = ?, removed = ?, removed_by = ?" +
                              " WHERE ip = ? AND active = ? AND type LIKE 'IP%';"
              ) )
        {
            pstmt.setBoolean( 1, false );
            pstmt.setBoolean( 2, true );
            pstmt.setString( 3, removedBy );
            pstmt.setString( 4, ip );
            pstmt.setBoolean( 5, true );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
    }

    @Override
    public List<PunishmentInfo> getMutes( UUID uuid )
    {
        final List<PunishmentInfo> punishments = Lists.newArrayList();

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE uuid = ? AND type NOT LIKE 'IP%';"
              ) )
        {
            pstmt.setString( 1, uuid.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
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

                    punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }

        return punishments;
    }

    @Override
    public List<PunishmentInfo> getIPMutes( String ip )
    {
        final List<PunishmentInfo> punishments = Lists.newArrayList();

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT * FROM " + PunishmentType.MUTE.getTable() + " WHERE ip = ? AND type LIKE 'IP%';"
              ) )
        {
            pstmt.setString( 1, ip );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
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

                    punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }

        return punishments;
    }

    @Override
    public PunishmentInfo getById( String id )
    {
        PunishmentInfo info = null;

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
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

                    info = PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }

        return info;
    }
}
