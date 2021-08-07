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

package be.dieterblancke.bungeeutilisalsx.common.storage.data.mongo.dao;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.ReportsDao;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.Report;
import be.dieterblancke.bungeeutilisalsx.common.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;

import java.util.*;
import java.util.function.Consumer;

public class MongoReportsDao implements ReportsDao
{

    @Override
    public void addReport( Report report )
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
    }

    @Override
    public void removeReport( long id )
    {
        db().getCollection( "bu_reports" ).deleteOne( Filters.eq( "_id", id ) );
    }

    @Override
    public Report getReport( long id )
    {
        final Report report;
        final Document document = db().getCollection( "bu_reports" ).find( Filters.eq( "_id", id ) ).limit( 1 ).first();

        if ( document == null || document.isEmpty() )
        {
            report = null;
        }
        else
        {
            report = getReport( document );
        }

        return report;
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

    @Override
    public List<Report> getReports()
    {
        final List<Report> reports = Lists.newArrayList();

        db().getCollection( "bu_reports" ).find()
                .forEach( (Consumer<? super Document>) doc -> reports.add( getReport( doc ) ) );

        return reports;
    }

    @Override
    public List<Report> getReports( UUID uuid )
    {
        final List<Report> reports = Lists.newArrayList();

        db().getCollection( "bu_reports" ).find( Filters.eq( "uuid", uuid.toString() ) )
                .forEach( (Consumer<? super Document>) doc -> reports.add( getReport( doc ) ) );

        return reports;
    }

    @Override
    public List<Report> getActiveReports()
    {
        return getHandledReports( false );
    }

    @Override
    public List<Report> getHandledReports()
    {
        return getHandledReports( true );
    }

    private List<Report> getHandledReports( final boolean handled )
    {
        final List<Report> reports = Lists.newArrayList();

        db().getCollection( "bu_reports" ).find( Filters.eq( "handled", handled ) )
                .forEach( (Consumer<? super Document>) doc -> reports.add( getReport( doc ) ) );

        return reports;
    }

    @Override
    public List<Report> getRecentReports( int days )
    {
        final List<Report> reports = Lists.newArrayList();
        final Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.DATE, -7 );

        db().getCollection( "bu_reports" ).find( Filters.gte( "date", calendar.getTime() ) )
                .forEach( (Consumer<? super Document>) doc -> reports.add( getReport( doc ) ) );

        return reports;
    }

    @Override
    public boolean reportExists( long id )
    {
        return db().getCollection( "bu_reports" ).find( Filters.eq( "_id", id ) ).limit( 1 ).iterator().hasNext();
    }

    @Override
    public void handleReport( long id, boolean accepted )
    {
        final MongoCollection<Document> collection = db().getCollection( "bu_reports" );
        final Document document = collection.find( Filters.eq( "_id", id ) ).limit( 1 ).first();

        document.put( "handled", true );
        document.put( "accepted", accepted );

        save( collection, document );
    }

    @Override
    public List<Report> getAcceptedReports()
    {
        return getAcceptedReports( true );
    }

    @Override
    public List<Report> getDeniedReports()
    {
        return getAcceptedReports( false );
    }

    @Override
    public List<Report> getReportsHistory( final String name )
    {
        final List<Report> reports = Lists.newArrayList();

        db().getCollection( "bu_reports" )
                .find( Filters.eq( "reported_by", name ) )
                .forEach( (Consumer<? super Document>) doc ->
                        reports.add( getReport( doc ) )
                );

        return reports;
    }

    private List<Report> getAcceptedReports( final boolean accepted )
    {
        final List<Report> reports = Lists.newArrayList();

        db().getCollection( "bu_reports" ).find( Filters.eq( "accepted", accepted ) )
                .forEach( (Consumer<? super Document>) doc -> reports.add( getReport( doc ) ) );

        return reports;
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

    private MongoDBStorageManager manager()
    {
        return (MongoDBStorageManager) BuX.getInstance().getAbstractStorageManager();
    }

    private MongoDatabase db()
    {
        return manager().getDatabase();
    }
}
