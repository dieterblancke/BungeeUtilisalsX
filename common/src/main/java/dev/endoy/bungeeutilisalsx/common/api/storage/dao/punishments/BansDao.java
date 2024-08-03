package dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments;

import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.PunishmentDao;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

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