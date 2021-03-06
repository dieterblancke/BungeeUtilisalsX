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

package com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao.punishment;

import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.KickAndWarnDao;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

public class MongoKickAndWarnDao implements KickAndWarnDao
{

    @Override
    public PunishmentInfo insertWarn( UUID uuid, String user, String ip, String reason, String server, String executedby )
    {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
        data.put( "type", PunishmentType.WARN.toString() );
        data.put( "uuid", uuid.toString() );
        data.put( "user", user );
        data.put( "ip", ip );
        data.put( "reason", reason );
        data.put( "server", server );
        data.put( "date", new Date() );
        data.put( "executed_by", executedby );
        data.put( "punishmentaction_status", false );

        db().getCollection( PunishmentType.WARN.getTable() ).insertOne( new Document( data ) );
        return PunishmentDao.buildPunishmentInfo( PunishmentType.WARN, uuid, user, ip, reason, server, executedby, new Date(), -1, true, null );
    }

    @Override
    public PunishmentInfo insertKick( UUID uuid, String user, String ip, String reason, String server, String executedby )
    {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
        data.put( "type", PunishmentType.KICK.toString() );
        data.put( "uuid", uuid.toString() );
        data.put( "user", user );
        data.put( "ip", ip );
        data.put( "reason", reason );
        data.put( "server", server );
        data.put( "date", new Date() );
        data.put( "executed_by", executedby );
        data.put( "punishmentaction_status", false );

        db().getCollection( PunishmentType.KICK.getTable() ).insertOne( new Document( data ) );
        return PunishmentDao.buildPunishmentInfo( PunishmentType.KICK, uuid, user, ip, reason, server, executedby, new Date(), -1, true, null );
    }

    @Override
    public List<PunishmentInfo> getKicks( UUID uuid )
    {
        final List<PunishmentInfo> punishments = Lists.newArrayList();
        final MongoCollection<Document> collection = db().getCollection( PunishmentType.KICK.getTable() );
        final FindIterable<Document> documents = collection.find( Filters.eq( "uuid", uuid.toString() ) );

        for ( Document document : documents )
        {
            final PunishmentType type = Utils.valueOfOr( document.getString( "type" ), PunishmentType.KICK );

            final String id = document.getObjectId( "_id" ).toString();
            final String user = document.getString( "user" );
            final String ip = document.getString( "ip" );
            final String reason = document.getString( "reason" );
            final String server = document.getString( "server" );
            final String executedby = document.getString( "executed_by" );
            final Date date = document.getDate( "date" );

            punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, -1, true, null ) );
        }
        return punishments;
    }

    @Override
    public List<PunishmentInfo> getWarns( UUID uuid )
    {
        final List<PunishmentInfo> punishments = Lists.newArrayList();
        final MongoCollection<Document> collection = db().getCollection( PunishmentType.WARN.getTable() );
        final FindIterable<Document> documents = collection.find( Filters.eq( "uuid", uuid.toString() ) );

        for ( Document document : documents )
        {
            final PunishmentType type = Utils.valueOfOr( document.getString( "type" ), PunishmentType.WARN );

            final String id = document.getObjectId( "_id" ).toString();
            final String user = document.getString( "user" );
            final String ip = document.getString( "ip" );
            final String reason = document.getString( "reason" );
            final String server = document.getString( "server" );
            final String executedby = document.getString( "executed_by" );
            final Date date = document.getDate( "date" );

            punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, -1, true, null ) );
        }
        return punishments;
    }

    @Override
    public List<PunishmentInfo> getKicksExecutedBy( String name )
    {
        final List<PunishmentInfo> punishments = Lists.newArrayList();
        final MongoCollection<Document> collection = db().getCollection( PunishmentType.KICK.getTable() );
        final FindIterable<Document> documents = collection.find( Filters.eq( "executed_by", name ) );

        for ( Document document : documents )
        {
            final PunishmentType type = Utils.valueOfOr( document.getString( "type" ), PunishmentType.KICK );

            final String id = document.getObjectId( "_id" ).toString();
            final String user = document.getString( "user" );
            final UUID uuid = UUID.fromString( document.getString( "uuid" ) );
            final String ip = document.getString( "ip" );
            final String reason = document.getString( "reason" );
            final String server = document.getString( "server" );
            final String executedby = document.getString( "executed_by" );
            final Date date = document.getDate( "date" );

            punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, -1, true, null ) );
        }
        return punishments;
    }

    @Override
    public List<PunishmentInfo> getWarnsExecutedBy( String name )
    {
        final List<PunishmentInfo> punishments = Lists.newArrayList();
        final MongoCollection<Document> collection = db().getCollection( PunishmentType.WARN.getTable() );
        final FindIterable<Document> documents = collection.find( Filters.eq( "executed_by", name ) );

        for ( Document document : documents )
        {
            final PunishmentType type = Utils.valueOfOr( document.getString( "type" ), PunishmentType.WARN );

            final String id = document.getObjectId( "_id" ).toString();
            final String user = document.getString( "user" );
            final UUID uuid = UUID.fromString( document.getString( "uuid" ) );
            final String ip = document.getString( "ip" );
            final String reason = document.getString( "reason" );
            final String server = document.getString( "server" );
            final String executedby = document.getString( "executed_by" );
            final Date date = document.getDate( "date" );

            punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, -1, true, null ) );
        }
        return punishments;
    }

    @Override
    public PunishmentInfo getKickById( String id )
    {
        final MongoCollection<Document> collection = db().getCollection( PunishmentType.KICK.getTable() );
        final Document document = collection.find( Filters.eq( "_id", id ) ).first();

        if ( document != null )
        {
            final PunishmentType type = Utils.valueOfOr( document.getString( "type" ), PunishmentType.KICK );

            final UUID uuid = UUID.fromString( document.getString( "uuid" ) );
            final String user = document.getString( "user" );
            final String ip = document.getString( "ip" );
            final String reason = document.getString( "reason" );
            final String server = document.getString( "server" );
            final String executedby = document.getString( "executed_by" );
            final Date date = document.getDate( "date" );

            return PunishmentDao.buildPunishmentInfo( type, uuid, user, ip, reason, server, executedby, date, -1, true, null );
        }

        return null;
    }

    @Override
    public PunishmentInfo getWarnById( String id )
    {
        final MongoCollection<Document> collection = db().getCollection( PunishmentType.WARN.getTable() );
        final Document document = collection.find( Filters.eq( "_id", id ) ).first();

        if ( document != null )
        {
            final PunishmentType type = Utils.valueOfOr( document.getString( "type" ), PunishmentType.WARN );

            final UUID uuid = UUID.fromString( document.getString( "uuid" ) );
            final String user = document.getString( "user" );
            final String ip = document.getString( "ip" );
            final String reason = document.getString( "reason" );
            final String server = document.getString( "server" );
            final String executedby = document.getString( "executed_by" );
            final Date date = document.getDate( "date" );

            return PunishmentDao.buildPunishmentInfo( type, uuid, user, ip, reason, server, executedby, date, -1, true, null );
        }

        return null;
    }

    private MongoDatabase db()
    {
        return ( (MongoDBStorageManager) AbstractStorageManager.getManager() ).getDatabase();
    }
}
