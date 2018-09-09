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

package com.dbsoftwares.bungeeutilisals.storage.data.sql;

import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.FriendsDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.PunishmentDao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.UserDao;
import com.dbsoftwares.bungeeutilisals.storage.data.sql.dao.SQLFriendsDao;
import com.dbsoftwares.bungeeutilisals.storage.data.sql.dao.SQLPunishmentDao;
import com.dbsoftwares.bungeeutilisals.storage.data.sql.dao.SQLUserDao;

public class SQLDao implements Dao {

    private UserDao userDao;
    private PunishmentDao punishmentDao;
    private FriendsDao friendsDao;

    public SQLDao() {
        this.userDao = new SQLUserDao();
        this.punishmentDao = new SQLPunishmentDao();
        this.friendsDao = new SQLFriendsDao();
    }

    @Override
    public UserDao getUserDao() {
        return userDao;
    }

    @Override
    public PunishmentDao getPunishmentDao() {
        return punishmentDao;
    }

    @Override
    public FriendsDao getFriendsDao() {
        return friendsDao;
    }
}
