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
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.OfflineMessageDao;
import com.google.gson.Gson;
import lombok.SneakyThrows;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class SqlOfflineMessageDao implements OfflineMessageDao
{

    private static final Gson GSON = new Gson();

    @Override
    @SneakyThrows
    public List<OfflineMessage> getOfflineMessages( final String username )
    {
        final List<OfflineMessage> messages = new ArrayList<>();

        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "select * from bu_offline_message where username = ? and active = ?;"
              ) )
        {
            pstmt.setString( 1, username );
            pstmt.setBoolean( 2, true );

            try ( ResultSet rs = pstmt.executeQuery() )
            {
                while ( rs.next() )
                {
                    messages.add( new OfflineMessage(
                            rs.getLong( "id" ),
                            rs.getString( "message" ),
                            GSON.fromJson( rs.getString( "parameters" ), Object[].class )
                    ) );
                }
            }
        }

        return messages;
    }

    @Override
    @SneakyThrows
    public void sendOfflineMessage( final String username, final OfflineMessage message )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "insert into bu_offline_message(username, message, parameters, active) values(?, ?, ?, ?);"
              ) )
        {
            pstmt.setString( 1, username );
            pstmt.setString( 2, message.getLanguagePath() );
            pstmt.setString( 3, GSON.toJson( message.getPlaceholders() ) );
            pstmt.setBoolean( 4, true );

            pstmt.execute();
        }
    }

    @Override
    @SneakyThrows
    public void updateOfflineMessage( final Long id, final boolean active )
    {
        try ( Connection connection = BuX.getApi().getStorageManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      "update bu_offline_message set active = ? where id = ?;"
              ) )
        {
            pstmt.setBoolean( 1, active );
            pstmt.setLong( 2, id );

            pstmt.executeUpdate();
        }
    }
}
