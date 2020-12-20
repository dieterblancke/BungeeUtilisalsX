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

package be.dieterblancke.bungeeutilisalsx.common.storage.data.sql;

import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.*;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.QueuedMessage;
import be.dieterblancke.bungeeutilisalsx.common.storage.data.sql.dao.*;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.*;
import be.dieterblancke.bungeeutilisalsx.common.storage.data.sql.dao.*;
import lombok.Getter;

import java.util.UUID;

@Getter
public class SQLDao implements Dao
{

    private final UserDao userDao;
    private final PunishmentDao punishmentDao;
    private final FriendsDao friendsDao;
    private final ReportsDao reportsDao;

    public SQLDao()
    {
        this.userDao = new SQLUserDao();
        this.punishmentDao = new SQLPunishmentDao();
        this.friendsDao = new SQLFriendsDao();
        this.reportsDao = new SQLReportsDao();
    }

    @Override
    public MessageQueue<QueuedMessage> createMessageQueue( final UUID uuid, final String name, final String ip )
    {
        return new SQLMessageQueue( uuid, name, ip );
    }

    @Override
    public MessageQueue<QueuedMessage> createMessageQueue()
    {
        return new SQLMessageQueue();
    }
}
