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
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.PunishmentDao;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface BansDao
{

    CompletableFuture<Boolean> isBanned( UUID uuid, String server );

    CompletableFuture<Boolean> isIPBanned( String ip, String server );

    CompletableFuture<PunishmentInfo> insertBan( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby );

    CompletableFuture<PunishmentInfo> insertIPBan( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby );

    CompletableFuture<PunishmentInfo> insertTempBan( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration );

    CompletableFuture<PunishmentInfo> insertTempIPBan( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration );

    CompletableFuture<PunishmentInfo> getCurrentBan( UUID uuid, String server );

    CompletableFuture<PunishmentInfo> getCurrentIPBan( String ip, String server );

    CompletableFuture<Void> removeCurrentBan( UUID uuid, String removedBy, String server );

    CompletableFuture<Void> removeCurrentIPBan( String ip, String removedBy, String server );

    CompletableFuture<List<PunishmentInfo>> getBans( UUID uuid );

    CompletableFuture<List<PunishmentInfo>> getBansExecutedBy( String name );

    CompletableFuture<List<PunishmentInfo>> getBans( UUID uuid, String server );

    CompletableFuture<List<PunishmentInfo>> getIPBans( String ip );

    CompletableFuture<List<PunishmentInfo>> getIPBans( String ip, String server );

    CompletableFuture<List<PunishmentInfo>> getRecentBans( int limit );

    CompletableFuture<PunishmentInfo> getById( String id );

    CompletableFuture<PunishmentInfo> getByPunishmentId( String punishmentUid );

    CompletableFuture<Boolean> isPunishmentUidFound( String puid );

    default String createUniqueBanId()
    {
        String uid = Utils.createRandomString(
                PunishmentDao.getPunishmentIdCharacters(),
                ConfigFiles.PUNISHMENT_CONFIG.getConfig().getInteger( "puid-length" )
        );

        // should not enter the loop often - if ever - but this is for safety so existing uids don't get duplicates.
        while ( this.isPunishmentUidFound( uid ).join() )
        {
            uid = Utils.createRandomString(
                    PunishmentDao.getPunishmentIdCharacters(),
                    ConfigFiles.PUNISHMENT_CONFIG.getConfig().getInteger( "puid-length" )
            );
        }

        return uid;
    }

    CompletableFuture<Integer> softDeleteSince( String user, String removedBy, Date date );

    CompletableFuture<Integer> hardDeleteSince( String user, Date date );
}