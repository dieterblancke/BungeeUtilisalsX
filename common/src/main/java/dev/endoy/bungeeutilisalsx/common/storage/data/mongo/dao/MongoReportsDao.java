package dev.endoy.bungeeutilisalsx.common.storage.data.mongo.dao;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.ReportsDao;
import dev.endoy.bungeeutilisalsx.common.api.utils.other.Report;
import dev.endoy.bungeeutilisalsx.common.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MongoReportsDao implements ReportsDao
{

    @Override
    public CompletableFuture<Void> addReport( Report report )
    {
        return CompletableFuture.runAsync( () ->
        {
            final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();

            data.put( "_id", manager().getNextSequenceValue( "reportid" ) );
            data.put( "uuid", report.getUuid().toString() );
            data.put( "reported_by", report.getReportedBy() );
            data.put( "date", new Date() );
            data.put( "handled", report.isHandled() );
            data.put( "server", report.getServer() );
            data.put( "reason", report.getReason() );
            data.put( "accepted", report.isAccepted() );

            db().getCollection( "bu_reports" ).insertOne( new Document( data ) );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> removeReport( long id )
    {
        return CompletableFuture.runAsync( () ->
        {
            db().getCollection( "bu_reports" ).deleteOne( Filters.eq( "_id", id ) );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Report> getReport( long id )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final Document document = db().getCollection( "bu_reports" ).find( Filters.eq( "_id", id ) ).limit( 1 ).first();

            return document == null || document.isEmpty() ? null : getReport( document );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<Report>> getReports()
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<Report> reports = Lists.newArrayList();

            db().getCollection( "bu_reports" ).find()
                    .forEach( (Consumer<? super Document>) doc -> reports.add( getReport( doc ) ) );

            return reports;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<Report>> getReports( UUID uuid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<Report> reports = Lists.newArrayList();

            db().getCollection( "bu_reports" ).find( Filters.eq( "uuid", uuid.toString() ) )
                    .forEach( (Consumer<? super Document>) doc -> reports.add( getReport( doc ) ) );

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

    private CompletableFuture<List<Report>> getHandledReports( final boolean handled )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<Report> reports = Lists.newArrayList();

            db().getCollection( "bu_reports" ).find( Filters.eq( "handled", handled ) )
                    .forEach( (Consumer<? super Document>) doc -> reports.add( getReport( doc ) ) );

            return reports;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<Report>> getRecentReports( int days )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<Report> reports = Lists.newArrayList();
            final Calendar calendar = Calendar.getInstance();
            calendar.add( Calendar.DATE, -7 );

            db().getCollection( "bu_reports" ).find( Filters.gte( "date", calendar.getTime() ) )
                    .forEach( (Consumer<? super Document>) doc -> reports.add( getReport( doc ) ) );

            return reports;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> handleReport( long id, boolean accepted )
    {
        return CompletableFuture.runAsync( () ->
        {
            final MongoCollection<Document> collection = db().getCollection( "bu_reports" );
            final Document document = collection.find( Filters.eq( "_id", id ) ).limit( 1 ).first();

            document.put( "handled", true );
            document.put( "accepted", accepted );

            save( collection, document );
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

            db().getCollection( "bu_reports" )
                    .find( Filters.eq( "reported_by", name ) )
                    .forEach( (Consumer<? super Document>) doc ->
                            reports.add( getReport( doc ) )
                    );

            return reports;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    private CompletableFuture<List<Report>> getAcceptedReports( final boolean accepted )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<Report> reports = Lists.newArrayList();

            db().getCollection( "bu_reports" ).find( Filters.eq( "accepted", accepted ) )
                    .forEach( (Consumer<? super Document>) doc -> reports.add( getReport( doc ) ) );

            return reports;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    private void save( final MongoCollection<Document> collection, final Document document )
    {
        final Object id = document.get( "_id" );

        if ( id == null )
        {
            collection.insertOne( document );
        }
        else
        {
            collection.replaceOne( Filters.eq( "_id", id ), document, new ReplaceOptions().upsert( true ) );
        }
    }

    private Report getReport( final Document document )
    {
        final MongoCollection<Document> userColl = db().getCollection( "bu_users" );
        final Document user = userColl.find( Filters.eq( "uuid", document.getString( "uuid" ) ) ).first();

        return new Report(
                document.getInteger( "_id" ),
                UUID.fromString( document.getString( "uuid" ) ),
                user.getString( "username" ),
                document.getString( "reported_by" ),
                document.getDate( "date" ),
                document.getString( "server" ),
                document.getString( "reason" ),
                document.getBoolean( "handled" ),
                document.getBoolean( "accepted" )
        );
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
