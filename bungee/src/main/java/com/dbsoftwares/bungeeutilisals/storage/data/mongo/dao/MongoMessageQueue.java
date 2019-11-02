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

package com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.MessageQueue;
import com.dbsoftwares.bungeeutilisals.api.utils.other.QueuedMessage;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import org.bson.Document;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;
import java.util.function.Consumer;

public class MongoMessageQueue extends LinkedList<QueuedMessage> implements MessageQueue<QueuedMessage>
{

    private static final Gson GSON = new Gson();

    private final UUID uuid;
    private final String name;
    private final String ip;

    public MongoMessageQueue()
    {
        this.uuid = null;
        this.name = null;
        this.ip = null;
    }

    public MongoMessageQueue( final UUID uuid, final String name, final String ip )
    {
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;

        fetchMessages();
    }

    @Override
    public boolean offer( final QueuedMessage message )
    {
        throw new UnsupportedOperationException( "Not supported." );
    }

    @Override
    public boolean add( final QueuedMessage message )
    {
        try
        {
            addMessage( message );
        } catch ( Exception e )
        {
            e.printStackTrace();
            return false;
        }

        return super.add( message );
    }

    @Override
    public QueuedMessage poll()
    {
        final QueuedMessage message = super.poll();

        if ( message != null )
        {
            handle( message );
        }

        return message;
    }

    @Override
    public QueuedMessage remove()
    {
        final QueuedMessage message = super.remove();

        handle( message );

        return message;
    }

    private void addMessage( final QueuedMessage message )
    {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();

        data.put( "_id", manager().getNextSequenceValue( "messagequeueid" ) );
        data.put( "user", message.getUser() );
        data.put( "message", GSON.toJson( message.getMessage() ) );
        data.put( "type", message.getType() );
        data.put( "active", true );

        db().getCollection( format( "{messagequeue-table}" ) ).insertOne( new Document( data ) );
    }

    private void handle( final QueuedMessage message )
    {
        final MongoCollection<Document> collection = db().getCollection( format( "{messagequeue-table}" ) );
        final Document document = collection.find( Filters.eq( "_id", message.getId() ) ).limit( 1 ).first();

        document.put( "active", false );
        save( collection, document );
    }

    private void fetchMessages()
    {
        db().getCollection( format( "{messagequeue-table}" ) )
                .find( Filters.and(
                        Filters.eq( "active", true ),
                        Filters.or(
                                Filters.and(
                                        Filters.eq( "user", uuid.toString() ),
                                        Filters.eq( "type", "UUID" )
                                ),
                                Filters.and(
                                        Filters.eq( "user", name ),
                                        Filters.eq( "type", "NAME" )
                                ),
                                Filters.and(
                                        Filters.eq( "user", ip ),
                                        Filters.eq( "type", "IP" )
                                )
                        )
                ) )
                .forEach( (Consumer<? super Document>) doc -> add( new QueuedMessage(
                        doc.getLong( "id" ),
                        doc.getString( "user" ),
                        GSON.fromJson( doc.getString( "message" ), QueuedMessage.Message.class ),
                        doc.getString( "type" )
                ) ) );
    }

    public void refetch()
    {
        clear();
        fetchMessages();
    }

    private void save( final MongoCollection<Document> collection, final Document document )
    {
        final Object id = document.get( "_id" );

        if ( id == null )
        {
            collection.insertOne( document );
        } else
        {
            collection.replaceOne( Filters.eq( "_id", id ), document, new ReplaceOptions().upsert( true ) );
        }
    }

    private String format( String line )
    {
        return PlaceHolderAPI.formatMessage( line );
    }

    private MongoDBStorageManager manager()
    {
        return (MongoDBStorageManager) BungeeUtilisals.getInstance().getDatabaseManagement();
    }

    private MongoDatabase db()
    {
        return manager().getDatabase();
    }
}
