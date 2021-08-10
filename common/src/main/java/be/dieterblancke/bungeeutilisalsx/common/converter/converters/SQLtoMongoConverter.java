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
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.ProgressableCallback;
import be.dieterblancke.bungeeutilisalsx.common.converter.Converter;
import be.dieterblancke.bungeeutilisalsx.common.storage.data.sql.SQLDao;
import be.dieterblancke.bungeeutilisalsx.common.storage.file.SQLiteStorageManager;
import com.google.common.collect.Lists;

import java.sql.*;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class SQLtoMongoConverter extends Converter
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
        queryBuilder.append( "SELECT (SELECT COUNT(*) FROM bu_users) users," );

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

            while ( rs.next() )
            {
                createUser( rs );
                status.incrementConvertedEntries( 1 );
                converterCallback.progress( status );
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

                while ( rs.next() )
                {
                    createPunishment( type, rs );
                    status.incrementConvertedEntries( 1 );
                    converterCallback.progress( status );
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
            }
        }
    }

    private void createUser( ResultSet rs ) throws SQLException
    {
        BuX.getApi().getStorageManager().getDao().getUserDao().createUser(
                UUID.fromString( rs.getString( "uuid" ) ),
                rs.getString( "username" ),
                rs.getString( "ip" ),
                BuX.getApi().getLanguageManager().getLangOrDefault( rs.getString( "language" ) ),
                Dao.formatStringToDate( rs.getString( "firstlogin" ) ),
                Dao.formatStringToDate( rs.getString( "lastlogout" ) ),
                rs.getString( "joined_host" )
        );
    }

    private void createPunishment( PunishmentType type, ResultSet rs ) throws SQLException
    {
        if ( !type.isActivatable() )
        {
            getImportUtils().insertPunishment(
                    type,
                    UUID.fromString( rs.getString( "uuid" ) ),
                    rs.getString( "user" ),
                    rs.getString( "ip" ),
                    rs.getString( "reason" ),
                    -1L,
                    rs.getString( "server" ),
                    false,
                    rs.getString( "executed_by" )
            );
        }
        else if ( type.isTemporary() )
        {
            getImportUtils().insertPunishment(
                    type,
                    UUID.fromString( rs.getString( "uuid" ) ),
                    rs.getString( "user" ),
                    rs.getString( "ip" ),
                    rs.getString( "reason" ),
                    rs.getLong( "time" ),
                    rs.getString( "server" ),
                    rs.getBoolean( "active" ),
                    rs.getString( "executed_by" )
            );
        }
        else
        {
            getImportUtils().insertPunishment(
                    type,
                    UUID.fromString( rs.getString( "uuid" ) ),
                    rs.getString( "user" ),
                    rs.getString( "ip" ),
                    rs.getString( "reason" ),
                    -1L,
                    rs.getString( "server" ),
                    rs.getBoolean( "active" ),
                    rs.getString( "executed_by" )
            );
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
        {
            // sqlite
            return new SQLiteStorageManager();
        }
        else
        {
            // mysql, mariadb or postgresql
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
