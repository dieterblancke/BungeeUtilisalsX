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
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.BansDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.KickAndWarnDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.MutesDao;
import com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao.punishment.MongoBansDao;
import com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao.punishment.MongoKickAndWarnDao;
import com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao.punishment.MongoMutesDao;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.Date;
import java.util.UUID;

public class MongoPunishmentDao implements PunishmentDao {

    private final BansDao bansDao;
    private final MutesDao mutesDao;
    private final KickAndWarnDao kickAndWarnDao;

    public MongoPunishmentDao() {
        this.bansDao = new MongoBansDao();
        this.mutesDao = new MongoMutesDao();
        this.kickAndWarnDao = new MongoKickAndWarnDao();
    }

    @Override
    public BansDao getBansDao() {
        return bansDao;
    }

    @Override
    public MutesDao getMutesDao() {
        return mutesDao;
    }

    @Override
    public KickAndWarnDao getKickAndWarnDao() {
        return kickAndWarnDao;
    }

    @Override
    public long getPunishmentsSince(PunishmentType type, UUID uuid, Date date) {
        final MongoCollection<Document> collection = db().getCollection(type.getTable());

        if (type.isActivatable()) {
            return collection.countDocuments(Filters.and(
                    Filters.eq("uuid", uuid.toString()),
                    Filters.gte("date", date),
                    Filters.eq("type", type.toString())
            ));
        } else {
            return collection.countDocuments(Filters.and(
                    Filters.eq("uuid", uuid),
                    Filters.gte("date", date)
            ));
        }
    }

    @Override
    public long getIPPunishmentsSince(PunishmentType type, String ip, Date date) {
        final MongoCollection<Document> collection = db().getCollection(type.getTable());

        return collection.countDocuments(Filters.and(
                Filters.eq("ip", ip),
                Filters.gte("date", date),
                Filters.eq("type", type.toString())
        ));
    }

    private MongoDatabase db() {
        return ((MongoDBStorageManager) BungeeUtilisals.getInstance().getDatabaseManagement()).getDatabase();
    }
}
