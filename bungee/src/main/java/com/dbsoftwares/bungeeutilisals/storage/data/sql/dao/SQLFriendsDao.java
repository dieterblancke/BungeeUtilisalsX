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
import com.dbsoftwares.bungeeutilisals.api.friends.FriendSettingType;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendSettings;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.FriendsDao;
import com.google.common.collect.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class SQLFriendsDao implements FriendsDao
{

    @Override
    public void addFriend( UUID user, UUID uuid )
    {
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "INSERT INTO {friends-table}(user, friend) VALUES(?, ?);" )
              ) )
        {
            pstmt.setString( 1, user.toString() );
            pstmt.setString( 2, uuid.toString() );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    @Override
    public void removeFriend( UUID user, UUID uuid )
    {
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "DELETE FROM {friends-table} WHERE user = ? AND friend = ?;" )
              ) )
        {
            pstmt.setString( 1, user.toString() );
            pstmt.setString( 2, uuid.toString() );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    @Override
    public List<FriendData> getFriends( UUID uuid )
    {
        final List<FriendData> friends = Lists.newArrayList();

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT created, user, friend, u.username friendname, u.lastlogout lastlogout FROM {friends-table}" +
                              " JOIN {users-table} u ON friend = u.uuid" +
                              " WHERE user = ?;" )
              ) )
        {
            pstmt.setString( 1, uuid.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                while ( rs.next() )
                {
                    friends.add( new FriendData(
                            UUID.fromString( rs.getString( "friend" ) ),
                            rs.getString( "friendname" ),
                            Dao.formatStringToDate( rs.getString( "created" ) ),
                            Dao.formatStringToDate( rs.getString( "lastlogout" ) )
                    ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        return friends;
    }

    @Override
    public long getAmountOfFriends( UUID uuid )
    {
        long count = 0;

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT COUNT(friend) FROM {friends-table} WHERE user = ?;" )
              ) )
        {
            pstmt.setString( 1, uuid.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    count = rs.getInt( 1 );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        return count;
    }

    @Override
    public void addFriendRequest( UUID user, UUID uuid )
    {
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "INSERT INTO {friendrequests-table}(user, friend) VALUES(?, ?);" )
              ) )
        {
            pstmt.setString( 1, user.toString() );
            pstmt.setString( 2, uuid.toString() );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    @Override
    public void removeFriendRequest( UUID user, UUID uuid )
    {
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "DELETE FROM {friendrequests-table} WHERE user = ? AND friend = ?;" )
              ) )
        {
            pstmt.setString( 1, uuid.toString() );
            pstmt.setString( 2, user.toString() );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    @Override
    public List<FriendRequest> getIncomingFriendRequests( UUID uuid )
    {
        final List<FriendRequest> friendRequests = Lists.newArrayList();

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT user, username, friend, requested_at FROM {friendrequests-table}"
                              + " JOIN {users-table} ON user = uuid"
                              + " WHERE friend = ?;" )
              ) )
        {
            pstmt.setString( 1, uuid.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                while ( rs.next() )
                {
                    friendRequests.add( new FriendRequest(
                            uuid,
                            rs.getString( "username" ),
                            UUID.fromString( rs.getString( "friend" ) ),
                            null,
                            Dao.formatStringToDate( rs.getString( "requested_at" ) )
                    ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        return friendRequests;
    }

    @Override
    public List<FriendRequest> getOutgoingFriendRequests( UUID uuid )
    {
        final List<FriendRequest> friendRequests = Lists.newArrayList();

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT user, friend, username, requested_at FROM {friendrequests-table}"
                              + " JOIN {users-table} ON user = uuid"
                              + " WHERE user = ?;" )
              ) )
        {
            pstmt.setString( 1, uuid.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                while ( rs.next() )
                {
                    friendRequests.add( new FriendRequest(
                            uuid,
                            null,
                            UUID.fromString( rs.getString( "friend" ) ),
                            rs.getString( "username" ),
                            Dao.formatStringToDate( rs.getString( "requested_at" ) )
                    ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        return friendRequests;
    }

    @Override
    public boolean hasIncomingFriendRequest( UUID user, UUID uuid )
    {
        boolean found = false;

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT EXISTS(SELECT * FROM {friendrequests-table} WHERE user = ? AND friend = ?);" )
              ) )
        {
            pstmt.setString( 1, uuid.toString() );
            pstmt.setString( 2, user.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    found = rs.getBoolean( 1 );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        return found;
    }

    @Override
    public boolean hasOutgoingFriendRequest( UUID user, UUID uuid )
    {
        boolean found = false;

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT EXISTS(SELECT * FROM {friendrequests-table} WHERE user = ? AND friend = ?);" )
              ) )
        {
            pstmt.setString( 1, user.toString() );
            pstmt.setString( 2, uuid.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    found = rs.getBoolean( 1 );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        return found;
    }

    @Override
    public void setSetting( UUID uuid, FriendSettingType type, boolean value )
    {
        // not really the cleanest code, but should be fine
        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT COUNT(user) FROM {friendsettings-table} WHERE user = ?" )
              ) )
        {
            pstmt.setString( 1, uuid.toString() );
            boolean exists = false;

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    exists = rs.getInt( 1 ) > 0;
                }
            }

            if ( exists )
            {
                try ( PreparedStatement updatePstmt = connection.prepareStatement(
                        format( "UPDATE {friendsettings-table} SET " + type.toString().toLowerCase() + " = ? WHERE user = ?;" )
                ) )
                {
                    updatePstmt.setBoolean( 1, value );
                    updatePstmt.setString( 2, uuid.toString() );

                    updatePstmt.executeUpdate();
                }
            }
            else
            {
                try ( PreparedStatement insertPstmt = connection.prepareStatement(
                        format( "INSERT INTO {friendsettings-table} (user, requests, messages) VALUES (?, ?, ?);" )
                ) )
                {
                    insertPstmt.setString( 1, uuid.toString() );
                    insertPstmt.setBoolean( 2, getValue( FriendSettingType.REQUESTS, type, value ) );
                    insertPstmt.setBoolean( 3, getValue( FriendSettingType.MESSAGES, type, value ) );

                    insertPstmt.executeUpdate();
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    private boolean getValue( final FriendSettingType setting, final FriendSettingType type, final boolean value )
    {
        return setting.equals( type ) ? value : setting.getDefault();
    }

    @Override
    public boolean getSetting( UUID uuid, FriendSettingType type )
    {
        boolean setting = type.getDefault();

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT " + type.toString().toLowerCase() + " FROM {friendsettings-table} WHERE user = ? LIMIT 1;" )
              ) )
        {
            pstmt.setString( 1, uuid.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    setting = rs.getBoolean( type.toString().toLowerCase() );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        return setting;
    }

    @Override
    public FriendSettings getSettings( UUID uuid )
    {
        FriendSettings settings = null;

        try ( Connection connection = BUCore.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT * FROM {friendsettings-table} WHERE user = ? LIMIT 1;" )
              ) )
        {
            pstmt.setString( 1, uuid.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    settings = new FriendSettings(
                            rs.getBoolean( "requests" ),
                            rs.getBoolean( "messages" )
                    );
                }
            }
        }
        catch ( SQLException e )
        {
            BUCore.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        return settings == null ? new FriendSettings() : settings;
    }

    private String format( String line )
    {
        return PlaceHolderAPI.formatMessage( line );
    }

    private String format( String line, Object... replacements )
    {
        return PlaceHolderAPI.formatMessage( String.format( line, replacements ) );
    }
}
