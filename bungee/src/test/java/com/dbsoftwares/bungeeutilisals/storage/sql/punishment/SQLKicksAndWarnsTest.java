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

package com.dbsoftwares.bungeeutilisals.storage.sql.punishment;

import com.dbsoftwares.bungeeutilisals.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.KickAndWarnDao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.storage.StorageUtils;
import com.dbsoftwares.bungeeutilisals.storage.sql.SQLTest;
import com.dbsoftwares.bungeeutilisals.storage.sql.SQLUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.Assert.assertEquals;

public class SQLKicksAndWarnsTest extends SQLTest
{

    private static final SQLUtils SQL_UTILS = new SQLUtils();
    private final String kicksTable = PlaceHolderAPI.formatMessage( "{kicks-table}" );
    private final String warnsTable = PlaceHolderAPI.formatMessage( "{warns-table}" );
    private final String usersTable = PlaceHolderAPI.formatMessage( "{users-table}" );

    @BeforeClass
    public static void setup() throws Exception
    {
        SQL_UTILS.setup();
    }

    @AfterClass
    public static void destroy()
    {
        SQL_UTILS.destroy();
    }

    @Before
    public void before()
    {
        try ( Connection connection = manager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( "DELETE FROM " + warnsTable + ";" ) )
        {
            pstmt.execute();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }
        try ( Connection connection = manager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( "DELETE FROM " + kicksTable + ";" ) )
        {
            pstmt.execute();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }
        try ( Connection connection = manager().getConnection();
              PreparedStatement pstmt = connection.prepareStatement( "DELETE FROM " + usersTable + ";" ) )
        {
            pstmt.execute();
        }
        catch ( SQLException e )
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testKick()
    {
        final UserStorage storage = StorageUtils.createRandomUser();
        StorageUtils.insertKick( storage );

        assertEquals( 1, count( kicksTable ) );
    }

    @Test
    public void testMultipleKicks()
    {
        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();
            StorageUtils.insertKick( storage );
        }

        assertEquals( 10, count( kicksTable ) );
    }

    @Test
    public void testWarn()
    {
        final UserStorage storage = StorageUtils.createRandomUser();
        StorageUtils.insertWarn( storage );

        assertEquals( 1, count( warnsTable ) );
    }

    @Test
    public void testMultipleWarns()
    {
        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();
            StorageUtils.insertWarn( storage );
        }

        assertEquals( 10, count( warnsTable ) );
    }

    @Test
    public void testGetKicks()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        for ( int i = 0; i < 8; i++ )
        {
            StorageUtils.insertKick( storage );
        }

        assertEquals( 8, dao().getKicks( storage.getUuid() ).size() );
    }

    @Test
    public void testGetWarns()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        for ( int i = 0; i < 8; i++ )
        {
            StorageUtils.insertWarn( storage );
        }

        assertEquals( 8, dao().getWarns( storage.getUuid() ).size() );
    }

    @Test
    public void testGetExecutedKicks()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        for ( int i = 0; i < 8; i++ )
        {
            StorageUtils.insertKick( storage );
        }

        assertEquals( 8, dao().getKicksExecutedBy( storage.getUserName() ).size() );
    }

    @Test
    public void testGetExecutedWarns()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        for ( int i = 0; i < 8; i++ )
        {
            StorageUtils.insertWarn( storage );
        }

        assertEquals( 8, dao().getWarnsExecutedBy( storage.getUserName() ).size() );
    }

    private KickAndWarnDao dao()
    {
        return manager().getDao().getPunishmentDao().getKickAndWarnDao();
    }
}
