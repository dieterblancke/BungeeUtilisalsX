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

package be.dieterblancke.bungeeutilisalsx.common.api.storage.dao;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.StorageType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface Dao
{

    ThreadLocal<SimpleDateFormat> SIMPLE_DATE_FORMAT = ThreadLocal.withInitial( () -> new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ) );

    static String formatDateToString( final Date date )
    {
        if ( date == null )
        {
            return null;
        }
        return SIMPLE_DATE_FORMAT.get().format( date );
    }

    static Date formatStringToDate( final String date )
    {
        if ( date == null )
        {
            return null;
        }
        try
        {
            return SIMPLE_DATE_FORMAT.get().parse( date );
        }
        catch ( ParseException e )
        {
            return new Date();
        }
    }

    static String getInsertDateParameter()
    {
        return BuX.getInstance().getAbstractStorageManager().getType() == StorageType.POSTGRESQL
                ? "TO_TIMESTAMP(?, 'YYYY/MM/DD HH24:MI:SS')"
                : "?";
    }

    UserDao getUserDao();

    PunishmentDao getPunishmentDao();

    FriendsDao getFriendsDao();

    ReportsDao getReportsDao();

    OfflineMessageDao getOfflineMessageDao();

    ApiTokenDao getApiTokenDao();
}
