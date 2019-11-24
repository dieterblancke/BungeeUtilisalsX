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
import com.dbsoftwares.bungeeutilisals.converter.Converter;
import com.dbsoftwares.bungeeutilisals.importer.ImporterCallback;
import com.dbsoftwares.bungeeutilisals.storage.data.mongo.MongoDao;
import com.google.common.collect.Maps;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.md_5.bungee.api.plugin.Plugin;
import org.bson.Document;

import java.sql.Connection;
import java.util.Map;
import java.util.UUID;

public class MongoToMongoConverter extends Converter
{

    @Override
    protected void importData( final ImporterCallback<ConverterStatus> importerCallback, final Map<String, String> properties )
    {
        final MongoDBStorageManager storageManager = new MongoDBStorageManager( BungeeUtilisals.getInstance(), AbstractStorageManager.StorageType.MONGODB, properties );
        final Map<String, MongoCollection<Document>> collections = Maps.newHashMap();

        collections.put( "users", storageManager.getDatabase().getCollection( PlaceHolderAPI.formatMessage( "{users-table}" ) ) );
        for ( PunishmentType type : PunishmentType.values() )
        {
            collections.put( type.toString(), storageManager.getDatabase().getCollection( PlaceHolderAPI.formatMessage( type.getTablePlaceHolder() ) ) );
        }

        final long count = collections.values().stream().mapToLong( MongoCollection::countDocuments ).sum();
        status = new ConverterStatus( count );

        for ( Document document : collections.get( "users" ).find() )
        {
            createUser( document );
            status.incrementConvertedEntries( 1 );
            importerCallback.onStatusUpdate( status );
        }
        for ( PunishmentType type : PunishmentType.values() )
        {
            for ( Document document : collections.get( type.toString() ).find() )
            {
                createPunishment( type, document );
                status.incrementConvertedEntries( 1 );
                importerCallback.onStatusUpdate( status );
            }
        }
    }

    private void createUser( Document document )
    {
        BUCore.getApi().getStorageManager().getDao().getUserDao().createUser(
                UUID.fromString( document.getString( "uuid" ) ),
                document.getString( "username" ),
                document.getString( "ip" ),
                BUCore.getApi().getLanguageManager().getLangOrDefault( document.getString( "language" ) ),
                document.getDate( "firstlogin" ),
                document.getDate( "lastlogout" )
        );
    }

    private void createPunishment( PunishmentType type, Document document )
    {
        final UUID uuid = UUID.fromString( document.getString( "uuid" ) );
        final String user = document.getString( "user" );
        final String ip = document.getString( "ip" );
        final String reason = document.getString( "reason" );
        final String server = document.getString( "server" );
        final String executedBy = document.getString( "executedBy" );
        final long duration = document.containsKey( "duration" ) ? -1L : document.getLong( "duration" );
        final boolean active = document.containsKey( "active" ) ? false : document.getBoolean( "active" );

        getImportUtils().insertPunishment( type, uuid, user, ip, reason, duration, server, active, executedBy );
    }

    private MongoDatabase getDatabase()
    {
        return ((com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager) BungeeUtilisals.getInstance().getDatabaseManagement()).getDatabase();
    }

    public class MongoDBStorageManager extends AbstractStorageManager
    {
        private MongoClient client;
        private MongoDatabase database;

        public MongoDBStorageManager( Plugin plugin, StorageType type, Map<String, String> properties )
        {
            super( plugin, type, new MongoDao() );

            String user = properties.get( "username" );
            String password = properties.get( "password" );
            String database = properties.get( "database" );

            MongoCredential credential = null;
            if ( user != null && !user.isEmpty() )
            {
                credential = MongoCredential.createCredential( user, database,
                        (password == null || password.isEmpty() ? null : password.toCharArray()) );
            }
            MongoClientOptions options = MongoClientOptions.builder().applicationName( "BungeeUtilisals" ).build();

            if ( credential == null )
            {
                client = new MongoClient( new ServerAddress( properties.get( "host" ), Integer.parseInt( properties.get( "port" ) ) ), options );
            }
            else
            {
                client = new MongoClient( new ServerAddress( properties.get( "host" ), Integer.parseInt( properties.get( "port" ) ) ), credential, options );
            }

            this.database = client.getDatabase( database );
        }

        @Override
        public Connection getConnection()
        {
            throw new UnsupportedOperationException( "MongoDB does not support java.sql.Connection!" );
        }

        @Override
        public void close()
        {
            client.close();
        }

        public MongoClient getClient()
        {
            return client;
        }

        public MongoDatabase getDatabase()
        {
            return database;
        }
    }
}
