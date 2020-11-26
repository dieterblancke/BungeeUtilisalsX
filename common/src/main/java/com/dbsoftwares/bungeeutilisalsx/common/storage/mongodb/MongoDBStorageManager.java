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

package com.dbsoftwares.bungeeutilisalsx.common.storage.mongodb;

import com.dbsoftwares.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisalsx.common.api.storage.StorageType;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.common.storage.data.mongo.MongoDao;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.sql.Connection;

@Getter
@Setter
public class MongoDBStorageManager extends AbstractStorageManager
{

    private MongoClient client;
    private MongoDatabase database;

    public MongoDBStorageManager( final MongoClient client, final MongoDatabase database )
    {
        super( StorageType.MONGODB, new MongoDao() );
        this.client = client;
        this.database = database;
    }

    public MongoDBStorageManager()
    {
        this( StorageType.MONGODB, ConfigFiles.CONFIG.getConfig() );
    }

    public MongoDBStorageManager( StorageType type, IConfiguration configuration )
    {
        super( type, new MongoDao() );

        String user = configuration.getString( "storage.username" );
        String password = configuration.getString( "storage.password" );
        String database = configuration.getString( "storage.database" );

        MongoCredential credential = null;
        if ( user != null && !user.isEmpty() )
        {
            credential = MongoCredential.createCredential( user, database,
                    ( password == null || password.isEmpty() ? null : password.toCharArray() ) );
        }
        MongoClientOptions options = MongoClientOptions.builder()
                .applicationName( "BungeeUtilisalsX" )
                .connectionsPerHost( configuration.getInteger( "storage.pool.max-pool-size" ) )
                .connectTimeout( configuration.getInteger( "storage.pool.connection-timeout" ) * 1000 )
                .maxConnectionLifeTime( configuration.getInteger( "storage.pool.max-lifetime" ) * 1000 )
                .build();

        if ( credential == null )
        {
            client = new MongoClient( new ServerAddress( configuration.getString( "storage.hostname" ),
                    configuration.getInteger( "storage.port" ) ), options );
        }
        else
        {
            client = new MongoClient( new ServerAddress( configuration.getString( "storage.hostname" ),
                    configuration.getInteger( "storage.port" ) ), credential, options );
        }

        this.database = client.getDatabase( database );

        initMongo();
    }

    private void initMongo()
    {
        final MongoCollection<Document> coll = database.getCollection( "bu-counters" );

        if ( coll.find( Filters.eq( "_id", "reportid" ) ).first() == null )
        {
            coll.insertOne( new Document().append( "_id", "reportid" ).append( "sequence_value", 0 ) );
        }
    }

    public long getNextSequenceValue( final String sequenceName )
    {
        final Document sequenceDocument = database.getCollection( "counters" ).findOneAndUpdate(
                Filters.eq( "_id", sequenceName ),
                Updates.inc( "sequence_value", 1 )
        );

        return sequenceDocument.getLong( "sequence_value" );
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
