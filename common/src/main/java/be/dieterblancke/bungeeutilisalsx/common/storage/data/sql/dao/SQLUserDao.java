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
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
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
import java.util.logging.Level;

public class SQLUserDao implements UserDao
{

    private static final String SELECT_USER = "SELECT %s FROM {users-table} WHERE %s;";
    private static final String UPDATE_USER_COLUMN = "UPDATE {users-table} SET %s = ? WHERE uuid = ?;";

    @Override
    public void createUser( UUID uuid, String username, String ip, Language language, String joinedHost )
    {
        Date date = new Date( System.currentTimeMillis() );

        createUser( uuid, username, ip, language, date, date, joinedHost );
    }

    @Override
    public void createUser( UUID uuid, String username, String ip, Language language, Date login, Date logout, String joinedHost )
    {
        final StorageType type = BuX.getInstance().getAbstractStorageManager().getType();
        final String statement = type == StorageType.SQLITE || type == StorageType.POSTGRESQL
                ? "INSERT INTO {users-table} (uuid, username, ip, language, firstlogin, lastlogout, joined_host) VALUES (?, ?, ?, ?, " + Dao.getInsertDateParameter() + ", " + Dao.getInsertDateParameter() + ", ?) ON CONFLICT(uuid) DO UPDATE SET username = ?;"
                : "INSERT INTO {users-table} (uuid, username, ip, language, firstlogin, lastlogout, joined_host) VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE username = ?;";

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( format( statement ) ) )
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
    }

    @Override
    public void updateUser( UUID uuid, String name, String ip, Language language, Date logout )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( format(
                      "UPDATE {users-table} SET username = ?, ip = ?, language = ?, lastlogout = " + Dao.getInsertDateParameter() + " WHERE uuid = ?;"
              ) ) )
        {
            pstmt.setString( 1, name );
            pstmt.setString( 2, ip );
            pstmt.setString( 3, language.getName() );
            pstmt.setString( 4, Dao.formatDateToString( logout ) );
            pstmt.setString( 5, uuid.toString() );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    @Override
    public boolean exists( String name )
    {
        boolean present = false;
        String statement = format( SELECT_USER, "id", name.contains( "." ) ? "ip = ?" : "username = ?" );

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( statement ) )
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
    }

    @Override
    public boolean exists( UUID uuid )
    {
        boolean present = false;
        String statement = format( SELECT_USER, "id", "uuid = ?" );

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( statement ) )
        {
            pstmt.setString( 1, uuid.toString() );

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
    }

    @Override
    public boolean ipExists( String ip )
    {
        boolean present = false;
        String statement = format( SELECT_USER, "id", "ip = ?" );

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( statement ) )
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
    }

    @Override
    public UserStorage getUserData( UUID uuid )
    {
        final UserStorage storage = new UserStorage();
        final String statement = format( SELECT_USER, "*", "uuid = ?" );

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( statement ) )
        {
            pstmt.setString( 1, uuid.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    storage.setUuid( uuid );
                    storage.setUserName( rs.getString( "username" ) );
                    storage.setIp( rs.getString( "ip" ) );
                    storage.setLanguage( BuX.getApi().getLanguageManager().getLangOrDefault( rs.getString( "language" ) ) );
                    storage.setFirstLogin(
                            Dao.formatStringToDate( rs.getString( "firstlogin" ) )
                    );
                    storage.setLastLogout(
                            Dao.formatStringToDate( rs.getString( "lastlogout" ) )
                    );
                    storage.setJoinedHost( rs.getString( "joined_host" ) );

                    storage.setIgnoredUsers( loadIgnoredUsers( connection, storage.getUuid() ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
        return storage;
    }

    @Override
    public UserStorage getUserData( String name )
    {
        UserStorage storage = new UserStorage();
        String statement = format( SELECT_USER, "*", "username = ?" );

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( statement ) )
        {
            pstmt.setString( 1, name );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    storage.setUuid( UUID.fromString( rs.getString( "uuid" ) ) );
                    storage.setUserName( name );
                    storage.setIp( rs.getString( "ip" ) );
                    storage.setLanguage( BuX.getApi().getLanguageManager().getLangOrDefault( rs.getString( "language" ) ) );
                    storage.setFirstLogin(
                            Dao.formatStringToDate( rs.getString( "firstlogin" ) )
                    );
                    storage.setLastLogout(
                            Dao.formatStringToDate( rs.getString( "lastlogout" ) )
                    );
                    storage.setJoinedHost( rs.getString( "joined_host" ) );

                    storage.setIgnoredUsers( loadIgnoredUsers( connection, storage.getUuid() ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
        return storage;
    }

    private List<String> loadIgnoredUsers( final Connection connection, final UUID user ) throws SQLException
    {
        final List<String> ignoredUsers = Lists.newArrayList();
        try ( PreparedStatement pstmt = connection.prepareStatement(
                format( "SELECT username FROM {ignoredusers-table} iu LEFT JOIN {users-table} u ON iu.ignored = u.uuid WHERE user = ?;" )
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

    @Override
    public List<String> getUsersOnIP( String ip )
    {
        List<String> users = Lists.newArrayList();
        String statement = format( SELECT_USER, "username", "ip = ?" );

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( statement ) )
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
    }

    @Override
    public Language getLanguage( UUID uuid )
    {
        Language language = null;
        String statement = format( SELECT_USER, "language", "uuid = ?" );

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( statement ) )
        {
            pstmt.setString( 1, uuid.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    language = BuX.getApi().getLanguageManager().getLangOrDefault( rs.getString( "language" ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
        return language;
    }

    @Override
    public void setName( UUID uuid, String name )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( format( UPDATE_USER_COLUMN, "username" ) ) )
        {
            pstmt.setString( 1, name );
            pstmt.setString( 2, uuid.toString() );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    @Override
    public void setIP( UUID uuid, String ip )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( format( UPDATE_USER_COLUMN, "ip" ) ) )
        {
            pstmt.setString( 1, ip );
            pstmt.setString( 2, uuid.toString() );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    @Override
    public void setLanguage( UUID uuid, Language language )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( format( UPDATE_USER_COLUMN, "language" ) ) )
        {
            pstmt.setString( 1, language.getName() );
            pstmt.setString( 2, uuid.toString() );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    @Override
    public void setLogout( UUID uuid, Date logout )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "UPDATE {users-table} SET lastlogout = " + Dao.getInsertDateParameter() + " WHERE uuid = ?;" )
              ) )
        {
            pstmt.setString( 1, Dao.formatDateToString( logout ) );
            pstmt.setString( 2, uuid.toString() );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    @Override
    public void setJoinedHost( UUID uuid, String joinedHost )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( format( UPDATE_USER_COLUMN, "joined_host" ) ) )
        {
            pstmt.setString( 1, joinedHost );
            pstmt.setString( 2, uuid.toString() );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    @Override
    public Map<String, Integer> getJoinedHostList()
    {
        final Map<String, Integer> map = Maps.newHashMap();

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT joined_host, COUNT(*) amount FROM {users-table} GROUP BY joined_host;" )
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
    }

    @Override
    public Map<String, Integer> searchJoinedHosts( String searchTag )
    {
        final Map<String, Integer> map = Maps.newHashMap();

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT joined_host, COUNT(*) amount FROM {users-table} WHERE joined_host LIKE ? GROUP BY joined_host;" )
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
    }

    @Override
    public void ignoreUser( UUID user, UUID ignore )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "INSERT INTO {ignoredusers-table}(user, ignored) VALUES (?, ?);" )
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
    }

    @Override
    public void unignoreUser( UUID user, UUID unignore )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "DELETE FROM {ignoredusers-table} WHERE user = ? AND ignored = ?;" )
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
    }

    @Override
    public void setCurrentServer( final UUID uuid, final String server )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "UPDATE {users-table} SET current_server = ? WHERE uuid = ?;" )
              ) )
        {
            pstmt.setString( 1, server );
            pstmt.setString( 2, uuid.toString() );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    @Override
    public String getCurrentServer( final UUID uuid )
    {
        String server = null;

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT current_server FROM {users-table} WHERE uuid = ?;" )
              ) )
        {
            pstmt.setString( 1, uuid.toString() );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                if ( rs.next() )
                {
                    server = rs.getString( "language" );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
        return server;
    }

    @Override
    public void setCurrentServerBulk( final List<UUID> users, final String server )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "UPDATE {users-table} SET current_server = ? WHERE uuid = ?;" )
              ) )
        {
            for ( UUID user : users )
            {
                pstmt.setString( 1, server );
                pstmt.setString( 2, user.toString() );
                pstmt.addBatch();
            }

            pstmt.executeBatch();
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
    }

    @Override
    public Map<String, String> getCurrentServersBulk( final List<String> users )
    {
        final Map<String, String> userServers = Maps.newHashMap();

        if ( users.isEmpty() )
        {
            return userServers;
        }
        final StringBuilder paramsBuilder = new StringBuilder();

        for ( int i = 0; i < users.size(); i++ )
        {
            if ( i > 0 )
            {
                paramsBuilder.append( ", " );
            }
            paramsBuilder.append( "?" );
        }

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      format( "SELECT username, current_server FROM {users-table} WHERE username IN (" + paramsBuilder.toString() + ");" )
              ) )
        {
            for ( int i = 0; i < users.size(); i++ )
            {
                pstmt.setString( i + 1, users.get( i ) );
            }

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                while ( rs.next() )
                {
                    userServers.put( rs.getString( "username" ), rs.getString( "current_server" ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured: ", e );
        }
        return userServers;
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
