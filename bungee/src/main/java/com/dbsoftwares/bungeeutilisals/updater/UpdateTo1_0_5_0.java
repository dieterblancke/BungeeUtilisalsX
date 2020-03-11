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

package com.dbsoftwares.bungeeutilisals.updater;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateTo1_0_5_0 implements Update
{

    @Override
    public void update()
    {
        if ( AbstractStorageManager.getManager().getType() == AbstractStorageManager.StorageType.MONGODB )
        {
            return;
        }
        BUCore.getLogger().info( "Updating bans table ..." );

        try ( Connection connection = AbstractStorageManager.getManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      PlaceHolderAPI.formatMessage( "UPDATE {bans-table} SET server = ?;" )
              ) )
        {
            pstmt.setString( 1, "ALL" );
            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }

        BUCore.getLogger().info( "Successfully updated bans table ..." );

        BUCore.getLogger().info( "Updating mutes table ..." );

        try ( Connection connection = AbstractStorageManager.getManager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement(
                      PlaceHolderAPI.formatMessage( "UPDATE {mutes-table} SET server = ?;" )
              ) )
        {
            pstmt.setString( 1, "ALL" );
            pstmt.executeUpdate();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }

        BUCore.getLogger().info( "Successfully updated mutes table ..." );
    }
}
