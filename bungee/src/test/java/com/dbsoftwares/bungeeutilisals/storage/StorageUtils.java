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

package com.dbsoftwares.bungeeutilisals.storage;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.language.Language;
import com.dbsoftwares.bungeeutilisals.api.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Date;
import java.util.UUID;

public class StorageUtils
{

    public static UserStorage createRandomUser()
    {
        final UserStorage storage = buildUserStorage();

        AbstractStorageManager.getManager().getDao().getUserDao().createUser(
                storage.getUuid(), storage.getUserName(), storage.getIp(), storage.getLanguage(), storage.getJoinedHost()
        );

        return storage;
    }

    public static UserStorage updateUser( final UUID uuid )
    {
        final UserStorage storage = buildUserStorage( uuid );

        AbstractStorageManager.getManager().getDao().getUserDao().updateUser(
                storage.getUuid(), storage.getUserName(), storage.getIp(), storage.getLanguage(), new Date()
        );

        return storage;
    }

    public static String updateJoinedHost( final UUID uuid )
    {
        final String str = RandomStringUtils.randomAlphanumeric( 3 );

        AbstractStorageManager.getManager().getDao().getUserDao().setJoinedHost( uuid, str );

        return str;
    }

    public static String updateIp( final UUID uuid )
    {
        final String str = RandomStringUtils.randomAlphanumeric( 12 );

        AbstractStorageManager.getManager().getDao().getUserDao().setIP( uuid, str );

        return str;
    }

    public static String updateUserName( final UUID uuid )
    {
        final String str = RandomStringUtils.randomAlphanumeric( 12 );

        AbstractStorageManager.getManager().getDao().getUserDao().setName( uuid, str );

        return str;
    }

    public static Language updateLanguage( final UUID uuid )
    {
        final Language language = BUCore.getApi().getLanguageManager().getDefaultLanguage();

        AbstractStorageManager.getManager().getDao().getUserDao().setLanguage( uuid, language );

        return language;
    }

    private static UserStorage buildUserStorage()
    {
        return buildUserStorage( null );
    }

    private static UserStorage buildUserStorage( final UUID uuid )
    {
        final UserStorage storage = new UserStorage();

        storage.setUuid( uuid == null ? UUID.randomUUID() : uuid );
        storage.setUserName( RandomStringUtils.randomAlphanumeric( 12 ) );
        storage.setIp( RandomStringUtils.randomAlphanumeric( 12 ) );
        storage.setLanguage( BUCore.getApi().getLanguageManager().getDefaultLanguage() );
        storage.setJoinedHost( RandomStringUtils.randomAlphanumeric( 3 ) );

        return storage;
    }
}
