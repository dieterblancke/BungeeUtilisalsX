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

package be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.punishments;

import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.List;
import java.util.UUID;

public interface BansDao
{

    static boolean useServerPunishments()
    {
        try
        {
            return ConfigFiles.PUNISHMENTS.getConfig().getBoolean( "per-server-punishments" );
        }
        catch ( Exception e )
        {
            return true;
        }
    }

    boolean isBanned( UUID uuid, String server );

    boolean isIPBanned( String ip, String server );

    boolean isBanned( PunishmentType type, UUID uuid, String server );

    PunishmentInfo insertBan( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby );

    PunishmentInfo insertIPBan( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby );

    PunishmentInfo insertTempBan( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration );

    PunishmentInfo insertTempIPBan( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration );

    boolean isIPBanned( PunishmentType type, String ip, String server );

    PunishmentInfo getCurrentBan( UUID uuid, String server );

    PunishmentInfo getCurrentIPBan( String ip, String server );

    void removeCurrentBan( UUID uuid, String removedBy, String server );

    void removeCurrentIPBan( String ip, String removedBy, String server );

    List<PunishmentInfo> getBans( UUID uuid );

    List<PunishmentInfo> getBansExecutedBy( String name );

    List<PunishmentInfo> getBans( UUID uuid, String server );

    List<PunishmentInfo> getIPBans( String ip );

    List<PunishmentInfo> getIPBans( String ip, String server );

    PunishmentInfo getById( String id );
}