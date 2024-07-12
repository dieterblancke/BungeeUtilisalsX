package dev.endoy.bungeeutilisalsx.common.storage.mongodb;

import dev.endoy.bungeeutilisalsx.common.api.storage.AbstractStorageManager;
import dev.endoy.bungeeutilisalsx.common.api.storage.StorageType;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import dev.endoy.bungeeutilisalsx.common.storage.data.mongo.MongoDao;
import dev.endoy.configuration.api.IConfiguration;
import com.mongodb.*;
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
        final String database = configuration.getString( "storage.database" );

        if ( configuration.exists( "storage.uri" ) )
        {
            this.client = new MongoClient( new MongoClientURI( configuration.getString( "storage.uri" ) ) );
        }
        else
        {
            final String user = configuration.getString( "storage.username" );
            final String password = configuration.getString( "storage.password" );

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
        }

        this.database = client.getDatabase( database );

        initMongo();
    }

    private void initMongo()
    {
        final MongoCollection<Document> coll = database.getCollection( "bu_counters" );

        if ( coll.find( Filters.eq( "_id", "reportid" ) ).first() == null )
        {
            coll.insertOne( new Document().append( "_id", "reportid" ).append( "sequence_value", 0 ) );
        }
        if ( coll.find( Filters.eq( "_id", "offline_message_id" ) ).first() == null )
        {
            coll.insertOne( new Document().append( "_id", "offline_message_id" ).append( "sequence_value", 0 ) );
        }
    }

    public long getNextSequenceValue( final String sequenceName )
    {
        final Document sequenceDocument = database.getCollection( "bu_counters" ).findOneAndUpdate(
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
