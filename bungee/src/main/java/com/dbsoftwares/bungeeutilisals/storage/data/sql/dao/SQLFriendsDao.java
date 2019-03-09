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
import com.dbsoftwares.bungeeutilisals.api.friends.FriendRequest;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.FriendsDao;
import com.google.common.collect.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public class SQLFriendsDao implements FriendsDao {

    private static final String ERROR_STRING = "An error occured: ";

    @Override
    public void addFriend(UUID user, UUID uuid) {
        try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     format("INSERT INTO {friends-table}(user, friend) VALUES(?, ?);")
             )) {
            pstmt.setString(1, user.toString());
            pstmt.setString(2, uuid.toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
    }

    @Override
    public void removeFriend(UUID user, UUID uuid) {
        try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     format("DELETE FROM {friends-table} WHERE userid = ? AND friendid = ?;")
             )) {
            pstmt.setString(1, user.toString());
            pstmt.setString(2, uuid.toString());

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
                     format("SELECT created, user, friend, u.username friendname, u.lastlogout lastlogout FROM {friends-table}" +
                             " JOIN {users-table} u ON friend = u.uuid" +
                             " WHERE user = ?;")
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

    @Override
    public long getAmountOfFriends(UUID uuid) {
        long count = 0;

        try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     format("SELECT COUNT(friend) FROM {friends-table} WHERE user = ?;")
             )) {
            pstmt.setString(1, uuid.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }

        return count;
    }

    @Override
    public void addFriendRequest(UUID user, UUID uuid) {
        try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     format("INSERT INTO {friendrequests-table}(user, friend) VALUES(?, ?);")
             )) {
            pstmt.setString(1, user.toString());
            pstmt.setString(2, uuid.toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
    }

    @Override
    public void removeFriendRequest(UUID user, UUID uuid) {
        try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     format("DELETE FROM {friendrequests-table} WHERE user = ? AND friend = ?;")
             )) {
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, user.toString());

            pstmt.executeUpdate();
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }
    }

    @Override
    public List<FriendRequest> getIncomingFriendRequests(UUID uuid) {
        final List<FriendRequest> friendRequests = Lists.newArrayList();

        try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     format("SELECT * FROM {friendrequests-table} WHERE friend = ?;")
             )) {
            pstmt.setString(1, uuid.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    friendRequests.add(new FriendRequest(
                            uuid,
                            UUID.fromString(rs.getString("friend")),
                            rs.getTimestamp("requested_at")
                    ));
                }
            }
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }

        return friendRequests;
    }

    @Override
    public List<FriendRequest> getOutgoingFriendRequests(UUID uuid) {
        final List<FriendRequest> friendRequests = Lists.newArrayList();

        try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     format("SELECT * FROM {friendrequests-table} WHERE user = ?;")
             )) {
            pstmt.setString(1, uuid.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    friendRequests.add(new FriendRequest(
                            uuid,
                            UUID.fromString(rs.getString("friend")),
                            rs.getTimestamp("requested_at")
                    ));
                }
            }
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }

        return friendRequests;
    }

    @Override
    public boolean hasIncomingFriendRequest(UUID user, UUID uuid) {
        boolean found = false;

        try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     format("SELECT EXISTS(SELECT * FROM {friendrequests-table} WHERE user = ? AND friend = ?);")
             )) {
            pstmt.setString(1, uuid.toString());
            pstmt.setString(2, user.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    found = rs.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }

        return found;
    }

    @Override
    public boolean hasOutgoingFriendRequest(UUID user, UUID uuid) {
        boolean found = false;

        try (Connection connection = BUCore.getApi().getStorageManager().getConnection();
             PreparedStatement pstmt = connection.prepareStatement(
                     format("SELECT EXISTS(SELECT * FROM {friendrequests-table} WHERE user = ? AND friend = ?);")
             )) {
            pstmt.setString(1, user.toString());
            pstmt.setString(2, uuid.toString());

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    found = rs.getBoolean(1);
                }
            }
        } catch (SQLException e) {
            BUCore.getLogger().error(ERROR_STRING, e);
        }

        return found;
    }

    private String format(String line) {
        return PlaceHolderAPI.formatMessage(line);
    }

    private String format(String line, Object... replacements) {
        return PlaceHolderAPI.formatMessage(String.format(line, replacements));
    }
}
