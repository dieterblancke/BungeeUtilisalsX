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

import com.dbsoftwares.bungeeutilisals.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendRequest;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendSettingType;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendSettings;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.FriendsDao;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class MongoFriendsDao implements FriendsDao
{

    @Override
    public void addFriend( UUID user, UUID uuid )
    {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();

        data.put( "user", user );
        data.put( "friend", uuid );
        data.put( "created", new Date( System.currentTimeMillis() ) );

        db().getCollection( format( "{friends-table}" ) ).insertOne( new Document( data ) );
    }

    @Override
    public void removeFriend( UUID user, UUID uuid )
    {
        final MongoCollection<Document> coll = db().getCollection( format( "{friends-table}" ) );

        coll.deleteOne(
                Filters.and(
                        Filters.eq( "user", user ),
                        Filters.eq( "friend", uuid )
                )
        );
    }

    @Override
    public List<FriendData> getFriends( UUID uuid )
    {
        final List<FriendData> friends = Lists.newArrayList();
        final MongoCollection<Document> coll = db().getCollection( format( "{friends-table}" ) );
        final MongoCollection<Document> userColl = db().getCollection( format( "{users-table}" ) );

        coll.find( Filters.eq( "user", uuid ) ).forEach( (Block<? super Document>) doc ->
        {
            final Document friend = userColl.find( Filters.eq( "uuid", doc.getString( "friend" ) ) ).first();

            friends.add( new FriendData(
                    UUID.fromString( doc.getString( "friend" ) ),
                    friend.getString( "username" ),
                    doc.getDate( "created" ),
                    friend.getDate( "lastlogout" )
            ) );
        } );

        return friends;
    }

    @Override
    public long getAmountOfFriends( UUID uuid )
    {
        final MongoCollection<Document> coll = db().getCollection( format( "{friends-table}" ) );

        return coll.countDocuments( Filters.eq( "user", uuid ) );
    }

    @Override
    public void addFriendRequest( UUID user, UUID uuid )
    {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();

        data.put( "user", user );
        data.put( "friend", uuid );
        data.put( "requested_at", new Date( System.currentTimeMillis() ) );

        db().getCollection( format( "{friendrequests-table}" ) ).insertOne( new Document( data ) );
    }

    @Override
    public void removeFriendRequest( UUID user, UUID uuid )
    {
        final MongoCollection<Document> coll = db().getCollection( format( "{friendrequests-table}" ) );

        coll.deleteOne(
                Filters.and(
                        Filters.eq( "user", uuid ),
                        Filters.eq( "friend", user )
                )
        );
    }

    @Override
    public List<FriendRequest> getIncomingFriendRequests( UUID uuid )
    {
        final List<FriendRequest> friendRequests = Lists.newArrayList();
        final MongoCollection<Document> coll = db().getCollection( format( "{friendrequests-table}" ) );
        final MongoCollection<Document> userColl = db().getCollection( format( "{users-table}" ) );

        coll.find( Filters.eq( "friend", uuid ) ).forEach( (Block<? super Document>) doc ->
        {
            final Document friend = userColl.find( Filters.eq( "uuid", doc.getString( "user" ) ) ).first();

            friendRequests.add( new FriendRequest(
                    uuid,
                    friend.getString( "username" ),
                    UUID.fromString( doc.getString( "friend" ) ),
                    null,
                    doc.getDate( "requested_at" )
            ) );
        } );

        return friendRequests;
    }

    @Override
    public List<FriendRequest> getOutgoingFriendRequests( UUID uuid )
    {
        final List<FriendRequest> friendRequests = Lists.newArrayList();
        final MongoCollection<Document> coll = db().getCollection( format( "{friendrequests-table}" ) );
        final MongoCollection<Document> userColl = db().getCollection( format( "{users-table}" ) );

        coll.find( Filters.eq( "user", uuid ) ).forEach( (Block<? super Document>) doc ->
        {
            final Document friend = userColl.find( Filters.eq( "uuid", doc.getString( "friend" ) ) ).first();
            friendRequests.add( new FriendRequest(
                    uuid,
                    null,
                    UUID.fromString( doc.getString( "friend" ) ),
                    friend.getString( "username" ),
                    doc.getDate( "requested_at" )
            ) );
        } );

        return friendRequests;
    }

    @Override
    public boolean hasIncomingFriendRequest( UUID user, UUID uuid )
    {
        final MongoCollection<Document> coll = db().getCollection( format( "{friendrequests-table}" ) );

        return coll.find( Filters.and(
                Filters.eq( "user", uuid.toString() ),
                Filters.eq( "friend", user )
        ) ).limit( 1 ).iterator().hasNext();
    }

    @Override
    public boolean hasOutgoingFriendRequest( UUID user, UUID uuid )
    {
        final MongoCollection<Document> coll = db().getCollection( format( "{friendrequests-table}" ) );

        return coll.find( Filters.and(
                Filters.eq( "user", user ),
                Filters.eq( "friend", uuid.toString() )
        ) ).limit( 1 ).iterator().hasNext();
    }

    @Override
    public void setSetting( UUID uuid, FriendSettingType type, boolean value )
    {
        final MongoCollection<Document> coll = db().getCollection( format( "{friendsettings-table}" ) );
        final boolean exists = coll.find( Filters.eq( "user", uuid.toString() ) ).limit( 1 ).iterator().hasNext();

        if ( exists )
        {
            coll.updateOne(
                    Filters.eq( "user", uuid.toString() ),
                    Updates.set( type.toString().toLowerCase(), value )
            );
        }
        else
        {
            final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();

            data.put( "user", uuid.toString() );
            data.put( "requests", getValue( FriendSettingType.REQUESTS, type, value ) );
            data.put( "messages", getValue( FriendSettingType.MESSAGES, type, value ) );

            coll.insertOne( new Document( data ) );
        }
    }

    private boolean getValue( final FriendSettingType setting, final FriendSettingType type, final boolean value )
    {
        return setting.equals( type ) ? value : setting.getDefault();
    }

    @Override
    public boolean getSetting( UUID uuid, FriendSettingType type )
    {
        final MongoCollection<Document> coll = db().getCollection( format( "{friendsettings-table}" ) );
        final boolean exists = coll.find( Filters.eq( "user", uuid.toString() ) ).limit( 1 ).iterator().hasNext();

        if ( !exists )
        {
            return type.getDefault();
        }

        final Document document = coll.find( Filters.eq( "user", uuid.toString() ) ).limit( 1 ).first();
        return document.getBoolean( type.toString().toLowerCase() );
    }

    @Override
    public FriendSettings getSettings( UUID uuid )
    {
        final MongoCollection<Document> coll = db().getCollection( format( "{friendsettings-table}" ) );
        final boolean exists = coll.find( Filters.eq( "user", uuid.toString() ) ).limit( 1 ).iterator().hasNext();

        if ( !exists )
        {
            return new FriendSettings();
        }

        final Document document = coll.find( Filters.eq( "user", uuid.toString() ) ).limit( 1 ).first();
        return new FriendSettings(
                document.getBoolean( "requests" ),
                document.getBoolean( "messages" )
        );
    }

    private String format( String line )
    {
        return PlaceHolderAPI.formatMessage( line );
    }

    private String format( String line, Object... replacements )
    {
        return PlaceHolderAPI.formatMessage( String.format( line, replacements ) );
    }

    private MongoDatabase db()
    {
        return ( (MongoDBStorageManager) AbstractStorageManager.getManager() ).getDatabase();
    }
}
