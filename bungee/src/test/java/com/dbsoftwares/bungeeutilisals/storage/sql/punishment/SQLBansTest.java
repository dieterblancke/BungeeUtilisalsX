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
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.BansDao;
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

import static org.junit.Assert.*;

public class SQLBansTest extends SQLTest
{
    private static final SQLUtils SQL_UTILS = new SQLUtils();
    private final String table = PlaceHolderAPI.formatMessage( "{bans-table}" );
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
              PreparedStatement pstmt = connection.prepareStatement( "DELETE FROM " + table + ";" ) )
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
    public void testBan()
    {
        final UserStorage storage = StorageUtils.createRandomUser();
        StorageUtils.insertBan( storage );

        assertEquals( 1, count( table ) );
    }

    @Test
    public void testMultipleBan()
    {
        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();
            StorageUtils.insertBan( storage );
        }

        assertEquals( 10, count( table ) );
    }

    @Test
    public void testIPBan()
    {
        final UserStorage storage = StorageUtils.createRandomUser();
        StorageUtils.insertIPBan( storage );

        assertEquals( 1, count( table ) );
    }

    @Test
    public void testMultipleIPBan()
    {
        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();
            StorageUtils.insertIPBan( storage );
        }

        assertEquals( 10, count( table ) );
    }

    @Test
    public void testTempBan()
    {
        final UserStorage storage = StorageUtils.createRandomUser();
        StorageUtils.insertTempBan( storage, 15 );

        assertEquals( 1, count( table ) );
    }

    @Test
    public void testMultipleTempBan()
    {
        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();
            StorageUtils.insertTempBan( storage, 15 );
        }

        assertEquals( 10, count( table ) );
    }

    @Test
    public void testTempIPBan()
    {
        final UserStorage storage = StorageUtils.createRandomUser();
        StorageUtils.insertTempIPBan( storage, 15 );

        assertEquals( 1, count( table ) );
    }

    @Test
    public void testMultipleTempIPBan()
    {
        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();
            StorageUtils.insertTempIPBan( storage, 15 );
        }

        assertEquals( 10, count( table ) );
    }

    @Test
    public void testBanAndUnban()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        StorageUtils.insertBan( storage );
        assertTrue( dao().isBanned( storage.getUuid(), "ALL" ) );
        assertFalse( dao().isIPBanned( storage.getIp(), "ALL" ) );

        StorageUtils.unban( storage.getUuid() );
        assertFalse( dao().isBanned( storage.getUuid(), "ALL" ) );
    }

    @Test
    public void testBanIPAndUnbanIP()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        StorageUtils.insertIPBan( storage );
        assertTrue( dao().isIPBanned( storage.getIp(), "ALL" ) );
        assertFalse( dao().isBanned( storage.getUuid(), "ALL" ) );

        StorageUtils.unbanIP( storage.getIp() );
        assertFalse( dao().isIPBanned( storage.getIp(), "ALL" ) );

        StorageUtils.insertTempIPBan( storage, 15 );
        assertTrue( dao().isIPBanned( storage.getIp(), "ALL" ) );
        assertFalse( dao().isBanned( storage.getUuid(), "ALL" ) );

        StorageUtils.unbanIP( storage.getIp() );
        assertFalse( dao().isIPBanned( storage.getIp(), "ALL" ) );
    }

    @Test
    public void testTempBanAndUnban()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        StorageUtils.insertTempBan( storage, 15 );
        assertTrue( dao().isBanned( storage.getUuid(), "ALL" ) );
        assertFalse( dao().isIPBanned( storage.getIp(), "ALL" ) );

        StorageUtils.unban( storage.getUuid() );
        assertFalse( dao().isBanned( storage.getUuid(), "ALL" ) );
    }

    @Test
    public void testTempBanIPAndUnbanIP()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        StorageUtils.insertTempIPBan( storage, 15 );
        assertTrue( dao().isIPBanned( storage.getIp(), "ALL" ) );
        assertFalse( dao().isBanned( storage.getUuid(), "ALL" ) );

        StorageUtils.unbanIP( storage.getIp() );
        assertFalse( dao().isIPBanned( storage.getIp(), "ALL" ) );
    }

    @Test
    public void testTempBanDecay()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        StorageUtils.insertTempBan( storage, 3 );
        PunishmentInfo info = dao().getCurrentBan( storage.getUuid(), "ALL" );

        assertFalse( info.isExpired() );
        try
        {
            Thread.sleep( 5000 );
        }
        catch ( InterruptedException e )
        {
            fail();
        }
        info = dao().getCurrentBan( storage.getUuid(), "ALL" );
        assertTrue( info.isExpired() );
    }

    @Test
    public void testGetBans()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        StorageUtils.insertBan( storage );
        assertEquals( 1, dao().getBans( storage.getUuid() ).size() );

        StorageUtils.insertTempBan( storage, 15 );
        assertEquals( 2, dao().getBans( storage.getUuid() ).size() );

        StorageUtils.insertIPBan( storage );
        assertEquals( 2, dao().getBans( storage.getUuid() ).size() );
        assertEquals( 1, dao().getIPBans( storage.getIp() ).size() );

        StorageUtils.insertTempIPBan( storage, 15 );
        assertEquals( 2, dao().getBans( storage.getUuid() ).size() );
        assertEquals( 2, dao().getIPBans( storage.getIp() ).size() );

        StorageUtils.unban( storage.getUuid() );
        assertEquals( 2, dao().getBans( storage.getUuid() ).size() );
        assertEquals( 2, dao().getIPBans( storage.getIp() ).size() );

        StorageUtils.unbanIP( storage.getIp() );
        assertEquals( 2, dao().getBans( storage.getUuid() ).size() );
        assertEquals( 2, dao().getIPBans( storage.getIp() ).size() );
    }

    @Test
    public void testIPTempBanDecay()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        StorageUtils.insertTempIPBan( storage, 3 );
        PunishmentInfo info = dao().getCurrentIPBan( storage.getIp(), "ALL" );

        assertFalse( info.isExpired() );
        try
        {
            Thread.sleep( 5000 );
        }
        catch ( InterruptedException e )
        {
            fail();
        }

        info = dao().getCurrentIPBan( storage.getIp(), "ALL" );
        assertTrue( info.isExpired() );
    }

    @Test
    public void testGetExecutedBans()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        StorageUtils.insertBan( storage );
        StorageUtils.insertTempBan( storage, 15 );
        StorageUtils.insertIPBan( storage );
        StorageUtils.insertTempIPBan( storage, 15 );

        assertEquals( 4, dao().getBansExecutedBy( storage.getUserName() ).size() );

        StorageUtils.unban( storage.getUuid() );
        StorageUtils.unbanIP( storage.getIp() );

        assertEquals( 4, dao().getBansExecutedBy( storage.getUserName() ).size() );
    }

    private BansDao dao()
    {
        return manager().getDao().getPunishmentDao().getBansDao();
    }
}
