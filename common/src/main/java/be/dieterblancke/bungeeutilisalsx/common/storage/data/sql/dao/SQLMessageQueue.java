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
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.MessageQueue;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.other.QueuedMessage;
import com.google.gson.Gson;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;
import java.util.logging.Level;


public class SQLMessageQueue extends LinkedList<QueuedMessage> implements MessageQueue<QueuedMessage>
{

    private static final Gson GSON = new Gson();

    private final UUID uuid;
    private final String name;
    private final String ip;

    public SQLMessageQueue()
    {
        this.uuid = null;
        this.name = null;
        this.ip = null;
    }

    public SQLMessageQueue( final UUID uuid, final String name, final String ip )
    {
        this.uuid = uuid;
        this.name = name;
        this.ip = ip;

        fetchMessages();
    }

    @Override
    public boolean offer( final QueuedMessage message )
    {
        throw new UnsupportedOperationException( "Not supported." );
    }

    @Override
    public boolean add( final QueuedMessage message )
    {
        try
        {
            addMessage( message );
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
            return false;
        }

        return super.add( message );
    }

    @Override
    public QueuedMessage poll()
    {
        final QueuedMessage message = super.poll();

        if ( message != null )
        {
            handle( message );
        }

        return message;
    }

    @Override
    public QueuedMessage remove()
    {
        final QueuedMessage message = super.remove();

        handle( message );

        return message;
    }

    private void addMessage( final QueuedMessage message ) throws SQLException
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "INSERT INTO bu_messagequeue(user, message, type, active, date) VALUES(?, ?, ?, ?, ?);"
              ) )
        {
            pstmt.setString( 1, message.getUser() );
            pstmt.setString( 2, GSON.toJson( message.getMessage() ) );
            pstmt.setString( 3, message.getType() );
            pstmt.setBoolean( 4, true );
            pstmt.setString( 5, Dao.formatDateToString( new Date() ) );

            pstmt.executeUpdate();
        }
    }

    private void handle( final QueuedMessage message )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "UPDATE bu_messagequeue SET active = ? WHERE id = ?;"
              ) )
        {
            pstmt.setBoolean( 1, false );
            pstmt.setLong( 2, message.getId() );

            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }
    }

    public void refetch()
    {
        clear();
        fetchMessages();
    }

    private void fetchMessages()
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "SELECT * FROM bu_messagequeue WHERE active = ? AND ((user = ? AND type = ?) OR (user = ? AND type = ?) OR (user = ? AND type = ?));"
              ) )
        {
            pstmt.setBoolean( 1, true );
            pstmt.setString( 2, uuid.toString() );
            pstmt.setString( 3, "UUID" );
            pstmt.setString( 4, name );
            pstmt.setString( 5, "NAME" );
            pstmt.setString( 6, ip );
            pstmt.setString( 7, "IP" );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                while ( rs.next() )
                {
                    super.add( new QueuedMessage(
                            rs.getLong( "id" ),
                            rs.getString( "user" ),
                            GSON.fromJson( rs.getString( "message" ), QueuedMessage.Message.class ),
                            rs.getString( "type" )
                    ) );
                }
            }
        }
        catch ( SQLException e )
        {
            BuX.getLogger().log( Level.SEVERE, "An error occured:", e );
        }
    }
}
