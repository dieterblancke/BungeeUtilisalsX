/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *  *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *  *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao.punishment;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.BansDao;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;

public class MongoBansDao implements BansDao {

    @Override
    public boolean isBanned(UUID uuid) {
        return db()
                .getCollection(PunishmentType.BAN.getTable())
                .find(Filters.and(
                        Filters.eq("uuid", uuid.toString()),
                        Filters.eq("active", true),
                        Filters.regex("type", "^(?!IP.*$).*")
                ))
                .limit(1)
                .iterator()
                .hasNext();
    }

    @Override
    public boolean isIPBanned(String ip) {
        return db()
                .getCollection(PunishmentType.BAN.getTable())
                .find(Filters.and(
                        Filters.eq("ip", ip),
                        Filters.eq("active", true),
                        Filters.regex("type", "IP*")
                ))
                .limit(1)
                .iterator()
                .hasNext();
    }

    @Override
    public boolean isBanned(PunishmentType type, UUID uuid) {
        if (type.isIP() || !type.isBan()) {
            return false;
        }
        return db()
                .getCollection(type.getTable())
                .find(Filters.and(
                        Filters.eq("uuid", uuid.toString()),
                        Filters.eq("active", true),
                        Filters.eq("type", type.toString())
                ))
                .limit(1)
                .iterator()
                .hasNext();
    }

    @Override
    public boolean isIPBanned(PunishmentType type, String ip) {
        if (!type.isBan() || !type.isIP()) {
            return false;
        }
        return db()
                .getCollection(type.getTable())
                .find(Filters.and(
                        Filters.eq("ip", ip),
                        Filters.eq("active", true),
                        Filters.eq("type", type.toString())
                ))
                .limit(1)
                .iterator()
                .hasNext();
    }

    @Override
    public PunishmentInfo insertBan(UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby) {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
        data.put("type", PunishmentType.BAN.toString());
        data.put("uuid", uuid.toString());
        data.put("user", user);
        data.put("ip", ip);
        data.put("reason", reason);
        data.put("server", server);
        data.put("date", new Date());
        data.put("duration", -1);
        data.put("active", active);
        data.put("executed_by", executedby);
        data.put("removed", false);
        data.put("removed_by", null);

        db().getCollection(PunishmentType.BAN.getTable()).insertOne(new Document(data));
        return PunishmentDao.buildPunishmentInfo(PunishmentType.BAN, uuid, user, ip, reason, server, executedby, new Date(), -1, active, null);
    }

    @Override
    public PunishmentInfo insertIPBan(UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby) {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
        data.put("type", PunishmentType.IPBAN.toString());
        data.put("uuid", uuid.toString());
        data.put("user", user);
        data.put("ip", ip);
        data.put("reason", reason);
        data.put("server", server);
        data.put("date", new Date());
        data.put("duration", -1);
        data.put("active", active);
        data.put("executed_by", executedby);
        data.put("removed", false);
        data.put("removed_by", null);

        db().getCollection(PunishmentType.IPBAN.getTable()).insertOne(new Document(data));
        return PunishmentDao.buildPunishmentInfo(PunishmentType.IPBAN, uuid, user, ip, reason, server, executedby, new Date(), -1, active, null);
    }

    @Override
    public PunishmentInfo insertTempBan(UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration) {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
        data.put("type", PunishmentType.TEMPBAN.toString());
        data.put("uuid", uuid.toString());
        data.put("user", user);
        data.put("ip", ip);
        data.put("reason", reason);
        data.put("server", server);
        data.put("date", new Date());
        data.put("duration", duration);
        data.put("active", active);
        data.put("executed_by", executedby);
        data.put("removed", false);
        data.put("removed_by", null);

        db().getCollection(PunishmentType.TEMPBAN.getTable()).insertOne(new Document(data));
        return PunishmentDao.buildPunishmentInfo(PunishmentType.TEMPBAN, uuid, user, ip, reason, server, executedby, new Date(), duration, active, null);
    }

    @Override
    public PunishmentInfo insertTempIPBan(UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration) {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
        data.put("type", PunishmentType.IPTEMPBAN.toString());
        data.put("uuid", uuid.toString());
        data.put("user", user);
        data.put("ip", ip);
        data.put("reason", reason);
        data.put("server", server);
        data.put("date", new Date());
        data.put("duration", duration);
        data.put("active", active);
        data.put("executed_by", executedby);
        data.put("removed", false);
        data.put("removed_by", null);

        db().getCollection(PunishmentType.IPTEMPBAN.getTable()).insertOne(new Document(data));
        return PunishmentDao.buildPunishmentInfo(PunishmentType.IPTEMPBAN, uuid, user, ip, reason, server, executedby, new Date(), duration, active, null);
    }

    @Override
    public PunishmentInfo getCurrentBan(UUID uuid) {
        final MongoCollection<Document> collection = db().getCollection(PunishmentType.BAN.getTable());
        final Document document = collection.find(Filters.and(
                Filters.eq("uuid", uuid.toString()),
                Filters.eq("active", true),
                Filters.regex("type", "^(?!IP.*$).*")
        )).first();

        if (document != null) {
            final PunishmentType type = Utils.valueOfOr(document.getString("type"), PunishmentType.BAN);

            final String user = document.getString("user");
            final String ip = document.getString("ip");
            final String reason = document.getString("reason");
            final String server = document.getString("server");
            final String executedby = document.getString("executed_by");
            final Date date = document.getDate("date");
            final Long time = document.getLong("duration");
            final boolean active = document.getBoolean("active");
            final String removedby = document.getString("removed_by");

            return PunishmentDao.buildPunishmentInfo(type, uuid, user, ip, reason, server, executedby, date, time, active, removedby);
        }

        return new PunishmentInfo();
    }

    @Override
    public PunishmentInfo getCurrentIPBan(String ip) {
        final MongoCollection<Document> collection = db().getCollection(PunishmentType.BAN.getTable());
        final Document document = collection.find(Filters.and(
                Filters.eq("ip", ip),
                Filters.eq("active", true),
                Filters.regex("type", "IP*")
        )).first();

        if (document != null) {
            final PunishmentType type = Utils.valueOfOr(document.getString("type"), PunishmentType.IPBAN);

            final UUID uuid = UUID.fromString(document.getString("uuid"));
            final String user = document.getString("user");
            final String reason = document.getString("reason");
            final String server = document.getString("server");
            final String executedby = document.getString("executed_by");
            final Date date = document.getDate("date");
            final Long time = document.getLong("duration");
            final boolean active = document.getBoolean("active");
            final String removedby = document.getString("removed_by");

            return PunishmentDao.buildPunishmentInfo(type, uuid, user, ip, reason, server, executedby, date, time, active, removedby);
        }

        return new PunishmentInfo();
    }

    @Override
    public void removeCurrentBan(UUID uuid, String removedBy) {
        final MongoCollection<Document> coll = db().getCollection(PunishmentType.BAN.getTable());

        // updateMany, this if for some reason multiple bans would be active at the same time.
        coll.updateMany(
                Filters.and(
                        Filters.eq("uuid", uuid.toString()),
                        Filters.eq("active", true),
                        Filters.regex("type", "^(?!IP.*$).*")
                ),
                Updates.combine(
                        Updates.set("active", false),
                        Updates.set("removed", true),
                        Updates.set("removed_by", removedBy)
                )
        );
    }

    @Override
    public void removeCurrentIPBan(String ip, String removedBy) {
        final MongoCollection<Document> coll = db().getCollection(PunishmentType.BAN.getTable());

        // updateMany, this if for some reason multiple bans would be active at the same time.
        coll.updateMany(
                Filters.and(
                        Filters.eq("ip", ip),
                        Filters.eq("active", true),
                        Filters.regex("type", "IP*")
                ),
                Updates.combine(
                        Updates.set("active", false),
                        Updates.set("removed", true),
                        Updates.set("removed_by", removedBy)
                )
        );
    }

    private MongoDatabase db() {
        return ((MongoDBStorageManager) BungeeUtilisals.getInstance().getDatabaseManagement()).getDatabase();
    }
}
