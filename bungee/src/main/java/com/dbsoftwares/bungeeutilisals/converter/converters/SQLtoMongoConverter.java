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

package com.dbsoftwares.bungeeutilisals.converter.converters;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager.StorageType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.converter.Converter;
import com.dbsoftwares.bungeeutilisals.importer.ImporterCallback;
import com.dbsoftwares.bungeeutilisals.storage.data.sql.SQLDao;
import com.dbsoftwares.bungeeutilisals.storage.file.SQLiteStorageManager;
import com.google.common.collect.Lists;

import java.sql.*;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class SQLtoMongoConverter extends Converter
{

    @Override
    protected void importData( final ImporterCallback<ConverterStatus> importerCallback, final Map<String, String> properties )
    {
        AbstractStorageManager storageManager;
        try
        {
            storageManager = createStorageManager( properties );
        } catch ( SQLException e )
        {
            BUCore.logException( e );
            return;
        }

        final StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append( "SELECT (SELECT COUNT(*) FROM {users-table}) users," );

        for ( PunishmentType type : PunishmentType.values() )
        {
            queryBuilder.append( "(SELECT COUNT(*) FROM " ).append( type.getTablePlaceHolder() ).append( ") " ).append( type.toString().toLowerCase() ).append( ", " );
        }

        queryBuilder.substring( 0, queryBuilder.length() - 2 );
        queryBuilder.append( ";" );

        String countQuery = PlaceHolderAPI.formatMessage( queryBuilder.toString() );

        try ( final Connection connection = storageManager.getConnection();
              final PreparedStatement pstmt = connection.prepareStatement( countQuery );
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
        } catch ( SQLException e )
        {
            BUCore.logException( e );
        }

        try ( final Connection connection = storageManager.getConnection();
              final PreparedStatement preparedStatement = connection.prepareStatement( PlaceHolderAPI.formatMessage( "SELECT * FROM {users-table} ORDER BY id ASC;" ) );
              final ResultSet rs = preparedStatement.executeQuery() )
        {

            while ( rs.next() )
            {
                createUser( rs );
                status.incrementConvertedEntries( 1 );
                importerCallback.onStatusUpdate( status );
            }
        } catch ( SQLException e )
        {
            BUCore.logException( e );
        }

        for ( PunishmentType type : PunishmentType.values() )
        {
            try ( final Connection connection = storageManager.getConnection();
                  final PreparedStatement preparedStatement = connection.prepareStatement( PlaceHolderAPI.formatMessage( "SELECT * FROM " + type.getTablePlaceHolder() + " ORDER BY id ASC;" ) );
                  final ResultSet rs = preparedStatement.executeQuery() )
            {

                while ( rs.next() )
                {
                    createPunishment( type, rs );
                    status.incrementConvertedEntries( 1 );
                    importerCallback.onStatusUpdate( status );
                }
            } catch ( SQLException e )
            {
                BUCore.logException( e );
            }
        }
    }

    private void createUser( ResultSet rs ) throws SQLException
    {
        BUCore.getApi().getStorageManager().getDao().getUserDao().createUser(
                UUID.fromString( rs.getString( "uuid" ) ),
                rs.getString( "username" ),
                rs.getString( "ip" ),
                BUCore.getApi().getLanguageManager().getLangOrDefault( rs.getString( "language" ) ),
                Dao.formatStringToDate( rs.getString( "firstlogin" ) ),
                Dao.formatStringToDate( rs.getString( "lastlogout" ) )
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
        } else if ( type.isTemporary() )
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
        } else
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
            return new SQLiteStorageManager( BungeeUtilisals.getInstance() );
        } else
        {
            // mysql, mariadb or postgresql
            return new AbstractStorageManager( BungeeUtilisals.getInstance(), type, new SQLDao() )
            {

                private Collection<Connection> connections = Lists.newArrayList();

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
