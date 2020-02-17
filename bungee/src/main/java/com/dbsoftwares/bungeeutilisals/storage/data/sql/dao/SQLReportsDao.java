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

package com.dbsoftwares.bungeeutilisals.storage.data.sql.dao;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.ReportsDao;
import com.dbsoftwares.bungeeutilisals.api.utils.other.Report;
import com.google.common.collect.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class SQLReportsDao implements ReportsDao
{

    @Override
    public void addReport( Report report )
    {
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "INSERT INTO {reports-table} (uuid, reported_by, handled, server, reason, accepted) VALUES (?, ?, ?, ?, ?, ?);" )
              ) )
        {
            pstmt.setString( 1, report.getUuid().toString() );
            pstmt.setString( 2, report.getReportedBy() );
            pstmt.setBoolean( 3, report.isHandled() );
            pstmt.setString( 4, report.getServer() );
            pstmt.setString( 5, report.getReason() );
            pstmt.setBoolean( 6, report.isAccepted() );

            pstmt.execute();
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
    }

    @Override
    public void removeReport( long id )
    {
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "DELETE FROM {reports-table} WHERE id = ?;" )
              ) )
        {
            pstmt.setLong( 1, id );

            pstmt.execute();
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
    }

    @Override
    public Report getReport( long id )
    {
        Report report = null;
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT r.*, u.username reported FROM {reports-table} r JOIN {users-table} u ON u.uuid = r.uuid WHERE r.id = ? LIMIT 1;" )
              ) )
        {
            pstmt.setLong( 1, id );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    report = getReport( rs );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
        return report;
    }

    @Override
    public List<Report> getReports()
    {
        final List<Report> reports = Lists.newArrayList();
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT r.*, u.username reported FROM {reports-table} r JOIN {users-table} u ON u.uuid = r.uuid;" )
              ) )
        {

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                while ( rs.next() )
                {
                    reports.add( getReport( rs ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
        return reports;
    }

    @Override
    public List<Report> getReports( UUID uuid )
    {
        final List<Report> reports = Lists.newArrayList();
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT r.*, u.username reported FROM {reports-table} r JOIN {users-table} u ON u.uuid = r.uuid WHERE r.uuid = ?;" )
              ) )
        {
            pstmt.setString( 1, uuid.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                while ( rs.next() )
                {
                    reports.add( getReport( rs ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
        return reports;
    }

    @Override
    public List<Report> getActiveReports()
    {
        return getHandledReports( false );
    }

    @Override
    public List<Report> getHandledReports()
    {
        return getHandledReports( true );
    }

    private List<Report> getHandledReports( boolean handled )
    {
        final List<Report> reports = Lists.newArrayList();
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT r.*, u.username reported FROM {reports-table} r JOIN {users-table} u ON u.uuid = r.uuid WHERE handled = ?;" )
              ) )
        {
            pstmt.setBoolean( 1, handled );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                while ( rs.next() )
                {
                    reports.add( getReport( rs ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
        return reports;
    }

    private List<Report> getAcceptedReports( boolean accepted )
    {
        final List<Report> reports = Lists.newArrayList();
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT r.*, u.username reported FROM {reports-table} r JOIN {users-table} u ON u.uuid = r.uuid WHERE accepted = ?;" )
              ) )
        {
            pstmt.setBoolean( 1, accepted );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                while ( rs.next() )
                {
                    reports.add( getReport( rs ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
        return reports;
    }

    @Override
    public List<Report> getRecentReports( int days )
    {
        final List<Report> reports = Lists.newArrayList();
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT r.*, u.username reported FROM {reports-table} r JOIN {users-table} u ON u.uuid = r.uuid WHERE date >= DATEADD(DAY, ?, GETDATE());" )
              ) )
        {
            pstmt.setInt( 1, -days );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                while ( rs.next() )
                {
                    reports.add( getReport( rs ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
        return reports;
    }

    @Override
    public boolean reportExists( long id )
    {
        boolean exists = false;
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT id FROM {reports-table} WHERE id = ? LIMIT 1;" )
              ) )
        {
            pstmt.setLong( 1, id );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                exists = rs.next();
            }
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
        return exists;
    }

    @Override
    public void handleReport( long id, boolean accepted )
    {
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "UPDATE {reports-table} SET handled = ?, accepted = ? WHERE id = ?;" )
              ) )
        {
            pstmt.setBoolean( 1, true );
            pstmt.setBoolean( 2, accepted );
            pstmt.setLong( 3, id );
            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
    }

    @Override
    public List<Report> getAcceptedReports()
    {
        return getAcceptedReports( true );
    }

    @Override
    public List<Report> getDeniedReports()
    {
        return getAcceptedReports( false );
    }

    @Override
    public List<Report> getReportsHistory( final String name )
    {
        final List<Report> reports = Lists.newArrayList();
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT r.*, u.username reported FROM {reports-table} r JOIN {users-table} u ON u.uuid = r.uuid WHERE reported_by = ?;" )
              ) )
        {
            pstmt.setString( 1, name );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                while ( rs.next() )
                {
                    reports.add( getReport( rs ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.logException( e );
        }
        return reports;
    }

    private String format( String line )
    {
        return PlaceHolderAPI.formatMessage( line );
    }

    private Report getReport( final ResultSet rs ) throws SQLException
    {
        return new Report(
                rs.getLong( "id" ),
                UUID.fromString( rs.getString( "uuid" ) ),
                rs.getString( "reported" ),
                rs.getString( "reported_by" ),
                Dao.formatStringToDate( rs.getString( "date" ) ),
                rs.getString( "server" ),
                rs.getString( "reason" ),
                rs.getBoolean( "handled" ),
                rs.getBoolean( "accepted" )
        );
    }
}
