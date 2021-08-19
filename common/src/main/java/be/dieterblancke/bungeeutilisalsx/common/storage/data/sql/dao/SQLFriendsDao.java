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

package be.dieterblancke.bungeeutilisalsx.common.storage.data.sql.dao;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequest;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSetting;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSettings;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.FriendsDao;
import com.google.common.collect.Lists;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;

public class SQLFriendsDao implements FriendsDao
{

    @Override
    public void addFriend( UUID user, UUID uuid )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "INSERT INTO bu_friends(user, friend, created) VALUES(?, ?, " + Dao.getInsertDateParameter() + ");"
              ) )
        {
            pstmt.setString( 1, user.toString() );
            pstmt.setString( 2, uuid.toString() );
            pstmt.setString( 3, Dao.formatDateToString( new Date() ) );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    @Override
    public void removeFriend( UUID user, UUID uuid )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "DELETE FROM bu_friends WHERE user = ? AND friend = ?;"
              ) )
        {
            pstmt.setString( 1, user.toString() );
            pstmt.setString( 2, uuid.toString() );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    @Override
    public List<FriendData> getFriends( UUID uuid )
    {
        final List<FriendData> friends = Lists.newArrayList();

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT created, user, friend, u.username friendname, u.lastlogout lastlogout FROM bu_friends" +
                              " JOIN bu_users u ON friend = u.uuid" +
                              " WHERE user = ?;"
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
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        return friends;
    }

    @Override
    public long getAmountOfFriends( UUID uuid )
    {
        long count = 0;

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT COUNT(friend) FROM bu_friends WHERE user = ?;"
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
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        return count;
    }

    @Override
    public void addFriendRequest( UUID user, UUID uuid )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "INSERT INTO bu_friendrequests(user, friend, requested_at) VALUES(?, ?, " + Dao.getInsertDateParameter() + ");"
              ) )
        {
            pstmt.setString( 1, user.toString() );
            pstmt.setString( 2, uuid.toString() );
            pstmt.setString( 3, Dao.formatDateToString( new Date() ) );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    @Override
    public void removeFriendRequest( UUID user, UUID uuid )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "DELETE FROM bu_friendrequests WHERE user = ? AND friend = ?;"
              ) )
        {
            pstmt.setString( 1, uuid.toString() );
            pstmt.setString( 2, user.toString() );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    @Override
    public List<FriendRequest> getIncomingFriendRequests( UUID uuid )
    {
        final List<FriendRequest> friendRequests = Lists.newArrayList();

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT user, username, friend, requested_at FROM bu_friendrequests"
                              + " JOIN bu_users ON user = uuid"
                              + " WHERE friend = ?;"
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
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        return friendRequests;
    }

    @Override
    public List<FriendRequest> getOutgoingFriendRequests( UUID uuid )
    {
        final List<FriendRequest> friendRequests = Lists.newArrayList();

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT user, friend, username, requested_at FROM bu_friendrequests"
                              + " JOIN bu_users ON friend = uuid"
                              + " WHERE user = ?;"
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
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        return friendRequests;
    }

    @Override
    public boolean hasIncomingFriendRequest( UUID user, UUID uuid )
    {
        boolean found = false;

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT EXISTS(SELECT * FROM bu_friendrequests WHERE user = ? AND friend = ?);"
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
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        return found;
    }

    @Override
    public boolean hasOutgoingFriendRequest( UUID user, UUID uuid )
    {
        boolean found = false;

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT EXISTS(SELECT * FROM bu_friendrequests WHERE user = ? AND friend = ?);"
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
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        return found;
    }

    @Override
    public void setSetting( UUID uuid, FriendSetting type, boolean value )
    {
        // not really the cleanest code, but should be fine
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT COUNT(user) FROM bu_friendsettings WHERE user = ? AND setting = ?"
              ) )
        {
            pstmt.setString( 1, uuid.toString() );
            pstmt.setString( 2, type.toString() );
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
                        "UPDATE bu_friendsettings SET value = ? WHERE user = ? and setting = ?;"
                ) )
                {
                    updatePstmt.setBoolean( 1, value );
                    updatePstmt.setString( 2, uuid.toString() );
                    updatePstmt.setString( 3, type.toString() );

                    updatePstmt.executeUpdate();
                }
            }
            else
            {
                try ( PreparedStatement insertPstmt = connection.prepareStatement(
                        "INSERT INTO bu_friendsettings (user, setting, value) VALUES (?, ?, ?);"
                ) )
                {
                    insertPstmt.setString( 1, uuid.toString() );
                    insertPstmt.setString( 2, type.toString() );
                    insertPstmt.setObject( 3, value );

                    insertPstmt.executeUpdate();
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    @Override
    public boolean getSetting( final UUID uuid, final FriendSetting type )
    {
        boolean setting = type.getDefault();

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT value FROM bu_friendsettings WHERE user = ? AND setting = ? LIMIT 1;"
              ) )
        {
            pstmt.setString( 1, uuid.toString() );
            pstmt.setString( 2, type.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    setting = rs.getBoolean( "value" );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        return setting;
    }

    @Override
    public FriendSettings getSettings( UUID uuid )
    {
        final FriendSettings settings = new FriendSettings();

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT * FROM bu_friendsettings WHERE user = ? LIMIT 1;"
              ) )
        {
            pstmt.setString( 1, uuid.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                while ( rs.next() )
                {
                    settings.set(
                            FriendSetting.valueOf( rs.getString("setting").toUpperCase() ),
                            rs.getBoolean( "value" )
                    );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }

        return settings;
    }
}
