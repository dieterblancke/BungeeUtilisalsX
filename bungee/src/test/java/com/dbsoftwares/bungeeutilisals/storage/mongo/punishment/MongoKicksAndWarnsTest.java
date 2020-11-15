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

package com.dbsoftwares.bungeeutilisals.storage.mongo.punishment;

import com.dbsoftwares.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisalsx.common.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisalsx.common.storage.dao.punishments.KickAndWarnDao;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.storage.StorageUtils;
import com.dbsoftwares.bungeeutilisals.storage.mongo.MongoUtils;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager;
import com.mongodb.client.MongoDatabase;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MongoKicksAndWarnsTest
{

    private static final MongoUtils MONGO_UTILS = new MongoUtils();
    private final String kicksTable = PlaceHolderAPI.formatMessage( "{kicks-table}" );
    private final String warnsTable = PlaceHolderAPI.formatMessage( "{warns-table}" );
    private final String usersTable = PlaceHolderAPI.formatMessage( "{users-table}" );

    @BeforeClass
    public static void setup() throws Exception
    {
        MONGO_UTILS.setup();
    }

    @AfterClass
    public static void destroy()
    {
        MONGO_UTILS.destroy();
    }

    @Before
    public void before()
    {
        db().getCollection( kicksTable ).drop();
        db().getCollection( warnsTable ).drop();
        db().getCollection( usersTable ).drop();
    }

    @Test
    public void testKick()
    {
        final UserStorage storage = StorageUtils.createRandomUser();
        StorageUtils.insertKick( storage );

        assertEquals( 1, db().getCollection( kicksTable ).countDocuments() );
    }

    @Test
    public void testMultipleKicks()
    {
        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();
            StorageUtils.insertKick( storage );
        }

        assertEquals( 10, db().getCollection( kicksTable ).countDocuments() );
    }

    @Test
    public void testWarn()
    {
        final UserStorage storage = StorageUtils.createRandomUser();
        StorageUtils.insertWarn( storage );

        assertEquals( 1, db().getCollection( warnsTable ).countDocuments() );
    }

    @Test
    public void testMultipleWarns()
    {
        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();
            StorageUtils.insertWarn( storage );
        }

        assertEquals( 10, db().getCollection( warnsTable ).countDocuments() );
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

    private AbstractStorageManager manager()
    {
        return AbstractStorageManager.getManager();
    }

    private KickAndWarnDao dao()
    {
        return manager().getDao().getPunishmentDao().getKickAndWarnDao();
    }

    private MongoDatabase db()
    {
        return ( (MongoDBStorageManager) manager() ).getDatabase();
    }
}
