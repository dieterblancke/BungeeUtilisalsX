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
import com.dbsoftwares.bungeeutilisalsx.common.api.language.Language;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisalsx.common.storage.AbstractStorageManager;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisalsx.common.utils.TimeUnit;
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

    public static PunishmentInfo insertBan( final UserStorage storage )
    {
        return AbstractStorageManager.getManager().getDao().getPunishmentDao().getBansDao().insertBan(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                RandomStringUtils.randomAlphanumeric( 32 ),
                "ALL",
                true,
                storage.getUserName()
        );
    }

    public static PunishmentInfo insertTempBan( final UserStorage storage, long duration )
    {
        return AbstractStorageManager.getManager().getDao().getPunishmentDao().getBansDao().insertTempBan(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                RandomStringUtils.randomAlphanumeric( 32 ),
                "ALL",
                true,
                storage.getUserName(),
                System.currentTimeMillis() + TimeUnit.SECONDS.toMillis( duration )
        );
    }

    public static PunishmentInfo insertIPBan( final UserStorage storage )
    {
        return AbstractStorageManager.getManager().getDao().getPunishmentDao().getBansDao().insertIPBan(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                RandomStringUtils.randomAlphanumeric( 32 ),
                "ALL",
                true,
                storage.getUserName()
        );
    }

    public static PunishmentInfo insertTempIPBan( final UserStorage storage, long duration )
    {
        return AbstractStorageManager.getManager().getDao().getPunishmentDao().getBansDao().insertTempIPBan(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                RandomStringUtils.randomAlphanumeric( 32 ),
                "ALL",
                true,
                storage.getUserName(),
                System.currentTimeMillis() + TimeUnit.SECONDS.toMillis( duration )
        );
    }

    public static PunishmentInfo insertMute( final UserStorage storage )
    {
        return AbstractStorageManager.getManager().getDao().getPunishmentDao().getMutesDao().insertMute(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                RandomStringUtils.randomAlphanumeric( 32 ),
                "ALL",
                true,
                storage.getUserName()
        );
    }

    public static PunishmentInfo insertTempMute( final UserStorage storage, long duration )
    {
        return AbstractStorageManager.getManager().getDao().getPunishmentDao().getMutesDao().insertTempMute(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                RandomStringUtils.randomAlphanumeric( 32 ),
                "ALL",
                true,
                storage.getUserName(),
                System.currentTimeMillis() + TimeUnit.SECONDS.toMillis( duration )
        );
    }

    public static PunishmentInfo insertIPMute( final UserStorage storage )
    {
        return AbstractStorageManager.getManager().getDao().getPunishmentDao().getMutesDao().insertIPMute(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                RandomStringUtils.randomAlphanumeric( 32 ),
                "ALL",
                true,
                storage.getUserName()
        );
    }

    public static PunishmentInfo insertTempIPMute( final UserStorage storage, long duration )
    {
        return AbstractStorageManager.getManager().getDao().getPunishmentDao().getMutesDao().insertTempIPMute(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                RandomStringUtils.randomAlphanumeric( 32 ),
                "ALL",
                true,
                storage.getUserName(),
                System.currentTimeMillis() + TimeUnit.SECONDS.toMillis( duration )
        );
    }


    public static PunishmentInfo insertKick( final UserStorage storage )
    {
        return AbstractStorageManager.getManager().getDao().getPunishmentDao().getKickAndWarnDao().insertKick(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                RandomStringUtils.randomAlphanumeric( 32 ),
                "ALL",
                storage.getUserName()
        );
    }

    public static PunishmentInfo insertWarn( final UserStorage storage )
    {
        return AbstractStorageManager.getManager().getDao().getPunishmentDao().getKickAndWarnDao().insertWarn(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                RandomStringUtils.randomAlphanumeric( 32 ),
                "ALL",
                storage.getUserName()
        );
    }

    public static void unban( final UUID uuid )
    {
        AbstractStorageManager.getManager().getDao().getPunishmentDao().getBansDao().removeCurrentBan(
                uuid,
                RandomStringUtils.randomAlphanumeric( 12 ),
                "ALL"
        );
    }

    public static void unbanIP( final String ip )
    {
        AbstractStorageManager.getManager().getDao().getPunishmentDao().getBansDao().removeCurrentIPBan(
                ip,
                RandomStringUtils.randomAlphanumeric( 12 ),
                "ALL"
        );
    }

    public static void unmute( final UUID uuid )
    {
        AbstractStorageManager.getManager().getDao().getPunishmentDao().getMutesDao().removeCurrentMute(
                uuid,
                RandomStringUtils.randomAlphanumeric( 12 ),
                "ALL"
        );
    }

    public static void unmuteIP( final String ip )
    {
        AbstractStorageManager.getManager().getDao().getPunishmentDao().getMutesDao().removeCurrentIPMute(
                ip,
                RandomStringUtils.randomAlphanumeric( 12 ),
                "ALL"
        );
    }
}
