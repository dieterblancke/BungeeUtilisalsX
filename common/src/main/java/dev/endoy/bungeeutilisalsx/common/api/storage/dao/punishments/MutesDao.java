package dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments;

import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.PunishmentDao;
import dev.endoy.bungeeutilisalsx.common.api.utils.Utils;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface MutesDao
{

    CompletableFuture<Boolean> isMuted( UUID uuid, String server );

    CompletableFuture<Boolean> isIPMuted( String ip, String server );

    CompletableFuture<PunishmentInfo> insertMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby );

    CompletableFuture<PunishmentInfo> insertIPMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby );

    CompletableFuture<PunishmentInfo> insertTempMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration );

    CompletableFuture<PunishmentInfo> insertTempIPMute( UUID uuid, String user, String ip, String reason, String server, boolean active, String executedby, long duration );

    CompletableFuture<PunishmentInfo> getCurrentMute( UUID uuid, String server );

    CompletableFuture<PunishmentInfo> getCurrentIPMute( String ip, String server );

    CompletableFuture<Void> removeCurrentMute( UUID uuid, String removedBy, String server );

    CompletableFuture<Void> removeCurrentIPMute( String ip, String removedBy, String server );

    CompletableFuture<List<PunishmentInfo>> getMutes( UUID uuid );

    CompletableFuture<List<PunishmentInfo>> getMutes( UUID uuid, String server );

    CompletableFuture<List<PunishmentInfo>> getMutesExecutedBy( String name );

    CompletableFuture<List<PunishmentInfo>> getIPMutes( String ip );

    CompletableFuture<List<PunishmentInfo>> getIPMutes( String ip, String server );

    CompletableFuture<List<PunishmentInfo>> getRecentMutes( int limit );

    CompletableFuture<PunishmentInfo> getById( String id );

    CompletableFuture<PunishmentInfo> getByPunishmentId( String punishmentUid );

    CompletableFuture<List<PunishmentInfo>> getActiveMutes( UUID uuid );

    CompletableFuture<List<PunishmentInfo>> getActiveIPMutes( String ip );

    CompletableFuture<Boolean> isPunishmentUidFound( String puid );

    default String createUniqueMuteId()
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
