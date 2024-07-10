package dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments;

import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentInfo;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface KickAndWarnDao
{

    CompletableFuture<PunishmentInfo> insertWarn( UUID uuid, String user, String ip, String reason, String server, String executedby );

    CompletableFuture<PunishmentInfo> insertKick( UUID uuid, String user, String ip, String reason, String server, String executedby );

    CompletableFuture<List<PunishmentInfo>> getKicks( final UUID uuid );

    CompletableFuture<List<PunishmentInfo>> getWarns( final UUID uuid );

    CompletableFuture<List<PunishmentInfo>> getKicksExecutedBy( String name );

    CompletableFuture<List<PunishmentInfo>> getWarnsExecutedBy( String name );

    CompletableFuture<PunishmentInfo> getKickById( final String id );

    CompletableFuture<PunishmentInfo> getWarnById( final String id );
}
