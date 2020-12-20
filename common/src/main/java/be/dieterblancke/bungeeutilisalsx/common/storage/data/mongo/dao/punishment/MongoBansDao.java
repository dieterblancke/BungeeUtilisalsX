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

package be.dieterblancke.bungeeutilisalsx.common.storage.data.mongo.dao.punishment;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.PunishmentDao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.punishments.BansDao;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.client.FindIterable;
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

import static be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.punishments.BansDao.useServerPunishments;

public class MongoBansDao implements BansDao
{

    @Override
    public boolean isBanned( final UUID uuid, final String server )
    {
        final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "uuid", uuid.toString() ),
                Filters.eq( "active", true ),
                Filters.regex( "type", "^(?!IP.*$).*" )
        );
        if ( useServerPunishments() )
        {
            filters.add( Filters.eq( "server", server ) );
        }

        return db()
                .getCollection( PunishmentType.BAN.getTable() )
                .find( Filters.and( filters ) )
                .limit( 1 )
                .iterator()
                .hasNext();
    }

    @Override
    public boolean isIPBanned( final String ip, final String server )
    {
        final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "ip", ip ),
                Filters.eq( "active", true ),
                Filters.regex( "type", "IP*" )
        );
        if ( useServerPunishments() )
        {
            filters.add( Filters.eq( "server", server ) );
        }

        return db()
                .getCollection( PunishmentType.BAN.getTable() )
                .find( Filters.and( filters ) )
                .limit( 1 )
                .iterator()
                .hasNext();
    }

    @Override
    public boolean isBanned( final PunishmentType type, final UUID uuid, final String server )
    {
        if ( type.isIP() || !type.isBan() )
        {
            return false;
        }
        final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "uuid", uuid.toString() ),
                Filters.eq( "active", true ),
                Filters.eq( "type", type.toString() )
        );
        if ( useServerPunishments() )
        {
            filters.add( Filters.eq( "server", server ) );
        }

        return db()
                .getCollection( type.getTable() )
                .find( Filters.and( filters ) )
                .limit( 1 )
                .iterator()
                .hasNext();
    }

    @Override
    public boolean isIPBanned( final PunishmentType type, final String ip, final String server )
    {
        if ( !type.isBan() || !type.isIP() )
        {
            return false;
        }
        final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "ip", ip ),
                Filters.eq( "active", true ),
                Filters.eq( "type", type.toString() )
        );
        if ( useServerPunishments() )
        {
            filters.add( Filters.eq( "server", server ) );
        }

        return db()
                .getCollection( type.getTable() )
                .find( Filters.and( filters ) )
                .limit( 1 )
                .iterator()
                .hasNext();
    }

    @Override
    public PunishmentInfo insertBan( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby )
    {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
        data.put( "type", PunishmentType.BAN.toString() );
        data.put( "uuid", uuid.toString() );
        data.put( "user", user );
        data.put( "ip", ip );
        data.put( "reason", reason );
        data.put( "server", server );
        data.put( "date", new Date() );
        data.put( "duration", -1 );
        data.put( "active", active );
        data.put( "executed_by", executedby );
        data.put( "removed", false );
        data.put( "removed_by", null );
        data.put( "punishmentaction_status", false );

        db().getCollection( PunishmentType.BAN.getTable() ).insertOne( new Document( data ) );
        return PunishmentDao.buildPunishmentInfo( PunishmentType.BAN, uuid, user, ip, reason, server, executedby, new Date(), -1, active, null );
    }

    @Override
    public PunishmentInfo insertIPBan( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby )
    {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
        data.put( "type", PunishmentType.IPBAN.toString() );
        data.put( "uuid", uuid.toString() );
        data.put( "user", user );
        data.put( "ip", ip );
        data.put( "reason", reason );
        data.put( "server", server );
        data.put( "date", new Date() );
        data.put( "duration", -1 );
        data.put( "active", active );
        data.put( "executed_by", executedby );
        data.put( "removed", false );
        data.put( "removed_by", null );
        data.put( "punishmentaction_status", false );

        db().getCollection( PunishmentType.IPBAN.getTable() ).insertOne( new Document( data ) );
        return PunishmentDao.buildPunishmentInfo( PunishmentType.IPBAN, uuid, user, ip, reason, server, executedby, new Date(), -1, active, null );
    }

    @Override
    public PunishmentInfo insertTempBan( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration )
    {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
        data.put( "type", PunishmentType.TEMPBAN.toString() );
        data.put( "uuid", uuid.toString() );
        data.put( "user", user );
        data.put( "ip", ip );
        data.put( "reason", reason );
        data.put( "server", server );
        data.put( "date", new Date() );
        data.put( "duration", duration );
        data.put( "active", active );
        data.put( "executed_by", executedby );
        data.put( "removed", false );
        data.put( "removed_by", null );
        data.put( "punishmentaction_status", false );

        db().getCollection( PunishmentType.TEMPBAN.getTable() ).insertOne( new Document( data ) );
        return PunishmentDao.buildPunishmentInfo( PunishmentType.TEMPBAN, uuid, user, ip, reason, server, executedby, new Date(), duration, active, null );
    }

    @Override
    public PunishmentInfo insertTempIPBan( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration )
    {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
        data.put( "type", PunishmentType.IPTEMPBAN.toString() );
        data.put( "uuid", uuid.toString() );
        data.put( "user", user );
        data.put( "ip", ip );
        data.put( "reason", reason );
        data.put( "server", server );
        data.put( "date", new Date() );
        data.put( "duration", duration );
        data.put( "active", active );
        data.put( "executed_by", executedby );
        data.put( "removed", false );
        data.put( "removed_by", null );
        data.put( "punishmentaction_status", false );

        db().getCollection( PunishmentType.IPTEMPBAN.getTable() ).insertOne( new Document( data ) );
        return PunishmentDao.buildPunishmentInfo( PunishmentType.IPTEMPBAN, uuid, user, ip, reason, server, executedby, new Date(), duration, active, null );
    }

    @Override
    public PunishmentInfo getCurrentBan( final UUID uuid, final String serverName )
    {
        final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "uuid", uuid.toString() ),
                Filters.eq( "active", true ),
                Filters.regex( "type", "^(?!IP.*$).*" )
        );
        if ( useServerPunishments() )
        {
            filters.add( Filters.eq( "server", serverName ) );
        }
        final MongoCollection<Document> collection = db().getCollection( PunishmentType.BAN.getTable() );
        final Document document = collection.find( Filters.and( filters ) ).first();

        if ( document != null )
        {
            final PunishmentType type = Utils.valueOfOr( document.getString( "type" ), PunishmentType.BAN );

            final String user = document.getString( "user" );
            final String ip = document.getString( "ip" );
            final String reason = document.getString( "reason" );
            final String server = document.getString( "server" );
            final String executedby = document.getString( "executed_by" );
            final Date date = document.getDate( "date" );
            final long time = ( (Number) document.get( "duration" ) ).longValue();
            final boolean active = document.getBoolean( "active" );
            final String removedby = document.getString( "removed_by" );

            return PunishmentDao.buildPunishmentInfo( type, uuid, user, ip, reason, server, executedby, date, time, active, removedby );
        }

        return new PunishmentInfo();
    }

    @Override
    public PunishmentInfo getCurrentIPBan( final String ip, final String serverName )
    {
        final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "ip", ip ),
                Filters.eq( "active", true ),
                Filters.regex( "type", "IP*" )
        );
        if ( useServerPunishments() )
        {
            filters.add( Filters.eq( "server", serverName ) );
        }

        final MongoCollection<Document> collection = db().getCollection( PunishmentType.BAN.getTable() );
        final Document document = collection.find( Filters.and( filters ) ).first();

        if ( document != null )
        {
            final PunishmentType type = Utils.valueOfOr( document.getString( "type" ), PunishmentType.IPBAN );

            final UUID uuid = UUID.fromString( document.getString( "uuid" ) );
            final String user = document.getString( "user" );
            final String reason = document.getString( "reason" );
            final String server = document.getString( "server" );
            final String executedby = document.getString( "executed_by" );
            final Date date = document.getDate( "date" );
            final long time = ( (Number) document.get( "duration" ) ).longValue();
            final boolean active = document.getBoolean( "active" );
            final String removedby = document.getString( "removed_by" );

            return PunishmentDao.buildPunishmentInfo( type, uuid, user, ip, reason, server, executedby, date, time, active, removedby );
        }

        return new PunishmentInfo();
    }

    @Override
    public void removeCurrentBan( final UUID uuid, final String removedBy, final String serverName )
    {
        final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "uuid", uuid.toString() ),
                Filters.eq( "active", true ),
                Filters.regex( "type", "^(?!IP.*$).*" )
        );
        if ( useServerPunishments() )
        {
            filters.add( Filters.eq( "server", serverName ) );
        }
        final MongoCollection<Document> coll = db().getCollection( PunishmentType.BAN.getTable() );

        // updateMany, this if for some reason multiple bans would be active at the same time.
        coll.updateMany(
                Filters.and( filters ),
                Updates.combine(
                        Updates.set( "active", false ),
                        Updates.set( "removed", true ),
                        Updates.set( "removed_by", removedBy ),
                        Updates.set( "removed_at", new Date() )
                )
        );
    }

    @Override
    public void removeCurrentIPBan( final String ip, final String removedBy, final String serverName )
    {
        final List<Bson> filters = Lists.newArrayList(
                Filters.eq( "ip", ip ),
                Filters.eq( "active", true ),
                Filters.regex( "type", "IP*" )
        );
        if ( useServerPunishments() )
        {
            filters.add( Filters.eq( "server", serverName ) );
        }
        final MongoCollection<Document> coll = db().getCollection( PunishmentType.BAN.getTable() );

        // updateMany, this if for some reason multiple bans would be active at the same time.
        coll.updateMany(
                Filters.and( filters ),
                Updates.combine(
                        Updates.set( "active", false ),
                        Updates.set( "removed", true ),
                        Updates.set( "removed_by", removedBy ),
                        Updates.set( "removed_at", new Date() )
                )
        );
    }

    @Override
    public List<PunishmentInfo> getBans( UUID uuid )
    {
        final List<PunishmentInfo> punishments = Lists.newArrayList();
        final MongoCollection<Document> collection = db().getCollection( PunishmentType.BAN.getTable() );
        final FindIterable<Document> documents = collection.find( Filters.and(
                Filters.eq( "uuid", uuid.toString() ),
                Filters.regex( "type", "^(?!IP.*$).*" )
        ) );

        for ( Document document : documents )
        {
            final PunishmentType type = Utils.valueOfOr( document.getString( "type" ), PunishmentType.BAN );

            final String id = document.getObjectId( "_id" ).toString();
            final String user = document.getString( "user" );
            final String ip = document.getString( "ip" );
            final String reason = document.getString( "reason" );
            final String server = document.getString( "server" );
            final String executedby = document.getString( "executed_by" );
            final Date date = document.getDate( "date" );
            final long time = ( (Number) document.get( "duration" ) ).longValue();
            final boolean active = document.getBoolean( "active" );
            final String removedby = document.getString( "removed_by" );

            punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby ) );
        }
        return punishments;
    }

    @Override
    public List<PunishmentInfo> getBansExecutedBy( String name )
    {
        final List<PunishmentInfo> punishments = Lists.newArrayList();
        final MongoCollection<Document> collection = db().getCollection( PunishmentType.BAN.getTable() );
        final FindIterable<Document> documents = collection.find( Filters.eq( "executed_by", name ) );

        for ( Document document : documents )
        {
            final PunishmentType type = Utils.valueOfOr( document.getString( "type" ), PunishmentType.BAN );

            final String id = document.getObjectId( "_id" ).toString();
            final String user = document.getString( "user" );
            final UUID uuid = UUID.fromString( document.getString( "uuid" ) );
            final String ip = document.getString( "ip" );
            final String reason = document.getString( "reason" );
            final String server = document.getString( "server" );
            final String executedby = document.getString( "executed_by" );
            final Date date = document.getDate( "date" );
            final long time = ( (Number) document.get( "duration" ) ).longValue();
            final boolean active = document.getBoolean( "active" );
            final String removedby = document.getString( "removed_by" );

            punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby ) );
        }
        return punishments;
    }

    @Override
    public List<PunishmentInfo> getBans( final UUID uuid, final String serverName )
    {
        final List<PunishmentInfo> punishments = Lists.newArrayList();
        final MongoCollection<Document> collection = db().getCollection( PunishmentType.BAN.getTable() );
        final FindIterable<Document> documents = collection.find( Filters.and(
                Filters.eq( "uuid", uuid.toString() ),
                Filters.regex( "type", "^(?!IP.*$).*" ),
                Filters.eq( "server", serverName )
        ) );

        for ( Document document : documents )
        {
            final PunishmentType type = Utils.valueOfOr( document.getString( "type" ), PunishmentType.BAN );

            final String id = document.getObjectId( "_id" ).toString();
            final String user = document.getString( "user" );
            final String ip = document.getString( "ip" );
            final String reason = document.getString( "reason" );
            final String server = document.getString( "server" );
            final String executedby = document.getString( "executed_by" );
            final Date date = document.getDate( "date" );
            final long time = ( (Number) document.get( "duration" ) ).longValue();
            final boolean active = document.getBoolean( "active" );
            final String removedby = document.getString( "removed_by" );

            punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby ) );
        }
        return punishments;
    }

    @Override
    public List<PunishmentInfo> getIPBans( final String ip )
    {
        final List<PunishmentInfo> punishments = Lists.newArrayList();
        final MongoCollection<Document> collection = db().getCollection( PunishmentType.IPBAN.getTable() );
        final FindIterable<Document> documents = collection.find( Filters.and(
                Filters.eq( "ip", ip ),
                Filters.regex( "type", "IP*" )
        ) );

        for ( Document document : documents )
        {
            final PunishmentType type = Utils.valueOfOr( document.getString( "type" ), PunishmentType.IPBAN );

            final String id = document.getObjectId( "_id" ).toString();
            final UUID uuid = UUID.fromString( document.getString( "uuid" ) );
            final String user = document.getString( "user" );
            final String reason = document.getString( "reason" );
            final String server = document.getString( "server" );
            final String executedby = document.getString( "executed_by" );
            final Date date = document.getDate( "date" );
            final long time = ( (Number) document.get( "duration" ) ).longValue();
            final boolean active = document.getBoolean( "active" );
            final String removedby = document.getString( "removed_by" );

            punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby ) );
        }
        return punishments;
    }

    @Override
    public List<PunishmentInfo> getIPBans( final String ip, final String serverName )
    {
        final List<PunishmentInfo> punishments = Lists.newArrayList();
        final MongoCollection<Document> collection = db().getCollection( PunishmentType.IPBAN.getTable() );
        final FindIterable<Document> documents = collection.find( Filters.and(
                Filters.eq( "ip", ip ),
                Filters.regex( "type", "IP*" ),
                Filters.eq( "server", serverName )
        ) );

        for ( Document document : documents )
        {
            final PunishmentType type = Utils.valueOfOr( document.getString( "type" ), PunishmentType.IPBAN );

            final String id = document.getObjectId( "_id" ).toString();
            final UUID uuid = UUID.fromString( document.getString( "uuid" ) );
            final String user = document.getString( "user" );
            final String reason = document.getString( "reason" );
            final String server = document.getString( "server" );
            final String executedby = document.getString( "executed_by" );
            final Date date = document.getDate( "date" );
            final long time = ( (Number) document.get( "duration" ) ).longValue();
            final boolean active = document.getBoolean( "active" );
            final String removedby = document.getString( "removed_by" );

            punishments.add( PunishmentDao.buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby ) );
        }
        return punishments;
    }

    @Override
    public PunishmentInfo getById( String id )
    {
        final MongoCollection<Document> collection = db().getCollection( PunishmentType.BAN.getTable() );
        final Document document = collection.find( Filters.eq( "_id", id ) ).first();

        if ( document != null )
        {
            final PunishmentType type = Utils.valueOfOr( document.getString( "type" ), PunishmentType.BAN );

            final UUID uuid = UUID.fromString( document.getString( "uuid" ) );
            final String user = document.getString( "user" );
            final String ip = document.getString( "ip" );
            final String reason = document.getString( "reason" );
            final String server = document.getString( "server" );
            final String executedby = document.getString( "executed_by" );
            final Date date = document.getDate( "date" );
            final long time = ( (Number) document.get( "duration" ) ).longValue();
            final boolean active = document.getBoolean( "active" );
            final String removedby = document.getString( "removed_by" );

            return PunishmentDao.buildPunishmentInfo( type, uuid, user, ip, reason, server, executedby, date, time, active, removedby );
        }

        return null;
    }

    private MongoDatabase db()
    {
        return ( (MongoDBStorageManager) BuX.getInstance().getAbstractStorageManager() ).getDatabase();
    }
}
