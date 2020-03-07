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

import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.BansDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.KickAndWarnDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.MutesDao;
import com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao.punishment.MongoBansDao;
import com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao.punishment.MongoKickAndWarnDao;
import com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao.punishment.MongoMutesDao;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Sorts;
import org.bson.Document;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.function.Consumer;

public class MongoPunishmentDao implements PunishmentDao
{

    private final BansDao bansDao;
    private final MutesDao mutesDao;
    private final KickAndWarnDao kickAndWarnDao;

    public MongoPunishmentDao()
    {
        this.bansDao = new MongoBansDao();
        this.mutesDao = new MongoMutesDao();
        this.kickAndWarnDao = new MongoKickAndWarnDao();
    }

    @Override
    public BansDao getBansDao()
    {
        return bansDao;
    }

    @Override
    public MutesDao getMutesDao()
    {
        return mutesDao;
    }

    @Override
    public KickAndWarnDao getKickAndWarnDao()
    {
        return kickAndWarnDao;
    }

    @Override
    public long getPunishmentsSince( PunishmentType type, UUID uuid, Date date )
    {
        final MongoCollection<Document> collection = db().getCollection( type.getTable() );

        if ( type.isActivatable() )
        {
            return collection.countDocuments( Filters.and(
                    Filters.eq( "uuid", uuid.toString() ),
                    Filters.gte( "date", date ),
                    Filters.eq( "type", type.toString() ),
                    Filters.eq( "punishmentaction_status", false )
            ) );
        }
        else
        {
            return collection.countDocuments( Filters.and(
                    Filters.eq( "uuid", uuid ),
                    Filters.gte( "date", date ),
                    Filters.eq( "punishmentaction_status", false )
            ) );
        }
    }

    @Override
    public long getIPPunishmentsSince( PunishmentType type, String ip, Date date )
    {
        final MongoCollection<Document> collection = db().getCollection( type.getTable() );

        return collection.countDocuments( Filters.and(
                Filters.eq( "ip", ip ),
                Filters.gte( "date", date ),
                Filters.eq( "type", type.toString() ),
                Filters.eq( "punishmentaction_status", false )
        ) );
    }

    @Override
    public void updateActionStatus( int limit, PunishmentType type, UUID uuid, Date date )
    {
        final MongoCollection<Document> collection = db().getCollection( type.getTable() );

        if ( type.isActivatable() )
        {
            collection.find(
                    Filters.and(
                            Filters.eq( "uuid", uuid.toString() ),
                            Filters.gte( "date", date ),
                            Filters.eq( "type", type.toString() ),
                            Filters.eq( "punishmentaction_status", false )
                    ) )
                    .sort( Sorts.ascending( "date" ) )
                    .limit( limit )
                    .forEach( (Consumer<? super Document>) doc ->
                    {
                        doc.put( "punishmentaction_status", true );

                        save( collection, doc );
                    } );
        }
        else
        {
            collection.find(
                    Filters.and(
                            Filters.eq( "uuid", uuid.toString() ),
                            Filters.gte( "date", date ),
                            Filters.eq( "punishmentaction_status", false )
                    ) )
                    .sort( Sorts.ascending( "date" ) )
                    .limit( limit )
                    .forEach( (Consumer<? super Document>) doc ->
                    {
                        doc.put( "punishmentaction_status", true );

                        save( collection, doc );
                    } );
        }
    }

    @Override
    public void updateIPActionStatus( int limit, PunishmentType type, String ip, Date date )
    {
        final MongoCollection<Document> collection = db().getCollection( type.getTable() );

        collection.find(
                Filters.and(
                        Filters.eq( "ip", ip ),
                        Filters.gte( "date", date ),
                        Filters.eq( "type", type.toString() ),
                        Filters.eq( "punishmentaction_status", false )
                ) )
                .sort( Sorts.ascending( "date" ) )
                .limit( limit )
                .forEach( (Consumer<? super Document>) doc ->
                {
                    doc.put( "punishmentaction_status", true );

                    save( collection, doc );
                } );
    }

    @Override
    public void savePunishmentAction( UUID uuid, String username, String ip, String uid )
    {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();

        data.put( "uuid", uuid.toString() );
        data.put( "user", username );
        data.put( "ip", ip );
        data.put( "actionid", uid );
        data.put( "date", new Date() );

        db().getCollection( PlaceHolderAPI.formatMessage( "{punishmentactions-table}" ) ).insertOne( new Document( data ) );
    }

    // Recreating save api ...
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

    private MongoDatabase db()
    {
        return ((MongoDBStorageManager) AbstractStorageManager.getManager()).getDatabase();
    }
}
