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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLTest
{

    protected int count( final String table )
    {
        int count = 0;
        try ( Connection connection = manager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( "SELECT * FROM " + table + ";" ) )
        {
            final ResultSet rs = pstmt.executeQuery();

            while ( rs.next() )
            {
                count++;
            }
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }
        return count;
    }

    protected AbstractStorageManager manager()
    {
        return AbstractStorageManager.getManager();
    }
}
