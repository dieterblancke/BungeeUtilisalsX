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
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequest;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSetting;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSettings;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.FriendsDao;
import be.dieterblancke.bungeeutilisalsx.common.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class MongoFriendsDao implements FriendsDao
{

    @Override
    public CompletableFuture<Void> addFriend( final UUID user, final UUID uuid )
    {
        return CompletableFuture.runAsync( () ->
        {
            final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();

            data.put( "user", user.toString() );
            data.put( "friend", uuid.toString() );
            data.put( "created", new Date( System.currentTimeMillis() ) );

            db().getCollection( "bu_friends" ).insertOne( new Document( data ) );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> removeFriend( final UUID user, final UUID uuid )
    {
        return CompletableFuture.runAsync( () ->
        {
            final MongoCollection<Document> coll = db().getCollection( "bu_friends" );

            coll.deleteOne(
                    Filters.and(
                            Filters.eq( "user", user.toString() ),
                            Filters.eq( "friend", uuid.toString() )
                    )
            );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<FriendData>> getFriends( final UUID uuid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<FriendData> friends = Lists.newArrayList();
            final MongoCollection<Document> coll = db().getCollection( "bu_friends" );
            final MongoCollection<Document> userColl = db().getCollection( "bu_users" );

            coll.find( Filters.eq( "user", uuid.toString() ) ).forEach( (Consumer<? super Document>) doc ->
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
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Long> getAmountOfFriends( final UUID uuid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final MongoCollection<Document> coll = db().getCollection( "bu_friends" );

            return coll.countDocuments( Filters.eq( "user", uuid.toString() ) );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> addFriendRequest( final UUID user, final UUID uuid )
    {
        return CompletableFuture.runAsync( () ->
        {
            final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();

            data.put( "user", user.toString() );
            data.put( "friend", uuid.toString() );
            data.put( "requested_at", new Date( System.currentTimeMillis() ) );

            db().getCollection( "bu_friendrequests" ).insertOne( new Document( data ) );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> removeFriendRequest( final UUID user, final UUID uuid )
    {
        return CompletableFuture.runAsync( () ->
        {
            final MongoCollection<Document> coll = db().getCollection( "bu_friendrequests" );

            coll.deleteOne(
                    Filters.and(
                            Filters.eq( "user", uuid.toString() ),
                            Filters.eq( "friend", user.toString() )
                    )
            );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<FriendRequest>> getIncomingFriendRequests( final UUID uuid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<FriendRequest> friendRequests = Lists.newArrayList();
            final MongoCollection<Document> coll = db().getCollection( "bu_friendrequests" );
            final MongoCollection<Document> userColl = db().getCollection( "bu_users" );

            coll.find( Filters.eq( "friend", uuid.toString() ) ).forEach( (Consumer<? super Document>) doc ->
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
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<FriendRequest>> getOutgoingFriendRequests( final UUID uuid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<FriendRequest> friendRequests = Lists.newArrayList();
            final MongoCollection<Document> coll = db().getCollection( "bu_friendrequests" );
            final MongoCollection<Document> userColl = db().getCollection( "bu_users" );

            coll.find( Filters.eq( "user", uuid.toString() ) ).forEach( (Consumer<? super Document>) doc ->
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
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Boolean> hasIncomingFriendRequest( final UUID user, final UUID uuid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final MongoCollection<Document> coll = db().getCollection( "bu_friendrequests" );

            return coll.find( Filters.and(
                    Filters.eq( "user", uuid.toString() ),
                    Filters.eq( "friend", user.toString() )
            ) ).limit( 1 ).iterator().hasNext();
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Boolean> hasOutgoingFriendRequest( final UUID user, final UUID uuid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final MongoCollection<Document> coll = db().getCollection( "bu_friendrequests" );

            return coll.find( Filters.and(
                    Filters.eq( "user", user.toString() ),
                    Filters.eq( "friend", uuid.toString() )
            ) ).limit( 1 ).iterator().hasNext();
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> setSetting( final UUID uuid, final FriendSetting type, final boolean value )
    {
        return CompletableFuture.runAsync( () ->
        {
            final MongoCollection<Document> coll = db().getCollection( "bu_friendsettings" );
            final boolean exists = coll.find( Filters.and(
                    Filters.eq( "user", uuid.toString() ),
                    Filters.eq( "setting", type.toString() )
            ) ).limit( 1 ).iterator().hasNext();

            if ( exists )
            {
                coll.updateOne(
                        Filters.and(
                                Filters.eq( "user", uuid.toString() ),
                                Filters.eq( "setting", type.toString() )
                        ),
                        Updates.set( type.toString().toLowerCase(), value )
                );
            }
            else
            {
                coll.insertOne( new Document()
                        .append( "user", uuid.toString() )
                        .append( "setting", type.toString() )
                        .append( "value", value ) );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Boolean> getSetting( final UUID uuid, final FriendSetting type )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final MongoCollection<Document> coll = db().getCollection( "bu_friendsettings" );
            final boolean exists = coll.find( Filters.and(
                    Filters.eq( "user", uuid.toString() ),
                    Filters.eq( "setting", type.toString() )
            ) ).limit( 1 ).iterator().hasNext();

            if ( !exists )
            {
                return type.getDefault();
            }

            final Document document = coll.find( Filters.eq( "user", uuid.toString() ) ).limit( 1 ).first();
            return document.getBoolean( type.toString().toLowerCase() );
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<FriendSettings> getSettings( UUID uuid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final MongoCollection<Document> coll = db().getCollection( "bu_friendsettings" );
            final boolean exists = coll.find( Filters.eq( "user", uuid.toString() ) ).limit( 1 ).iterator().hasNext();

            if ( !exists )
            {
                return new FriendSettings();
            }
            final FriendSettings friendSettings = new FriendSettings();

            coll.find( Filters.eq( "user", uuid.toString() ) ).forEach( (Consumer<Document>) doc ->
            {
                final FriendSetting setting = FriendSetting.valueOf( doc.getString( "setting" ) );

                friendSettings.set(
                        setting,
                        doc.getBoolean( "value" )
                );
            } );

            return friendSettings;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    private MongoDatabase db()
    {
        return ( (MongoDBStorageManager) BuX.getInstance().getAbstractStorageManager() ).getDatabase();
    }
}
