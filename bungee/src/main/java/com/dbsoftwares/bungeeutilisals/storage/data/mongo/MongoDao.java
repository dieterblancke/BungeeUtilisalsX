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
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.other.QueuedMessage;
import com.dbsoftwares.bungeeutilisals.storage.data.mongo.dao.*;
import lombok.Getter;

@Getter
public class MongoDao implements Dao
{

    private UserDao userDao;
    private PunishmentDao punishmentDao;
    private FriendsDao friendsDao;
    private ReportsDao reportsDao;

    public MongoDao()
    {
        this.userDao = new MongoUserDao();
        this.punishmentDao = new MongoPunishmentDao();
        this.friendsDao = new MongoFriendsDao();
        this.reportsDao = new MongoReportsDao();
    }

    @Override
    public MessageQueue<QueuedMessage> createMessageQueue( User user )
    {
        return new MongoMessageQueue( user.getUuid(), user.getName(), user.getIp() );
    }

    @Override
    public MessageQueue<QueuedMessage> createMessageQueue()
    {
        return new MongoMessageQueue();
    }
}
