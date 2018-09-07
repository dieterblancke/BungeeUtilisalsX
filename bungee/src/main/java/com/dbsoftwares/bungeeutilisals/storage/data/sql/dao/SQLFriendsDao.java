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

/*
 * Created by DBSoftwares on 30 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class SQLFriendsDao implements FriendsDao {

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
            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
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
