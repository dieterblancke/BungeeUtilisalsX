package dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments;

import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentTrackInfo;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface TracksDao
{

    static boolean useServerPunishments()
    {
        try
        {
            return ConfigFiles.PUNISHMENT_CONFIG.getConfig().getBoolean( "per-server-punishments" );
        }
        catch ( Exception e )
        {
            return true;
        }
    }

    CompletableFuture<List<PunishmentTrackInfo>> getTrackInfos( UUID uuid, String trackId, String server );

    CompletableFuture<Void> addToTrack( PunishmentTrackInfo punishmentTrackInfo );

    CompletableFuture<Void> resetTrack( UUID uuid, String trackId, String server );
}