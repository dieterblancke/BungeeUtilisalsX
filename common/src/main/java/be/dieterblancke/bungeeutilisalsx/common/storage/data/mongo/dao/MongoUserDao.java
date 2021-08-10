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
import be.dieterblancke.bungeeutilisalsx.common.api.language.Language;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.UserDao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MongoUserDao implements UserDao
{

    @Override
    public void createUser( UUID uuid, String username, String ip, Language language, String joinedHost )
    {
        final Date date = new Date( System.currentTimeMillis() );

        createUser( uuid, username, ip, language, date, date, joinedHost );
    }

    @Override
    public void createUser( UUID uuid, String username, String ip, Language language, Date login, Date logout, String joinedHost )
    {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();

        data.put( "uuid", uuid.toString() );
        data.put( "username", username );
        data.put( "ip", ip );
        data.put( "language", language.getName() );
        data.put( "firstlogin", login );
        data.put( "lastlogout", logout );
        data.put( "joined_host", joinedHost );
        data.put( "current_server", null );

        db().getCollection( "bu_users" ).insertOne( new Document( data ) );
    }

    @Override
    public void updateUser( UUID uuid, String name, String ip, Language language, Date date )
    {
        List<Bson> updates = Lists.newArrayList();

        updates.add( Updates.set( "username", name ) );
        updates.add( Updates.set( "ip", ip ) );
        updates.add( Updates.set( "language", language.getName() ) );
        updates.add( Updates.set( "lastlogout", date ) );

        db().getCollection( "bu_users" )
                .findOneAndUpdate( Filters.eq( "uuid", uuid.toString() ), Updates.combine( updates ) );
    }

    @Override
    public boolean exists( String name )
    {
        return db().getCollection( "bu_users" ).find(
                name.contains( "." ) ? Filters.eq( "ip", name ) : Filters.eq( "username", name )
        ).iterator().hasNext();
    }

    @Override
    public boolean exists( UUID uuid )
    {
        return db().getCollection( "bu_users" )
                .find( Filters.eq( "uuid", uuid.toString() ) ).iterator().hasNext();
    }

    @Override
    public boolean ipExists( String ip )
    {
        return db().getCollection( "bu_users" )
                .find( Filters.eq( "ip", ip ) ).iterator().hasNext();
    }

    @Override
    public UserStorage getUserData( UUID uuid )
    {
        final UserStorage storage = new UserStorage();
        final MongoCollection<Document> ignoredUsersColl = db().getCollection( "bu_ignoredusers" );
        final MongoCollection<Document> userColl = db().getCollection( "bu_users" );

        final Document document = userColl.find( Filters.eq( "uuid", uuid.toString() ) ).first();

        if ( document != null )
        {
            storage.setUuid( uuid );
            storage.setUserName( document.getString( "username" ) );
            storage.setIp( document.getString( "ip" ) );
            storage.setLanguage(
                    BuX.getApi().getLanguageManager().getLangOrDefault( document.getString( "language" ) )
            );
            storage.setFirstLogin( document.getDate( "lastlogin" ) );
            storage.setLastLogout( document.getDate( "lastlogout" ) );
            storage.setJoinedHost( document.getString( "joined_host" ) );

            ignoredUsersColl.find(
                    Filters.eq( "user", storage.getUuid().toString() )
            ).forEach( (Consumer<? super Document>) doc ->
            {
                final UUID ignoredUuid = UUID.fromString( doc.getString( "ignored" ) );
                final Document ignoredDoc = userColl.find( Filters.eq( "uuid", ignoredUuid.toString() ) ).first();

                if ( ignoredDoc != null )
                {
                    storage.getIgnoredUsers().add( ignoredDoc.getString( "username" ) );
                }
            } );
        }

        return storage;
    }

    @Override
    public UserStorage getUserData( String name )
    {
        final UserStorage storage = new UserStorage();
        final MongoCollection<Document> ignoredUsersColl = db().getCollection( "bu_ignoredusers" );
        final MongoCollection<Document> userColl = db().getCollection( "bu_users" );

        final Document document = userColl.find( Filters.eq( "username", name ) ).first();

        if ( document != null )
        {
            storage.setUuid( UUID.fromString( document.getString( "uuid" ) ) );
            storage.setUserName( name );
            storage.setIp( document.getString( "ip" ) );
            storage.setLanguage(
                    BuX.getApi().getLanguageManager().getLangOrDefault( document.getString( "language" ) )
            );
            storage.setFirstLogin( document.getDate( "lastlogin" ) );
            storage.setLastLogout( document.getDate( "lastlogout" ) );
            storage.setJoinedHost( document.getString( "joined_host" ) );

            ignoredUsersColl.find(
                    Filters.eq( "user", storage.getUuid().toString() )
            ).forEach( (Consumer<? super Document>) doc ->
            {
                final UUID ignoredUuid = UUID.fromString( doc.getString( "ignored" ) );
                final Document ignoredDoc = userColl.find( Filters.eq( "uuid", ignoredUuid.toString() ) ).first();

                if ( ignoredDoc != null )
                {
                    storage.getIgnoredUsers().add( ignoredDoc.getString( "username" ) );
                }
            } );
        }

        return storage;
    }

    @Override
    public List<String> getUsersOnIP( String ip )
    {
        List<String> users = Lists.newArrayList();

        for ( Document document : db().getCollection( "bu_users" ).find( Filters.eq( "ip", ip ) ) )
        {
            users.add( document.getString( "username" ) );
        }

        return users;
    }

    @Override
    public Language getLanguage( UUID uuid )
    {
        Language language = null;

        Document document = db().getCollection( "bu_users" ).find( Filters.eq( "uuid", uuid.toString() ) ).first();

        if ( document != null )
        {
            language = BuX.getApi().getLanguageManager().getLangOrDefault( document.getString( "language" ) );
        }

        return language;
    }

    @Override
    public void setName( UUID uuid, String name )
    {
        db().getCollection( "bu_users" )
                .findOneAndUpdate( Filters.eq( "uuid", uuid.toString() ), Updates.set( "username", name ) );
    }

    @Override
    public void setIP( UUID uuid, String ip )
    {
        db().getCollection( "bu_users" )
                .findOneAndUpdate( Filters.eq( "uuid", uuid.toString() ), Updates.set( "ip", ip ) );
    }

    @Override
    public void setLanguage( UUID uuid, Language language )
    {
        db().getCollection( "bu_users" )
                .findOneAndUpdate( Filters.eq( "uuid", uuid.toString() ), Updates.set( "language", language.getName() ) );
    }

    @Override
    public void setLogout( UUID uuid, Date logout )
    {
        db().getCollection( "bu_users" )
                .findOneAndUpdate( Filters.eq( "uuid", uuid.toString() ), Updates.set( "lastlogout", logout ) );
    }

    @Override
    public void setJoinedHost( UUID uuid, String joinedHost )
    {
        db().getCollection( "bu_users" )
                .findOneAndUpdate( Filters.eq( "uuid", uuid.toString() ), Updates.set( "joined_host", joinedHost ) );
    }

    @Override
    public Map<String, Integer> getJoinedHostList()
    {
        final Map<String, Integer> map = Maps.newHashMap();
        final MongoCollection<Document> collection = db().getCollection( "bu_users" );

        for ( Document doc : collection.aggregate( Collections.singletonList(
                Aggregates.group( "$joined_host", Accumulators.sum( "count", 1 ) )
        ) ) )
        {
            final String joinedHost = doc.getString( "_id" );

            if ( joinedHost != null )
            {
                map.put( joinedHost, doc.getInteger( "count" ) );
            }
        }

        return map;
    }

    @Override
    public Map<String, Integer> searchJoinedHosts( String searchTag )
    {
        final Map<String, Integer> map = Maps.newHashMap();
        final MongoCollection<Document> collection = db().getCollection( "bu_users" );

        for ( Document doc : collection.aggregate( Arrays.asList(
                Aggregates.match( Filters.regex( "joined_host", searchTag ) ),
                Aggregates.group( "$joined_host", Accumulators.sum( "count", 1 ) )
        ) ) )
        {
            final String joinedHost = doc.getString( "_id" );

            if ( joinedHost != null )
            {
                map.put( joinedHost, doc.getInteger( "count" ) );
            }
        }

        return map;
    }

    @Override
    public void ignoreUser( UUID user, UUID ignore )
    {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
        final MongoCollection<Document> ignoredUsersColl = db().getCollection( "bu_ignoredusers" );

        data.put( "user", user.toString() );
        data.put( "ignored", ignore.toString() );

        ignoredUsersColl.insertOne( new Document( data ) );
    }

    @Override
    public void unignoreUser( UUID user, UUID unignore )
    {
        final MongoCollection<Document> ignoredUsersColl = db().getCollection( "bu_ignoredusers" );

        ignoredUsersColl.findOneAndDelete(
                Filters.and(
                        Filters.eq( "user", user.toString() ),
                        Filters.eq( "ignored", unignore.toString() )
                )
        );
    }

    @Override
    public void setCurrentServer( final UUID uuid, final String server )
    {
        db().getCollection( "bu_users" )
                .findOneAndUpdate( Filters.eq( "uuid", uuid.toString() ), Updates.set( "current_server", server ) );
    }

    @Override
    public String getCurrentServer( final UUID uuid )
    {
        Document document = db().getCollection( "bu_users" )
                .find( Filters.eq( "uuid", uuid.toString() ) )
                .first();

        if ( document != null )
        {
            return document.getString( "current_server" );
        }

        return null;
    }

    @Override
    public void setCurrentServerBulk( final List<UUID> users, final String server )
    {
        db().getCollection( "bu_users" ).updateMany(
                Filters.in( "uuid", users.stream().map( UUID::toString ).collect( Collectors.toList() ) ),
                Updates.set( "current_server", server )
        );
    }

    @Override
    public Map<String, String> getCurrentServersBulk( final List<String> users )
    {
        final Map<String, String> userServers = new HashMap<>();

        if ( users.isEmpty() )
        {
            return userServers;
        }
        final FindIterable<Document> documents = db().getCollection( "bu_users" )
                .find( Filters.in( "username", users ) );

        for ( Document document : documents )
        {
            userServers.put( document.getString( "username" ), document.getString( "current_server" ) );
        }

        return userServers;
    }

    private MongoDatabase db()
    {
        return ( (MongoDBStorageManager) BuX.getInstance().getAbstractStorageManager() ).getDatabase();
    }
}
