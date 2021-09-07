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

package be.dieterblancke.bungeeutilisalsx.common.converter.converters;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.StorageType;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.ProgressableCallback;
import be.dieterblancke.bungeeutilisalsx.common.converter.Converter;
import be.dieterblancke.bungeeutilisalsx.common.storage.data.sql.SQLDao;
import be.dieterblancke.bungeeutilisalsx.common.storage.file.SQLiteStorageManager;
import com.google.common.collect.Lists;

import java.sql.*;
import java.util.Collection;
import java.util.Map;
import java.util.logging.Level;

public class SQLtoSQLConverter extends Converter
{

    @Override
    protected void importData( final ProgressableCallback<ConverterStatus> converterCallback, final Map<String, String> properties )
    {
        AbstractStorageManager storageManager;
        try
        {
            storageManager = createStorageManager( properties );
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            return;
        }

        final StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append( "SELECT (SELECT COUNT(*) FROM bu_users) users, " );

        for ( PunishmentType type : PunishmentType.values() )
        {
            queryBuilder.append( "(SELECT COUNT(*) FROM " ).append( type.getTable() ).append( ") " ).append( type.toString().toLowerCase() ).append( ", " );
        }

        queryBuilder.substring( 0, queryBuilder.length() - 2 );
        queryBuilder.append( ";" );

        try ( final Connection connection = storageManager.getConnection();
              final PreparedStatement pstmt = connection.prepareStatement( queryBuilder.toString() );
              final ResultSet rs = pstmt.executeQuery() )
        {
            if ( rs.next() )
            {
                int count = rs.getInt( "users" );

                for ( PunishmentType type : PunishmentType.values() )
                {
                    count += rs.getInt( type.toString().toLowerCase() );
                }

                status = new ConverterStatus( count );
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
        }

        try ( final Connection connection = storageManager.getConnection();
              final PreparedStatement preparedStatement = connection.prepareStatement( "SELECT * FROM bu_users ORDER BY id ASC;" );
              final ResultSet rs = preparedStatement.executeQuery() )
        {

            try ( final Connection newConnection = BuX.getApi().getStorageManager().getConnection() )
            {
                newConnection.setAutoCommit( false );
                while ( rs.next() )
                {
                    createUser( newConnection, rs );
                    status.incrementConvertedEntries( 1 );
                    converterCallback.progress( status );

                    if ( status.getConvertedEntries() % 100 == 0 )
                    {
                        newConnection.commit();
                    }
                }
                newConnection.commit();
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
        }

        for ( PunishmentType type : PunishmentType.values() )
        {
            try ( final Connection connection = storageManager.getConnection();
                  final PreparedStatement preparedStatement = connection.prepareStatement( "SELECT * FROM " + type.getTable() + " ORDER BY id ASC;" );
                  final ResultSet rs = preparedStatement.executeQuery() )
            {
                try ( final Connection newConnection = BuX.getApi().getStorageManager().getConnection() )
                {
                    newConnection.setAutoCommit( false );
                    while ( rs.next() )
                    {
                        createPunishment( type, newConnection, rs );
                        status.incrementConvertedEntries( 1 );
                        converterCallback.progress( status );

                        if ( status.getConvertedEntries() % 100 == 0 )
                        {
                            newConnection.commit();
                        }
                    }
                    newConnection.commit();
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }
        }
    }

    private void createUser( Connection connection, ResultSet rs ) throws SQLException
    {
        try ( PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO bu_users(uuid, username, ip, language, firstlogin, lastlogout) VALUES (?, ?, ?, ?, ?, ?);"
        ) )
        {
            preparedStatement.setString( 1, rs.getString( "uuid" ) );
            preparedStatement.setString( 2, rs.getString( "username" ) );
            preparedStatement.setString( 3, rs.getString( "ip" ) );
            preparedStatement.setString( 4, rs.getString( "language" ) );
            preparedStatement.setString( 5, rs.getString( "firstlogin" ) );
            preparedStatement.setString( 6, rs.getString( "lastlogout" ) );

            preparedStatement.executeUpdate();
        }
    }

    private void createPunishment( PunishmentType type, Connection connection, ResultSet rs ) throws SQLException
    {
        if ( !type.isActivatable() )
        {
            try ( PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + type.getTable() + "(uuid, user, ip, reason, server, date, executed_by) VALUES (?, ?, ?, ?, ?, ?, ?);"
            ) )
            {
                preparedStatement.setString( 1, rs.getString( "uuid" ) );
                preparedStatement.setString( 2, rs.getString( "user" ) );
                preparedStatement.setString( 3, rs.getString( "ip" ) );
                preparedStatement.setString( 4, rs.getString( "reason" ) );
                preparedStatement.setString( 5, rs.getString( "server" ) );
                preparedStatement.setString( 6, rs.getString( "date" ) );
                preparedStatement.setString( 7, rs.getString( "executed_by" ) );

                preparedStatement.executeUpdate();
            }
        }
        else if ( type.isTemporary() )
        {
            try ( PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + type.getTable() + "(uuid, user, ip, time, reason, server, date, active, executed_by, removed_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);"
            ) )
            {
                preparedStatement.setString( 1, rs.getString( "uuid" ) );
                preparedStatement.setString( 2, rs.getString( "user" ) );
                preparedStatement.setString( 3, rs.getString( "ip" ) );
                preparedStatement.setLong( 4, rs.getLong( "time" ) );
                preparedStatement.setString( 5, rs.getString( "reason" ) );
                preparedStatement.setString( 6, rs.getString( "server" ) );
                preparedStatement.setString( 7, rs.getString( "date" ) );
                preparedStatement.setBoolean( 8, rs.getBoolean( "active" ) );
                preparedStatement.setString( 9, rs.getString( "executed_by" ) );
                preparedStatement.setString( 10, rs.getString( "removed_by" ) );

                preparedStatement.executeUpdate();
            }
        }
        else
        {
            try ( PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO " + type.getTable() + "(uuid, user, ip, reason, server, date, active, executed_by, removed_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);"
            ) )
            {
                preparedStatement.setString( 1, rs.getString( "uuid" ) );
                preparedStatement.setString( 2, rs.getString( "user" ) );
                preparedStatement.setString( 3, rs.getString( "ip" ) );
                preparedStatement.setString( 4, rs.getString( "reason" ) );
                preparedStatement.setString( 5, rs.getString( "server" ) );
                preparedStatement.setString( 6, rs.getString( "date" ) );
                preparedStatement.setBoolean( 7, rs.getBoolean( "active" ) );
                preparedStatement.setString( 8, rs.getString( "executed_by" ) );
                preparedStatement.setString( 9, rs.getString( "removed_by" ) );

                preparedStatement.executeUpdate();
            }
        }
    }

    private AbstractStorageManager createStorageManager( final Map<String, String> properties ) throws SQLException
    {
        if ( properties.isEmpty() )
        {
            throw new IllegalArgumentException( "Invalid properties supplied." );
        }
        final StorageType type = StorageType.valueOf( properties.get( "type" ).toUpperCase() );

        if ( type.equals( StorageType.SQLITE ) )
        { // sqlite
            return new SQLiteStorageManager();
        }
        else
        { // mysql, mariadb or postgresql
            return new AbstractStorageManager( type, new SQLDao() )
            {

                private final Collection<Connection> connections = Lists.newArrayList();

                @Override
                public Connection getConnection() throws SQLException
                {
                    final Connection connection = DriverManager.getConnection(
                            "jdbc:" + ( type.equals( StorageType.POSTGRESQL ) ? "postgresql" : "mysql" ) + "://" + properties.get( "host" ) + ":"
                                    + properties.get( "port" )
                                    + "/" + properties.get( "database" ),
                            properties.get( "username" ),
                            properties.get( "password" )
                    );
                    connections.add( connection );
                    return connection;
                }

                @Override
                public void close() throws SQLException
                {
                    for ( Connection connection : connections )
                    {
                        connection.close();
                    }
                    connections.clear();
                }
            };
        }
    }
}
