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
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.PunishmentDao;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.List;
import java.util.UUID;

public interface MutesDao
{

    boolean isMuted( UUID uuid, String server );

    boolean isIPMuted( String ip, String server );

    boolean isMuted( PunishmentType type, UUID uuid, String server );

    boolean isIPMuted( PunishmentType type, String ip, String server );

    PunishmentInfo insertMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby );

    PunishmentInfo insertIPMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby );

    PunishmentInfo insertTempMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration );

    PunishmentInfo insertTempIPMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration );

    PunishmentInfo getCurrentMute( UUID uuid, String server );

    PunishmentInfo getCurrentIPMute( String ip, String server );

    void removeCurrentMute( UUID uuid, String removedBy, String server );

    void removeCurrentIPMute( String ip, String removedBy, String server );

    List<PunishmentInfo> getMutes( UUID uuid );

    List<PunishmentInfo> getMutes( UUID uuid, String server );

    List<PunishmentInfo> getMutesExecutedBy( String name );

    List<PunishmentInfo> getIPMutes( String ip );

    List<PunishmentInfo> getIPMutes( String ip, String server );

    PunishmentInfo getById( String id );

    PunishmentInfo getByPunishmentId( String punishmentUid );

    List<PunishmentInfo> getActiveMutes( UUID uuid );

    List<PunishmentInfo> getActiveIPMutes( String ip );

    boolean isPunishmentUidFound( String puid );

    default String createUniqueMuteId()
    {
        String uid = Utils.createRandomString(
                PunishmentDao.getPunishmentIdCharacters(),
                ConfigFiles.PUNISHMENT_CONFIG.getConfig().getInteger( "puid-length" )
        );

        // should not enter the loop often - if ever - but this is for safety so existing uids don't get duplicates.
        while ( this.isPunishmentUidFound( uid ) )
        {
            uid = Utils.createRandomString(
                    PunishmentDao.getPunishmentIdCharacters(),
                    ConfigFiles.PUNISHMENT_CONFIG.getConfig().getInteger( "puid-length" )
            );
        }

        return uid;
    }
}
