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
import be.dieterblancke.bungeeutilisalsx.common.api.language.Language;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.StorageType;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.UserDao;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class SqlUserDao implements UserDao
{

    @Override
    public CompletableFuture<Void> createUser( final UUID uuid,
                                               final String username,
                                               final String ip,
                                               final Language language,
                                               final String joinedHost )
    {
        final Date date = new Date( System.currentTimeMillis() );

        return createUser( uuid, username, ip, language, date, date, joinedHost );
    }

    @Override
    public CompletableFuture<Void> createUser( final UUID uuid,
                                               final String username,
                                               final String ip,
                                               final Language language,
                                               final Date login,
                                               final Date logout,
                                               final String joinedHost )
    {
        return CompletableFuture.runAsync( () ->
        {
            final StorageType type = BuX.getInstance().getAbstractStorageManager().getType();
            final String statement = type == StorageType.SQLITE || type == StorageType.POSTGRESQL
                    ? "INSERT INTO bu_users (uuid, username, ip, language, firstlogin, lastlogout, joined_host) VALUES (?, ?, ?, ?, " + Dao.getInsertDateParameter() + ", " + Dao.getInsertDateParameter() + ", ?) ON CONFLICT(uuid) DO UPDATE SET username = ?;"
                    : "INSERT INTO bu_users (uuid, username, ip, language, firstlogin, lastlogout, joined_host) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE username = ?;";

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement( statement ) )
            {
                pstmt.setString( 1, uuid.toString() );
                pstmt.setString( 2, username );
                pstmt.setString( 3, ip );
                pstmt.setString( 4, language.getName() );
                pstmt.setString( 5, Dao.formatDateToString( login ) );
                pstmt.setString( 6, Dao.formatDateToString( logout ) );
                pstmt.setString( 7, joinedHost );
                pstmt.setString( 8, username );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> updateUser( UUID uuid, String name, String ip, Language language, Date logout )
    {
        return CompletableFuture.runAsync( () ->
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "UPDATE bu_users SET username = ?, ip = ?, language = ?"
                                  + ( logout == null ? "" : ", lastlogout = " + Dao.getInsertDateParameter() )
                                  + " WHERE uuid = ?;"
                  ) )
            {
                pstmt.setString( 1, name );
                pstmt.setString( 2, ip );
                pstmt.setString( 3, language.getName() );
                if ( logout != null )
                {
                    pstmt.setString( 4, Dao.formatDateToString( logout ) );
                }
                pstmt.setString( logout == null ? 4 : 5, uuid.toString() );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Boolean> exists( final String name )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            boolean present = false;

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT id FROM bu_users WHERE username = ?;"
                  ) )
            {
                pstmt.setString( 1, name );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    present = rs.next();
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }

            return present;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Boolean> ipExists( String ip )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            boolean present = false;

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT id FROM bu_users WHERE ip = ?;"
                  ) )
            {
                pstmt.setString( 1, ip );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    present = rs.next();
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }

            return present;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<UserStorage> getUserData( final UUID uuid )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final UserStorage storage = new UserStorage();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement( "SELECT * FROM bu_users WHERE uuid = ?;" ) )
            {
                pstmt.setString( 1, uuid.toString() );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    if ( rs.next() )
                    {
                        loadUserStorageFromResultSet( connection, storage, rs );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }
            return storage;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<UserStorage> getUserData( final String name )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final UserStorage storage = new UserStorage();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement( "SELECT * FROM bu_users WHERE username = ? OR ip = ?;" ) )
            {
                pstmt.setString( 1, name );
                pstmt.setString( 2, name );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    if ( rs.next() )
                    {
                        loadUserStorageFromResultSet( connection, storage, rs );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }
            return storage;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<List<String>> getUsersOnIP( String ip )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final List<String> users = Lists.newArrayList();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT username FROM bu_users WHERE ip = ?;"
                  ) )
            {
                pstmt.setString( 1, ip );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    while ( rs.next() )
                    {
                        users.add( rs.getString( "username" ) );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }
            return users;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> setName( UUID uuid, String name )
    {
        return CompletableFuture.runAsync( () ->
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "UPDATE bu_users SET username = ? WHERE uuid = ?;"
                  ) )
            {
                pstmt.setString( 1, name );
                pstmt.setString( 2, uuid.toString() );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> setLanguage( UUID uuid, Language language )
    {
        return CompletableFuture.runAsync( () ->
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "UPDATE bu_users SET language = ? WHERE uuid = ?;"
                  ) )
            {
                pstmt.setString( 1, language.getName() );
                pstmt.setString( 2, uuid.toString() );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> setJoinedHost( UUID uuid, String joinedHost )
    {
        return CompletableFuture.runAsync( () ->
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "UPDATE bu_users SET joined_host = ? WHERE uuid = ?;"
                  ) )
            {
                pstmt.setString( 1, joinedHost );
                pstmt.setString( 2, uuid.toString() );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Map<String, Integer>> getJoinedHostList()
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final Map<String, Integer> map = Maps.newHashMap();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT joined_host, COUNT(*) amount FROM bu_users GROUP BY joined_host;"
                  ) )
            {
                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    while ( rs.next() )
                    {
                        final String joinedHost = rs.getString( "joined_host" );

                        if ( joinedHost != null )
                        {
                            map.put( joinedHost, rs.getInt( "amount" ) );
                        }
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }

            return map;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Map<String, Integer>> searchJoinedHosts( String searchTag )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            final Map<String, Integer> map = Maps.newHashMap();

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "SELECT joined_host, COUNT(*) amount FROM bu_users WHERE joined_host LIKE ? GROUP BY joined_host;"
                  ) )
            {
                pstmt.setString( 1, "%" + searchTag + "%" );
                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    while ( rs.next() )
                    {
                        final String joinedHost = rs.getString( "joined_host" );

                        if ( joinedHost != null )
                        {
                            map.put( joinedHost, rs.getInt( "amount" ) );
                        }
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }

            return map;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> ignoreUser( UUID user, UUID ignore )
    {
        return CompletableFuture.runAsync( () ->
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "INSERT INTO bu_ignoredusers(user, ignored) VALUES (?, ?);"
                  ) )
            {
                pstmt.setString( 1, user.toString() );
                pstmt.setString( 2, ignore.toString() );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<Void> unignoreUser( UUID user, UUID unignore )
    {
        return CompletableFuture.runAsync( () ->
        {
            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement(
                          "DELETE FROM bu_ignoredusers WHERE user = ? AND ignored = ?;"
                  ) )
            {
                pstmt.setString( 1, user.toString() );
                pstmt.setString( 2, unignore.toString() );

                pstmt.executeUpdate();
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    @Override
    public CompletableFuture<UUID> getUuidFromName( final String targetName )
    {
        return CompletableFuture.supplyAsync( () ->
        {
            UUID uuid = null;

            try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
                  PreparedStatement pstmt = connection.prepareStatement( "SELECT uuid FROM bu_users WHERE username = ?;" ) )
            {
                pstmt.setString( 1, targetName );

                try ( ResultSet rs = pstmt.executeQuery() )
                {
                    if ( rs.next() )
                    {
                        uuid = UUID.fromString( rs.getString( "uuid" ) );
                    }
                }
            }
            catch ( SQLException e )
            {
                BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
            }
            return uuid;
        }, BuX.getInstance().getScheduler().getExecutorService() );
    }

    private void loadUserStorageFromResultSet( final Connection connection,
                                               final UserStorage storage,
                                               final ResultSet rs ) throws SQLException
    {
        storage.setUuid( UUID.fromString( rs.getString( "uuid" ) ) );
        storage.setUserName( rs.getString( "username" ) );
        storage.setIp( rs.getString( "ip" ) );
        storage.setLanguage( BuX.getApi().getLanguageManager().getLangOrDefault( rs.getString( "language" ) ) );
        storage.setFirstLogin( Dao.formatStringToDate( rs.getString( "firstlogin" ) ) );
        storage.setLastLogout( Dao.formatStringToDate( rs.getString( "lastlogout" ) ) );
        storage.setJoinedHost( rs.getString( "joined_host" ) );
        storage.setIgnoredUsers( loadIgnoredUsers( connection, storage.getUuid() ) );
    }

    private List<String> loadIgnoredUsers( final Connection connection, final UUID user ) throws SQLException
    {
        final List<String> ignoredUsers = Lists.newArrayList();
        try ( PreparedStatement pstmt = connection.prepareStatement(
                "SELECT username FROM bu_ignoredusers iu LEFT JOIN bu_users u ON iu.ignored = u.uuid WHERE user = ?;"
        ) )
        {
            pstmt.setString( 1, user.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                while ( rs.next() )
                {
                    ignoredUsers.add( rs.getString( "username" ) );
                }
            }
        }
        return ignoredUsers;
    }
}
