package dev.endoy.bungeeutilisalsx.common.storage.data.mongo.dao.punishment;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentTrackInfo;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments.TracksDao;
import dev.endoy.bungeeutilisalsx.common.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments.TracksDao.useServerPunishments;

public class MongoTracksDao implements TracksDao
{

    @Override
    public CompletableFuture<List<PunishmentTrackInfo>> getTrackInfos( final UUID uuid, final String trackId, final String server )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<PunishmentTrackInfo> trackInfos = new ArrayList<>();
            final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "uuid", uuid.toString() ),
                Filters.eq( "track_id", trackId ),
                Filters.eq( "active", true )
            );
            if ( useServerPunishments() )
            {
                filters.add( Filters.eq( "server", server ) );
            }

            final MongoCollection<Document> collection = db().getCollection( "bu_punishmenttracks" );
            final MongoIterable<Document> documentIterator = collection.find( Filters.and( filters ) );

            for ( Document document : documentIterator )
            {
                trackInfos.add( new PunishmentTrackInfo(
                    UUID.fromString( document.getString( "uuid" ) ),
                    document.getString( "track_id" ),
                    document.getString( "server" ),
                    document.getString( "executed_by" ),
                    document.getDate( "date" ),
                    document.getBoolean( "active" )
                ) );
            }

            return trackInfos;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> addToTrack( final PunishmentTrackInfo trackInfo )
    {
        return CompletableFuture.runAsync( () ->
        {
            final MongoCollection<Document> collection = db().getCollection( "bu_punishmenttracks" );
            final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
            data.put( "uuid", trackInfo.getUuid().toString() );
            data.put( "track_id", trackInfo.getTrackId() );
            data.put( "server", trackInfo.getServer() );
            data.put( "executed_by", trackInfo.getExecutedBy() );
            data.put( "date", trackInfo.getDate() );
            data.put( "active", trackInfo.isActive() );

            collection.insertOne( new Document( data ) );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> resetTrack( final UUID uuid, final String trackId, final String server )
    {
        return CompletableFuture.runAsync( () ->
        {
            final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "uuid", uuid.toString() ),
                Filters.eq( "track_id", trackId ),
                Filters.eq( "active", true )
            );
            if ( useServerPunishments() )
            {
                filters.add( Filters.eq( "server", server ) );
            }
            final MongoCollection<Document> collection = db().getCollection( "bu_punishmenttracks" );

            collection.deleteMany( Filters.and( filters ) );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    private MongoDatabase db()
    {
        return ( (MongoDBStorageManager) BuX.getInstance().getAbstractStorageManager() ).getDatabase();
    }
}
