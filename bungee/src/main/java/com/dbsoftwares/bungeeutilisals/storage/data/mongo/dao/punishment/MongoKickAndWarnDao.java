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
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.KickAndWarnDao;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;

public class MongoKickAndWarnDao implements KickAndWarnDao {

    @Override
    public PunishmentInfo insertWarn(UUID uuid, String user, String ip, String reason, String server, String executedby) {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
        data.put("type", PunishmentType.WARN.toString());
        data.put("uuid", uuid.toString());
        data.put("user", user);
        data.put("ip", ip);
        data.put("reason", reason);
        data.put("server", server);
        data.put("date", new Date());
        data.put("executed_by", executedby);

        db().getCollection(PunishmentType.WARN.getTable()).insertOne(new Document(data));
        return PunishmentDao.buildPunishmentInfo(PunishmentType.WARN, uuid, user, ip, reason, server, executedby, new Date(), -1, true, null);
    }

    @Override
    public PunishmentInfo insertKick(UUID uuid, String user, String ip, String reason, String server, String executedby) {
        final LinkedHashMap<String, Object> data = Maps.newLinkedHashMap();
        data.put("type", PunishmentType.KICK.toString());
        data.put("uuid", uuid.toString());
        data.put("user", user);
        data.put("ip", ip);
        data.put("reason", reason);
        data.put("server", server);
        data.put("date", new Date());
        data.put("executed_by", executedby);

        db().getCollection(PunishmentType.KICK.getTable()).insertOne(new Document(data));
        return PunishmentDao.buildPunishmentInfo(PunishmentType.KICK, uuid, user, ip, reason, server, executedby, new Date(), -1, true, null);
    }

    private MongoDatabase db() {
        return ((MongoDBStorageManager) BungeeUtilisals.getInstance().getDatabaseManagement()).getDatabase();
    }
}
