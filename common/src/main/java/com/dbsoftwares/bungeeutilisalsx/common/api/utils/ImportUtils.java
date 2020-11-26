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

package com.dbsoftwares.bungeeutilisalsx.common.api.utils;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisalsx.common.api.storage.dao.PunishmentDao;

import java.util.UUID;

public class ImportUtils
{

    public void insertPunishment( PunishmentType type, UUID uuid, String user, String ip, String reason, long duration,
                                  String server, boolean active, String executedBy )
    {
        final PunishmentDao punishmentDao = BuX.getApi().getStorageManager().getDao().getPunishmentDao();

        switch ( type )
        {
            case BAN:
                punishmentDao.getBansDao().insertBan( uuid, user, ip, reason, server, active, executedBy );
                break;
            case TEMPBAN:
                punishmentDao.getBansDao().insertTempBan( uuid, user, ip, reason, server, active, executedBy, duration );
                break;
            case IPBAN:
                punishmentDao.getBansDao().insertIPBan( uuid, user, ip, reason, server, active, executedBy );
                break;
            case IPTEMPBAN:
                punishmentDao.getBansDao().insertTempIPBan( uuid, user, ip, reason, server, active, executedBy, duration );
                break;
            case MUTE:
                punishmentDao.getMutesDao().insertMute( uuid, user, ip, reason, server, active, executedBy );
                break;
            case TEMPMUTE:
                punishmentDao.getMutesDao().insertTempMute( uuid, user, ip, reason, server, active, executedBy, duration );
                break;
            case IPMUTE:
                punishmentDao.getMutesDao().insertIPMute( uuid, user, ip, reason, server, active, executedBy );
                break;
            case IPTEMPMUTE:
                punishmentDao.getMutesDao().insertTempIPMute( uuid, user, ip, reason, server, active, executedBy, duration );
            case KICK:
                punishmentDao.getKickAndWarnDao().insertKick( uuid, user, ip, reason, server, executedBy );
                break;
            case WARN:
                punishmentDao.getKickAndWarnDao().insertWarn( uuid, user, ip, reason, server, executedBy );
                break;
        }
    }
}
