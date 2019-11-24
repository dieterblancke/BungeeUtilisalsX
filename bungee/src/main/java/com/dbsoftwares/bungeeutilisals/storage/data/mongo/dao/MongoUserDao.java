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
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.UserDao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class MongoUserDao implements UserDao
{

    @Override
    public void createUser( UUID uuid, String username, String ip, Language language )
    {
        final Date date = new Date( System.currentTimeMillis() );

        createUser( uuid, username, ip, language, date, date );
    }

    @Override
    public void createUser( UUID uuid, String username, String ip, Language language, Date login, Date logout )
    {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();

        data.put( "uuid", uuid.toString() );
        data.put( "username", username );
        data.put( "ip", ip );
        data.put( "language", language.getName() );
        data.put( "firstlogin", login );
        data.put( "lastlogout", logout );

        db().getCollection( format( "{users-table}" ) ).insertOne( new Document( data ) );
    }

    @Override
    public void updateUser( UUID uuid, String name, String ip, Language language, Date date )
    {
        List<Bson> updates = Lists.newArrayList();

        updates.add( Updates.set( "username", name ) );
        updates.add( Updates.set( "ip", ip ) );
        updates.add( Updates.set( "language", language.getName() ) );
        updates.add( Updates.set( "lastlogout", date ) );

        db().getCollection( format( "{users-table}" ) )
                .findOneAndUpdate( Filters.eq( "uuid", uuid.toString() ), Updates.combine( updates ) );
    }

    @Override
    public boolean exists( String name )
    {
        return db().getCollection( format( "{users-table}" ) ).find(
                name.contains( "." ) ? Filters.eq( "ip", name ) : Filters.eq( "username", name )
        ).iterator().hasNext();
    }

    @Override
    public boolean exists( UUID uuid )
    {
        return db().getCollection( format( "{users-table}" ) )
                .find( Filters.eq( "uuid", uuid.toString() ) ).iterator().hasNext();
    }

    @Override
    public boolean ipExists( String ip )
    {
        return db().getCollection( format( "{users-table}" ) )
                .find( Filters.eq( "ip", ip ) ).iterator().hasNext();
    }

    @Override
    public UserStorage getUserData( UUID uuid )
    {
        final UserStorage storage = new UserStorage();
        final MongoCollection<Document> ignoredUsersColl = db().getCollection( format( "{ignoredusers-table}" ) );
        final MongoCollection<Document> userColl = db().getCollection( format( "{users-table}" ) );

        final Document document = userColl.find( Filters.eq( "uuid", uuid.toString() ) ).first();

        if ( document != null )
        {
            storage.setUuid( uuid );
            storage.setUserName( document.getString( "username" ) );
            storage.setIp( document.getString( "ip" ) );
            storage.setLanguage(
                    BUCore.getApi().getLanguageManager().getLangOrDefault( document.getString( "language" ) )
            );
            storage.setFirstLogin( document.getDate( "lastlogin" ) );
            storage.setLastLogout( document.getDate( "lastlogout" ) );

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
        final MongoCollection<Document> ignoredUsersColl = db().getCollection( format( "{ignoredusers-table}" ) );
        final MongoCollection<Document> userColl = db().getCollection( format( "{users-table}" ) );

        final Document document = userColl.find( Filters.eq( "username", name ) ).first();

        if ( document != null )
        {
            storage.setUuid( UUID.fromString( document.getString( "uuid" ) ) );
            storage.setUserName( name );
            storage.setIp( document.getString( "ip" ) );
            storage.setLanguage(
                    BUCore.getApi().getLanguageManager().getLangOrDefault( document.getString( "language" ) )
            );
            storage.setFirstLogin( document.getDate( "lastlogin" ) );
            storage.setLastLogout( document.getDate( "lastlogout" ) );

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

        for ( Document document : db().getCollection( format( "{users-table}" ) ).find( Filters.eq( "ip", ip ) ) )
        {
            users.add( document.getString( "username" ) );
        }

        return users;
    }

    @Override
    public Language getLanguage( UUID uuid )
    {
        Language language = null;

        Document document = db().getCollection( format( "{users-table}" ) ).find( Filters.eq( "uuid", uuid.toString() ) ).first();

        if ( document != null )
        {
            language = BUCore.getApi().getLanguageManager().getLangOrDefault( document.getString( "language" ) );
        }

        return language;
    }

    @Override
    public void setName( UUID uuid, String name )
    {
        db().getCollection( format( "{users-table}" ) )
                .findOneAndUpdate( Filters.eq( "uuid", uuid.toString() ), Updates.set( "username", name ) );
    }

    @Override
    public void setIP( UUID uuid, String ip )
    {
        db().getCollection( format( "{users-table}" ) )
                .findOneAndUpdate( Filters.eq( "uuid", uuid.toString() ), Updates.set( "ip", ip ) );
    }

    @Override
    public void setLanguage( UUID uuid, Language language )
    {
        db().getCollection( format( "{users-table}" ) )
                .findOneAndUpdate( Filters.eq( "uuid", uuid.toString() ), Updates.set( "language", language.getName() ) );
    }

    @Override
    public void setLogout( UUID uuid, Date logout )
    {
        db().getCollection( format( "{users-table}" ) )
                .findOneAndUpdate( Filters.eq( "uuid", uuid.toString() ), Updates.set( "lastlogout", logout ) );
    }

    @Override
    public void ignoreUser( UUID user, UUID ignore )
    {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
        final MongoCollection<Document> ignoredUsersColl = db().getCollection( format( "{ignoredusers-table}" ) );

        data.put( "user", user.toString() );
        data.put( "ignored", ignore.toString() );

        ignoredUsersColl.insertOne( new Document( data ) );
    }

    @Override
    public void unignoreUser( UUID user, UUID unignore )
    {
        final MongoCollection<Document> ignoredUsersColl = db().getCollection( format( "{ignoredusers-table}" ) );

        ignoredUsersColl.findOneAndDelete(
                Filters.and(
                        Filters.eq( "user", user.toString() ),
                        Filters.eq( "ignored", unignore.toString() )
                )
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
        return ((MongoDBStorageManager) BungeeUtilisals.getInstance().getDatabaseManagement()).getDatabase();
    }
}
