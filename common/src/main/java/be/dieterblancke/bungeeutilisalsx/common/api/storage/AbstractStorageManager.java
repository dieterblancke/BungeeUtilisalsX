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

package be.dieterblancke.bungeeutilisalsx.common.api.storage;

import be.dieterblancke.bungeeutilisalsx.common.AbstractBungeeUtilisalsX;
import be.dieterblancke.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.Dao;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Data
@RequiredArgsConstructor
public abstract class AbstractStorageManager
{

    private final StorageType type;
    private final Dao dao;

    public String getName()
    {
        return type.getName();
    }

    public abstract Connection getConnection() throws SQLException;

    public void initialize() throws Exception
    {
        if ( type.equals( StorageType.MONGODB ) )
        {
            return;
        }
        try ( InputStream is = AbstractBungeeUtilisalsX.class.getClassLoader().getResourceAsStream( type.getSchema() ) )
        {
            if ( is == null )
            {
                throw new Exception( "Could not find schema for " + type.toString() + ": " + type.getSchema() + "!" );
            }
            try ( BufferedReader reader = new BufferedReader( new InputStreamReader( is, StandardCharsets.UTF_8 ) );
                  Connection connection = getConnection(); Statement st = connection.createStatement() )
            {
                StringBuilder builder = new StringBuilder();
                String line;
                while ( ( line = reader.readLine() ) != null )
                {
                    builder.append( line );

                    if ( line.endsWith( ";" ) )
                    {
                        builder.deleteCharAt( builder.length() - 1 );

                        String statement = PlaceHolderAPI.formatMessage( builder.toString().trim() );
                        if ( !statement.isEmpty() )
                        {
                            st.executeUpdate( statement );
                        }

                        builder = new StringBuilder();
                    }
                }
            }
        }
    }

    public abstract void close() throws SQLException;

}