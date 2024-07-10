package dev.endoy.bungeeutilisalsx.common.api.storage.dao;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentType;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments.BansDao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments.KickAndWarnDao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments.MutesDao;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments.TracksDao;
import dev.endoy.bungeeutilisalsx.common.api.utils.Validate;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface PunishmentDao
{

    static String getPunishmentIdCharacters()
    {
        return ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "puid-characters" );
    }

    static PunishmentInfo buildPunishmentInfo( final String id, final PunishmentType type, final UUID uuid, final String user,
                                               final String ip, final String reason, final String server,
                                               final String executedby, final Date date, final long time,
                                               final boolean active, final String removedby, final String punishmentUid )
    {
        final PunishmentInfo info = new PunishmentInfo();

        info.setId( id );
        info.setUuid( uuid );
        info.setUser( user );
        info.setIp( ip );
        info.setReason( reason );
        info.setServer( server );
        info.setExecutedBy( executedby );
        info.setDate( date );
        info.setType( type );

        info.setExpireTime( time );
        info.setActive( active );
        Validate.ifNotNull( removedby, info::setRemovedBy );
        info.setPunishmentUid( punishmentUid );

        return info;
    }

    static PunishmentInfo buildPunishmentInfo( final int id, final PunishmentType type, final UUID uuid, final String user,
                                               final String ip, final String reason, final String server,
                                               final String executedby, final Date date, final long time,
                                               final boolean active, final String removedby, final String punishmentUid )
    {
        return buildPunishmentInfo( String.valueOf( id ), type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, punishmentUid );
    }

    static PunishmentInfo buildPunishmentInfo( final String id, final PunishmentType type, final UUID uuid, final String user,
                                               final String ip, final String reason, final String server,
                                               final String executedby, final Date date, final long time,
                                               final boolean active, final String removedby )
    {
        return buildPunishmentInfo( id, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, null );
    }

    static PunishmentInfo buildPunishmentInfo( final int id, final PunishmentType type, final UUID uuid, final String user,
                                               final String ip, final String reason, final String server,
                                               final String executedby, final Date date, final long time,
                                               final boolean active, final String removedby )
    {
        return buildPunishmentInfo( String.valueOf( id ), type, uuid, user, ip, reason, server, executedby, date, time, active, removedby );
    }

    static PunishmentInfo buildPunishmentInfo( final PunishmentType type, final UUID uuid, final String user,
                                               final String ip, final String reason, final String server,
                                               final String executedby, final Date date, final long time,
                                               final boolean active, final String removedby )
    {
        return buildPunishmentInfo( -1, type, uuid, user, ip, reason, server, executedby, date, time, active, removedby );
    }

    static PunishmentInfo buildPunishmentInfo( final PunishmentType type, final UUID uuid, final String user,
                                               final String ip, final String reason, final String server,
                                               final String executedby, final Date date, final long time,
                                               final boolean active, final String removedby, final String punishmentUid )
    {
        return buildPunishmentInfo( String.valueOf( -1 ), type, uuid, user, ip, reason, server, executedby, date, time, active, removedby, punishmentUid );
    }

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

    BansDao getBansDao();

    MutesDao getMutesDao();

    KickAndWarnDao getKickAndWarnDao();

    TracksDao getTracksDao();

    CompletableFuture<Long> getPunishmentsSince( PunishmentType type, UUID uuid, Date date );

    CompletableFuture<Long> getIPPunishmentsSince( PunishmentType type, String ip, Date date );

    CompletableFuture<Void> updateActionStatus( int limit, PunishmentType type, UUID uuid, Date date );

    CompletableFuture<Void> updateIPActionStatus( int limit, PunishmentType type, String ip, Date date );

    CompletableFuture<Void> savePunishmentAction( UUID uuid, String username, String ip, String uid );

    default CompletableFuture<Integer> softDeleteSince( final String user, final String removedBy, final Date time )
    {
        return CompletableFuture.supplyAsync(
                () -> getBansDao().softDeleteSince( user, removedBy, time ).join()
                        + getMutesDao().softDeleteSince( user, removedBy, time ).join(),
                BuX.getInstance().getScheduler().getExecutorService()
        );
    }

    default CompletableFuture<Integer> hardDeleteSince( final String user, final Date time )
    {
        return CompletableFuture.supplyAsync(
                () -> getBansDao().hardDeleteSince( user, time ).join()
                        + getMutesDao().hardDeleteSince( user, time ).join(),
                BuX.getInstance().getScheduler().getExecutorService()
        );
    }
}
