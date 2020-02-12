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

package com.dbsoftwares.bungeeutilisals.storage.sql;

import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import net.md_5.bungee.api.plugin.Plugin;

import java.sql.*;

import static com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI.formatMessage;

public abstract class SQLStorageManager extends AbstractStorageManager
{
    public SQLStorageManager( Plugin plugin, StorageType type, Dao dao )
    {
        super( plugin, type, dao );
    }

    @Override
    public void initialize() throws Exception
    {
        super.initialize();

        try ( Connection connection = getConnection() )
        {
            final DatabaseMetaData metaData = connection.getMetaData();

            // Adding joined_host table if needed
            initJoinedHostColumn( connection, metaData );
            // Adding removed_at column if needed
            initRemovedAtColumn( connection, metaData );
        }
        catch ( Exception ignored )
        {
        }
    }

    private void initJoinedHostColumn( final Connection connection, final DatabaseMetaData metaData ) throws SQLException
    {
        try ( ResultSet rs = metaData.getColumns( null, null, formatMessage( "{users-table}" ), "joined_host" ) )
        {
            if ( !rs.next() )
            {
                try ( PreparedStatement pstmt = connection.prepareStatement( formatMessage( "ALTER TABLE {users-table} ADD joined_host TEXT" ) ) )
                {
                    pstmt.execute();
                }
            }
        }
    }

    private void initRemovedAtColumn( final Connection connection, final DatabaseMetaData metaData ) throws SQLException
    {
        final String type = this.getType() == StorageType.SQLITE ? "DATETIME" : "TIMESTAMP";

        try ( ResultSet rs = metaData.getColumns( null, null, formatMessage( "{bans-table}" ), "removed_at" ) )
        {
            if ( !rs.next() )
            {
                try ( PreparedStatement pstmt = connection.prepareStatement( formatMessage( "ALTER TABLE {bans-table} ADD removed_at " + type + " DEFAULT NULL;" ) ) )
                {
                    pstmt.execute();
                }
            }
        }
        try ( ResultSet rs = metaData.getColumns( null, null, formatMessage( "{mutes-table}" ), "removed_at" ) )
        {
            if ( !rs.next() )
            {
                try ( PreparedStatement pstmt = connection.prepareStatement( formatMessage( "ALTER TABLE {mutes-table} ADD removed_at " + type + " DEFAULT NULL;" ) ) )
                {
                    pstmt.execute();
                }
            }
        }
    }
}
