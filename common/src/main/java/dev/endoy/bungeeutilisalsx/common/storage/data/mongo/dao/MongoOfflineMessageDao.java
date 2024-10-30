package dev.endoy.bungeeutilisalsx.common.storage.data.mongo.dao;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.OfflineMessageDao;
import dev.endoy.bungeeutilisalsx.common.api.utils.placeholders.MessagePlaceholders;
import dev.endoy.bungeeutilisalsx.common.storage.mongodb.MongoDBStorageManager;
import com.google.gson.Gson;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MongoOfflineMessageDao implements OfflineMessageDao
{

    private static final Gson GSON = new Gson();

    @Override
    public CompletableFuture<List<OfflineMessage>> getOfflineMessages( final String username )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<OfflineMessage> messages = new ArrayList<>();
            final MongoCollection<Document> collection = db().getCollection( "bu_offline_message" );
            final FindIterable<Document> results = collection.find( Filters.and(
                Filters.eq( "username", username ),
                Filters.eq( "active", true )
            ) );

            for ( Document document : results )
            {
                messages.add( new OfflineMessage(
                    document.getLong( "_id" ),
                    document.getString( "message" ),
                    MessagePlaceholders.fromArray( GSON.fromJson( document.getString( "parameters" ), Object[].class ) )
                ) );
            }

            return messages;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> sendOfflineMessage( final String username, final OfflineMessage message )
    {
        return CompletableFuture.runAsync( () ->
        {
            final MongoCollection<Document> collection = db().getCollection( "bu_offline_message" );

            collection.insertOne(
                new Document()
                    .append( "_id", manager().getNextSequenceValue( "offline_message_id" ) )
                    .append( "username", username )
                    .append( "message", message.getLanguagePath() )
                    .append( "parameters", GSON.toJson( message.getPlaceholders().getMessagePlaceholders().asArray() ) )
                    .append( "active", true )
            );
        } );
    }

    @Override
    public CompletableFuture<Void> deleteOfflineMessage( final Long id )
    {
        return CompletableFuture.runAsync( () ->
        {
            final MongoCollection<Document> collection = db().getCollection( "bu_offline_message" );

            collection.deleteOne( Filters.eq( "_id", id ) );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    private MongoDBStorageManager manager()
    {
        return (MongoDBStorageManager) BuX.getInstance().getAbstractStorageManager();
    }

    private MongoDatabase db()
    {
        return manager().getDatabase();
    }
}
