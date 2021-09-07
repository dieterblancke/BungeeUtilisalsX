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
import be.dieterblancke.bungeeutilisalsx.common.storage.data.sql.dao.*;
import lombok.Getter;

@Getter
public class SQLDao implements Dao
{

    private final UserDao userDao;
    private final PunishmentDao punishmentDao;
    private final FriendsDao friendsDao;
    private final ReportsDao reportsDao;
    private final OfflineMessageDao offlineMessageDao;
    private final ApiTokenDao apiTokenDao;

    public SQLDao()
    {
        this.userDao = new SqlUserDao();
        this.punishmentDao = new SqlPunishmentDao();
        this.friendsDao = new SqlFriendsDao();
        this.reportsDao = new SqlReportsDao();
        this.offlineMessageDao = new SqlOfflineMessageDao();
        this.apiTokenDao = new SqlApiTokenDao();
    }
}
