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

package com.dbsoftwares.bungeeutilisals.storage.data.sql.dao;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.FriendsDao;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.google.common.collect.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class SQLFriendsDao implements FriendsDao {

    private static final String ERROR_STRING = "An error occured: ";
    private final String SELECT_ID = "(SELECT id FROM {users-table} WHERE uuid = ?)";

    @Override
    public void addFriend(User user, UUID identifier) {
        try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     format("INSERT INTO {friends-table}(userid, friendid) VALUES(%s, %s);", SELECT_ID, SELECT_ID)
             )) {
            pstmt.setString(1, user.getUUID().toString());
            pstmt.setString(2, identifier.toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
    }

    @Override
    public void removeFriend(User user, UUID identifier) {
        try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     format("DELETE FROM {friends-table} WHERE userid = %s AND friendid = %s;", SELECT_ID, SELECT_ID)
             )) {
            pstmt.setString(1, user.getUUID().toString());
            pstmt.setString(2, identifier.toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
    }

    @Override
    public List<FriendData> getFriends(UUID uuid) {
        final List<FriendData> friends = Lists.newArrayList();

        try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     format("SELECT friendsince, u.username friendname, u.lastlogout lastlogout FROM {friends-table}" +
                             " JOIN {users-table} u ON friendid = u.id" +
                             " WHERE userid = %s;", SELECT_ID)
             )) {
            pstmt.setString(1, uuid.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    friends.add(new FriendData(
                            uuid,
                            rs.getString("friendname"),
                            rs.getDate("friendsince"),
                            rs.getDate("lastlogout")
                    ));
                }
            }
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }

        return friends;
    }

    private String format(String line) {
        return PlaceHolderAPI.formatMessage(line);
    }

    private String format(String line, Object... replacements) {
        return PlaceHolderAPI.formatMessage(String.format(line, replacements));
    }
}
