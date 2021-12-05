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

package be.dieterblancke.bungeeutilisalsx.common.storage.data.sql.dao;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.ReportsDao;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.Report;
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

public class SqlReportsDao implements ReportsDao
{

    @Override
    public CompletableFuture<Void> addReport( Report report )
    {
        return CompletableFuture.runAsync( () ->
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "INSERT INTO bu_reports (uuid, reported_by, handled, server, reason, accepted, date) VALUES (?, ?, ?, ?, ?, ?, ?);"
                  ) )
            {
                pstmt.setString( 1, report.getUuid().toString() );
                pstmt.setString( 2, report.getReportedBy() );
                pstmt.setBoolean( 3, report.isHandled() );
                pstmt.setString( 4, report.getServer() );
                pstmt.setString( 5, report.getReason() );
                pstmt.setBoolean( 6, report.isAccepted() );
                pstmt.setString( 7, Dao.formatDateToString( new Date() ) );

                pstmt.execute();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> removeReport( long id )
    {
        return CompletableFuture.runAsync( () ->
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "DELETE FROM bu_reports WHERE id = ?;"
                  ) )
            {
                pstmt.setLong( 1, id );

                pstmt.execute();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Report> getReport( long id )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            Report report = null;
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT r.*, u.username reported FROM bu_reports r JOIN bu_users u ON u.uuid = r.uuid WHERE r.id = ? LIMIT 1;"
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
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
            return report;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<Report>> getReports()
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<Report> reports = Lists.newArrayList();
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT r.*, u.username reported FROM bu_reports r JOIN bu_users u ON u.uuid = r.uuid;"
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
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
            return reports;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<Report>> getReports( UUID uuid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<Report> reports = Lists.newArrayList();
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT r.*, u.username reported FROM bu_reports r JOIN bu_users u ON u.uuid = r.uuid WHERE r.uuid = ?;"
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
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
            return reports;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<Report>> getActiveReports()
    {
        return getHandledReports( false );
    }

    @Override
    public CompletableFuture<List<Report>> getHandledReports()
    {
        return getHandledReports( true );
    }

    private CompletableFuture<List<Report>> getHandledReports( boolean handled )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<Report> reports = Lists.newArrayList();
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT r.*, u.username reported FROM bu_reports r JOIN bu_users u ON u.uuid = r.uuid WHERE handled = ?;"
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
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
            return reports;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    private CompletableFuture<List<Report>> getAcceptedReports( boolean accepted )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<Report> reports = Lists.newArrayList();
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT r.*, u.username reported FROM bu_reports r JOIN bu_users u ON u.uuid = r.uuid WHERE accepted = ?;"
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
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
            return reports;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<Report>> getRecentReports( int days )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<Report> reports = Lists.newArrayList();
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT r.*, u.username reported FROM bu_reports r JOIN bu_users u ON u.uuid = r.uuid WHERE date >= DATEADD(DAY, ?, GETDATE());"
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
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
            return reports;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> handleReport( long id, boolean accepted )
    {
        return CompletableFuture.runAsync( () ->
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "UPDATE bu_reports SET handled = ?, accepted = ? WHERE id = ?;"
                  ) )
            {
                pstmt.setBoolean( 1, true );
                pstmt.setBoolean( 2, accepted );
                pstmt.setLong( 3, id );
                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<Report>> getAcceptedReports()
    {
        return getAcceptedReports( true );
    }

    @Override
    public CompletableFuture<List<Report>> getDeniedReports()
    {
        return getAcceptedReports( false );
    }

    @Override
    public CompletableFuture<List<Report>> getReportsHistory( final String name )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<Report> reports = Lists.newArrayList();
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT r.*, u.username reported FROM bu_reports r JOIN bu_users u ON u.uuid = r.uuid WHERE reported_by = ?;"
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
                BuX.getLogger().log( Level.SEVERE, "An error occured", e );
            }
            return reports;
        }, BuX.getInstance().getScheduler().getExecutorService() );
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
