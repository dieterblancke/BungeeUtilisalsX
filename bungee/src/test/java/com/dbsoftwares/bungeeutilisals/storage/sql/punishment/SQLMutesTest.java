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

import com.dbsoftwares.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisalsx.common.storage.dao.punishments.MutesDao;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.UserStorage;
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

public class SQLMutesTest extends SQLTest
{
    private static final SQLUtils SQL_UTILS = new SQLUtils();
    private final String table = PlaceHolderAPI.formatMessage( "{mutes-table}" );
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
    public void testMute()
    {
        final UserStorage storage = StorageUtils.createRandomUser();
        StorageUtils.insertMute( storage );

        assertEquals( 1, count( table ) );
    }

    @Test
    public void testMultipleMute()
    {
        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();
            StorageUtils.insertMute( storage );
        }

        assertEquals( 10, count( table ) );
    }

    @Test
    public void testIPMute()
    {
        final UserStorage storage = StorageUtils.createRandomUser();
        StorageUtils.insertIPMute( storage );

        assertEquals( 1, count( table ) );
    }

    @Test
    public void testMultipleIPMute()
    {
        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();
            StorageUtils.insertIPMute( storage );
        }

        assertEquals( 10, count( table ) );
    }

    @Test
    public void testTempMute()
    {
        final UserStorage storage = StorageUtils.createRandomUser();
        StorageUtils.insertTempMute( storage, 15 );

        assertEquals( 1, count( table ) );
    }

    @Test
    public void testMultipleTempMute()
    {
        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();
            StorageUtils.insertTempMute( storage, 15 );
        }

        assertEquals( 10, count( table ) );
    }

    @Test
    public void testTempIPMute()
    {
        final UserStorage storage = StorageUtils.createRandomUser();
        StorageUtils.insertTempIPMute( storage, 15 );

        assertEquals( 1, count( table ) );
    }

    @Test
    public void testMultipleTempIPMute()
    {
        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();
            StorageUtils.insertTempIPMute( storage, 15 );
        }

        assertEquals( 10, count( table ) );
    }

    @Test
    public void testMuteAndUnmute()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        StorageUtils.insertMute( storage );
        assertTrue( dao().isMuted( storage.getUuid(), "ALL" ) );
        assertFalse( dao().isIPMuted( storage.getIp(), "ALL" ) );

        StorageUtils.unmute( storage.getUuid() );
        assertFalse( dao().isMuted( storage.getUuid(), "ALL" ) );
    }

    @Test
    public void testMuteIPAndUnmuteIP()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        StorageUtils.insertIPMute( storage );
        assertTrue( dao().isIPMuted( storage.getIp(), "ALL" ) );
        assertFalse( dao().isMuted( storage.getUuid(), "ALL" ) );

        StorageUtils.unmuteIP( storage.getIp() );
        assertFalse( dao().isIPMuted( storage.getIp(), "ALL" ) );

        StorageUtils.insertTempIPMute( storage, 15 );
        assertTrue( dao().isIPMuted( storage.getIp(), "ALL" ) );
        assertFalse( dao().isMuted( storage.getUuid(), "ALL" ) );

        StorageUtils.unmuteIP( storage.getIp() );
        assertFalse( dao().isIPMuted( storage.getIp(), "ALL" ) );
    }

    @Test
    public void testTempMuteAndUnmute()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        StorageUtils.insertTempMute( storage, 15 );
        assertTrue( dao().isMuted( storage.getUuid(), "ALL" ) );
        assertFalse( dao().isIPMuted( storage.getIp(), "ALL" ) );

        StorageUtils.unmute( storage.getUuid() );
        assertFalse( dao().isMuted( storage.getUuid(), "ALL" ) );
    }

    @Test
    public void testTempMuteIPAndUnmuteIP()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        StorageUtils.insertTempIPMute( storage, 15 );
        assertTrue( dao().isIPMuted( storage.getIp(), "ALL" ) );
        assertFalse( dao().isMuted( storage.getUuid(), "ALL" ) );

        StorageUtils.unmuteIP( storage.getIp() );
        assertFalse( dao().isIPMuted( storage.getIp(), "ALL" ) );
    }

    @Test
    public void testTempMuteDecay()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        StorageUtils.insertTempMute( storage, 3 );
        PunishmentInfo info = dao().getCurrentMute( storage.getUuid(), "ALL" );

        assertFalse( info.isExpired() );
        try
        {
            Thread.sleep( 5000 );
        }
        catch ( InterruptedException e )
        {
            fail();
        }
        info = dao().getCurrentMute( storage.getUuid(), "ALL" );
        assertTrue( info.isExpired() );
    }

    @Test
    public void testIPTempMuteDecay()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        StorageUtils.insertTempIPMute( storage, 3 );
        PunishmentInfo info = dao().getCurrentIPMute( storage.getIp(), "ALL" );

        assertFalse( info.isExpired() );
        try
        {
            Thread.sleep( 5000 );
        }
        catch ( InterruptedException e )
        {
            fail();
        }

        info = dao().getCurrentIPMute( storage.getIp(), "ALL" );
        assertTrue( info.isExpired() );
    }

    @Test
    public void testGetMutes()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        StorageUtils.insertMute( storage );
        assertEquals( 1, dao().getMutes( storage.getUuid() ).size() );

        StorageUtils.insertTempMute( storage, 15 );
        assertEquals( 2, dao().getMutes( storage.getUuid() ).size() );

        StorageUtils.insertIPMute( storage );
        assertEquals( 2, dao().getMutes( storage.getUuid() ).size() );
        assertEquals( 1, dao().getIPMutes( storage.getIp() ).size() );

        StorageUtils.insertTempIPMute( storage, 15 );
        assertEquals( 2, dao().getMutes( storage.getUuid() ).size() );
        assertEquals( 2, dao().getIPMutes( storage.getIp() ).size() );

        StorageUtils.unmute( storage.getUuid() );
        assertEquals( 2, dao().getMutes( storage.getUuid() ).size() );
        assertEquals( 2, dao().getIPMutes( storage.getIp() ).size() );

        StorageUtils.unmuteIP( storage.getIp() );
        assertEquals( 2, dao().getMutes( storage.getUuid() ).size() );
        assertEquals( 2, dao().getIPMutes( storage.getIp() ).size() );
    }

    @Test
    public void testGetExecutedMutes()
    {
        final UserStorage storage = StorageUtils.createRandomUser();

        StorageUtils.insertMute( storage );
        StorageUtils.insertTempMute( storage, 15 );
        StorageUtils.insertIPMute( storage );
        StorageUtils.insertTempIPMute( storage, 15 );

        assertEquals( 4, dao().getMutesExecutedBy( storage.getUserName() ).size() );

        StorageUtils.unmute( storage.getUuid() );
        StorageUtils.unmuteIP( storage.getIp() );

        assertEquals( 4, dao().getMutesExecutedBy( storage.getUserName() ).size() );
    }

    private MutesDao dao()
    {
        return manager().getDao().getPunishmentDao().getMutesDao();
    }
}
