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

package com.dbsoftwares.bungeeutilisals.api.storage.dao;

import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.other.QueuedMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface Dao {

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static String formatDateToString(final Date date) {
        return format.format(date);
    }

    static Date formatStringToDate(final String date) {
        try {
            return format.parse(date);
        } catch (ParseException e) {
            return new Date();
        }
    }

    UserDao getUserDao();

    PunishmentDao getPunishmentDao();

    FriendsDao getFriendsDao();

    ReportsDao getReportsDao();

    MessageQueue<QueuedMessage> createMessageQueue(final User user);

    MessageQueue<QueuedMessage> createMessageQueue();
}
