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

package com.dbsoftwares.bungeeutilisals.storage.mongo;

import com.dbsoftwares.bungeeutilisalsx.common.api.language.Language;
import com.dbsoftwares.bungeeutilisalsx.common.api.placeholder.PlaceHolderAPI;
import com.dbsoftwares.bungeeutilisalsx.common.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.storage.StorageUtils;
import com.dbsoftwares.bungeeutilisals.storage.mongodb.MongoDBStorageManager;
import com.google.common.collect.Maps;
import com.mongodb.client.MongoDatabase;
import org.apache.commons.compress.utils.Lists;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.*;

public class MongoUserTest
{

    private static final MongoUtils MONGO_UTILS = new MongoUtils();
    private final String table = PlaceHolderAPI.formatMessage( "{users-table}" );

    @BeforeClass
    public static void setup() throws IOException
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
        db().getCollection( table ).drop();
    }

    @Test
    public void testUserCreate()
    {
        StorageUtils.createRandomUser();
        assertEquals( 1, db().getCollection( table ).countDocuments() );
    }

    @Test
    public void testMultipleUserCreate()
    {
        for ( int i = 0; i < 10; i++ )
        {
            StorageUtils.createRandomUser();
        }

        assertEquals( 10, db().getCollection( table ).countDocuments() );
    }

    @Test
    public void testMultipleUserUpdate()
    {
        final List<UUID> uuids = Lists.newArrayList();

        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();

            uuids.add( storage.getUuid() );
        }

        for ( UUID uuid : uuids )
        {
            final UserStorage storage = StorageUtils.updateUser( uuid );
            final UserStorage dbStorage = manager().getDao().getUserDao().getUserData( uuid );

            assertEquals( storage.getUuid(), dbStorage.getUuid() );
            assertEquals( storage.getUserName(), dbStorage.getUserName() );
            assertEquals( storage.getIp(), dbStorage.getIp() );
            assertEquals( storage.getLanguage(), dbStorage.getLanguage() );
        }
    }

    @Test
    public void testGetJoinedHostList()
    {
        final List<String> joinedHosts = Lists.newArrayList();

        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();

            joinedHosts.add( storage.getJoinedHost() );
        }

        // counting hosts
        final Map<String, Integer> hosts = Maps.newHashMap();

        for ( String host : joinedHosts )
        {
            hosts.put( host, hosts.containsKey( host ) ? hosts.get( host ) + 1 : 1 );
        }

        final Map<String, Integer> dbHosts = manager().getDao().getUserDao().getJoinedHostList();
        for ( Map.Entry<String, Integer> entry : hosts.entrySet() )
        {
            assertEquals( entry.getValue(), dbHosts.get( entry.getKey() ) );
        }
    }

    @Test
    public void testMultipleUserUpdateJoinedHost()
    {
        final List<UUID> uuids = Lists.newArrayList();

        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();

            uuids.add( storage.getUuid() );
        }

        for ( UUID uuid : uuids )
        {
            final String joinedHost = StorageUtils.updateJoinedHost( uuid );

            assertEquals( joinedHost, manager().getDao().getUserDao().getUserData( uuid ).getJoinedHost() );
        }
    }

    @Test
    public void testMultipleUserUpdateIp()
    {
        final List<UUID> uuids = Lists.newArrayList();

        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();

            uuids.add( storage.getUuid() );
        }

        for ( UUID uuid : uuids )
        {
            final String ip = StorageUtils.updateIp( uuid );

            assertEquals( ip, manager().getDao().getUserDao().getUserData( uuid ).getIp() );
        }
    }

    @Test
    public void testMultipleUserUpdateUserName()
    {
        final List<UUID> uuids = Lists.newArrayList();

        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();

            uuids.add( storage.getUuid() );
        }

        for ( UUID uuid : uuids )
        {
            final String userName = StorageUtils.updateUserName( uuid );

            assertEquals( userName, manager().getDao().getUserDao().getUserData( uuid ).getUserName() );
        }
    }

    @Test
    public void testMultipleUserUpdateLanguage()
    {
        final List<UUID> uuids = Lists.newArrayList();

        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();

            uuids.add( storage.getUuid() );
        }

        for ( UUID uuid : uuids )
        {
            final Language language = StorageUtils.updateLanguage( uuid );

            assertEquals( language, manager().getDao().getUserDao().getUserData( uuid ).getLanguage() );
        }
    }

    @Test
    public void testMultipleUserUpdateLanguageSpecificGet()
    {
        final List<UUID> uuids = Lists.newArrayList();

        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();

            uuids.add( storage.getUuid() );
        }

        for ( UUID uuid : uuids )
        {
            final Language language = StorageUtils.updateLanguage( uuid );

            assertEquals( language, manager().getDao().getUserDao().getLanguage( uuid ) );
        }
    }

    @Test
    public void testMultipleUserExists()
    {
        final List<UUID> uuids = Lists.newArrayList();

        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();

            uuids.add( storage.getUuid() );
        }

        for ( UUID uuid : uuids )
        {
            assertTrue( manager().getDao().getUserDao().exists( uuid ) );
        }
    }

    @Test
    public void testMultipleUserNotExists()
    {
        for ( int i = 0; i < 10; i++ )
        {
            assertFalse( manager().getDao().getUserDao().exists( UUID.randomUUID() ) );
        }
    }

    @Test
    public void testIgnoredAndUnignoreUsers()
    {
        final UserStorage user = StorageUtils.createRandomUser();
        final List<UserStorage> ignoredUsers = Lists.newArrayList();

        for ( int i = 0; i < 10; i++ )
        {
            final UserStorage storage = StorageUtils.createRandomUser();

            manager().getDao().getUserDao().ignoreUser( user.getUuid(), storage.getUuid() );
            ignoredUsers.add( storage );
        }

        List<String> ignoredDbUsers = manager().getDao().getUserDao().getUserData( user.getUuid() ).getIgnoredUsers();
        assertEquals( 10, ignoredDbUsers.size() );

        for ( UserStorage u : ignoredUsers )
        {
            assertTrue( ignoredDbUsers.contains( u.getUserName() ) );
        }

        for ( int i = 0; i < 5; i++ )
        {
            final UserStorage storage = ignoredUsers.remove( i );

            manager().getDao().getUserDao().unignoreUser( user.getUuid(), storage.getUuid() );
        }

        ignoredDbUsers = manager().getDao().getUserDao().getUserData( user.getUuid() ).getIgnoredUsers();
        assertEquals( 5, ignoredDbUsers.size() );

        for ( UserStorage u : ignoredUsers )
        {
            assertTrue( ignoredDbUsers.contains( u.getUserName() ) );
        }
    }

    private MongoDBStorageManager manager()
    {
        return (MongoDBStorageManager) AbstractStorageManager.getManager();
    }

    private MongoDatabase db()
    {
        final MongoDBStorageManager manager = (MongoDBStorageManager) AbstractStorageManager.getManager();

        return manager.getDatabase();
    }
}
