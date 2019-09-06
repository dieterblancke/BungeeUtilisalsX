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

package com.dbsoftwares.bungeeutilisals.storage.data.mongo;

import com.dbsoftwares.bungeeutilisals.api.storage.dao.*;
import com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao.MongoFriendsDao;
import com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao.MongoPunishmentDao;
import com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao.MongoReportsDao;
import com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao.MongoUserDao;
import lombok.Getter;

@Getter
public class MongoDao implements Dao {

    private UserDao userDao;
    private PunishmentDao punishmentDao;
    private FriendsDao friendsDao;
    private ReportsDao reportsDao;

    public MongoDao() {
        this.userDao = new MongoUserDao();
        this.punishmentDao = new MongoPunishmentDao();
        this.friendsDao = new MongoFriendsDao();
        this.reportsDao = new MongoReportsDao();
    }
}
