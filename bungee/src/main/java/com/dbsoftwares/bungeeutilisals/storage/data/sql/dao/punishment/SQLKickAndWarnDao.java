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
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.KickAndWarnDao;
import com.google.api.client.util.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SQLKickAndWarnDao implements KickAndWarnDao
{

    @Override
    public PunishmentInfo insertWarn( UUID uuid, String user, String ip, String reason, String server, String executedby )
    {
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "INSERT INTO " + PunishmentType.WARN.getTable() + " (uuid, user, ip, reason, server, executed_by, date)" +
                              " VALUES (?, ?, ?, ?, ?, ?, ?);"
              ) )
        {
            pstmt.setString( 1, uuid.toString() );
            pstmt.setString( 2, user );
            pstmt.setString( 3, ip );
            pstmt.setString( 4, reason );
            pstmt.setString( 5, server );
            pstmt.setString( 6, executedby );
            pstmt.setString( 7, Dao.formatDateToString( new Date() ) );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
        return PunishmentDao.buildPunishmentInfo( PunishmentType.WARN, uuid, user, ip, reason, server, executedby, new Date(), -1, true, null );
    }

    @Override
    public PunishmentInfo insertKick( UUID uuid, String user, String ip, String reason, String server, String executedby )
    {
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "INSERT INTO " + PunishmentType.KICK.getTable() + " (uuid, user, ip, reason, server, executed_by, date)" +
                              " VALUES (?, ?, ?, ?, ?, ?, ?);"
              ) )
        {
            pstmt.setString( 1, uuid.toString() );
            pstmt.setString( 2, user );
            pstmt.setString( 3, ip );
            pstmt.setString( 4, reason );
            pstmt.setString( 5, server );
            pstmt.setString( 6, executedby );
            pstmt.setString( 7, Dao.formatDateToString( new Date() ) );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
        return PunishmentDao.buildPunishmentInfo( PunishmentType.KICK, uuid, user, ip, reason, server, executedby, new Date(), -1, true, null );
    }

    @Override
    public List<PunishmentInfo> getKicks( UUID uuid )
    {
        final List<PunishmentInfo> punishments = Lists.newArrayList();

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT * FROM " + PunishmentType.KICK.getTable() + " WHERE uuid = ?;"
              ) )
        {
            pstmt.setString( 1, uuid.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    final int id = rs.getInt( "id" );
                    final String ip = rs.getString( "ip" );
                    final String user = rs.getString( "user" );
                    final String reason = rs.getString( "reason" );
                    final String server = rs.getString( "server" );
                    final String executedby = rs.getString( "executed_by" );
                    final Date date = Dao.formatStringToDate( rs.getString( "date" ) );

                    punishments.add( PunishmentDao.buildPunishmentInfo(
                            id, PunishmentType.KICK, uuid, user, ip, reason, server, executedby,
                            date, -1, true, null
                    ) );
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
    public List<PunishmentInfo> getWarns( UUID uuid )
    {
        final List<PunishmentInfo> punishments = Lists.newArrayList();

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT * FROM " + PunishmentType.WARN.getTable() + " WHERE uuid = ?;"
              ) )
        {
            pstmt.setString( 1, uuid.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    final int id = rs.getInt( "id" );
                    final String ip = rs.getString( "ip" );
                    final String user = rs.getString( "user" );
                    final String reason = rs.getString( "reason" );
                    final String server = rs.getString( "server" );
                    final String executedby = rs.getString( "executed_by" );
                    final Date date = Dao.formatStringToDate( rs.getString( "date" ) );

                    punishments.add( PunishmentDao.buildPunishmentInfo(
                            id, PunishmentType.WARN, uuid, user, ip, reason, server, executedby,
                            date, -1, true, null
                    ) );
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
    public PunishmentInfo getKickById( String id )
    {
        PunishmentInfo info = null;

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT * FROM " + PunishmentType.KICK.getTable() + " WHERE id = ? LIMIT 1;"
              ) )
        {
            pstmt.setInt( 1, Integer.parseInt( id ) );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    final UUID uuid = UUID.fromString( rs.getString( "uuid" ) );
                    final String user = rs.getString( "user" );
                    final String ip = rs.getString( "ip" );
                    final String reason = rs.getString( "reason" );
                    final String server = rs.getString( "server" );
                    final String executedby = rs.getString( "executed_by" );
                    final Date date = Dao.formatStringToDate( rs.getString( "date" ) );

                    info = PunishmentDao.buildPunishmentInfo(
                            id, PunishmentType.KICK, uuid, user, ip, reason, server, executedby,
                            date, -1, true, null
                    );
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
    public PunishmentInfo getWarnById( String id )
    {
        PunishmentInfo info = null;

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT * FROM " + PunishmentType.WARN.getTable() + " WHERE id = ? LIMIT 1;"
              ) )
        {
            pstmt.setInt( 1, Integer.parseInt( id ) );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    final UUID uuid = UUID.fromString( rs.getString( "uuid" ) );
                    final String user = rs.getString( "user" );
                    final String ip = rs.getString( "ip" );
                    final String reason = rs.getString( "reason" );
                    final String server = rs.getString( "server" );
                    final String executedby = rs.getString( "executed_by" );
                    final Date date = Dao.formatStringToDate( rs.getString( "date" ) );

                    info = PunishmentDao.buildPunishmentInfo(
                            id, PunishmentType.WARN, uuid, user, ip, reason, server, executedby,
                            date, -1, true, null
                    );
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
