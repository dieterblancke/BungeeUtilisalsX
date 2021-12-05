package be.dieterblancke.bungeeutilisalsx.webapi.query;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendData;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.storage.dao.ApiTokenDao.ApiPermission;
import be.dieterblancke.bungeeutilisalsx.webapi.auth.RequiresPermission;
import be.dieterblancke.bungeeutilisalsx.webapi.caching.Cacheable;
import be.dieterblancke.bungeeutilisalsx.webapi.dto.*;
import be.dieterblancke.bungeeutilisalsx.webapi.service.UserService;
import com.google.common.base.Strings;
import graphql.kickstart.tools.GraphQLQueryResolver;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Query implements GraphQLQueryResolver
{

    private final UserService userService;

    @RequiresPermission( ApiPermission.FIND_USER )
    public User findUserByName( final String name )
    {
        return userService.findByName( name );
    }

    @RequiresPermission( ApiPermission.FIND_USER )
    public User findUserByUuid( final UUID uuid )
    {
        return userService.findByUuid( uuid );
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_FRIENDS )
    public List<Friend> findFriends( final UUID uuid )
    {
        final List<FriendData> friend = BuX.getApi().getStorageManager().getDao().getFriendsDao().getFriends( uuid );

        return friend
                .stream()
                .map( data -> Friend.of( uuid, data ) )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_FRIENDS )
    public List<FriendRequest> findFriendRequests( final UUID uuid, final FriendRequestType requestType )
    {
        final List<be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendRequest> requests;

        if ( requestType == FriendRequestType.INCOMING )
        {
            requests = BuX.getApi().getStorageManager().getDao().getFriendsDao().getIncomingFriendRequests( uuid );
        }
        else
        {
            requests = BuX.getApi().getStorageManager().getDao().getFriendsDao().getOutgoingFriendRequests( uuid );
        }

        return requests
                .stream()
                .map( FriendRequest::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_BAN )
    public Punishment findCurrentBan( final UUID uuid, final String server )
    {
        final PunishmentInfo punishmentInfo = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().getCurrentBan( uuid, server );

        return punishmentInfo.isLoaded() ? Punishment.of( punishmentInfo ) : null;
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_BAN )
    public Punishment findCurrentIpBan( final String ip, final String server )
    {
        final PunishmentInfo punishmentInfo = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().getCurrentIPBan( ip, server );

        return punishmentInfo.isLoaded() ? Punishment.of( punishmentInfo ) : null;
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_BAN )
    public List<Punishment> findAllBansFor( final UUID uuid, final String server )
    {
        final List<PunishmentInfo> bans;

        if ( Strings.isNullOrEmpty( server ) )
        {
            bans = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().getBans( uuid );
        }
        else
        {
            bans = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().getBans( uuid, server );
        }

        return bans
                .stream()
                .map( Punishment::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_BAN )
    public List<Punishment> findAllIpBansFor( final String ip, final String server )
    {
        final List<PunishmentInfo> ipBans;

        if ( Strings.isNullOrEmpty( server ) )
        {
            ipBans = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().getIPBans( ip );
        }
        else
        {
            ipBans = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().getIPBans( ip, server );
        }

        return ipBans
                .stream()
                .map( Punishment::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_BAN )
    public List<Punishment> findAllBansExecutedBy( final String name )
    {
        return BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().getBansExecutedBy( name )
                .stream()
                .map( Punishment::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_BAN )
    public Punishment findBanByPunishmentUid( final String uid )
    {
        final PunishmentInfo punishmentInfo = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().getByPunishmentId( uid );

        return punishmentInfo.isLoaded() ? Punishment.of( punishmentInfo ) : null;
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_BAN )
    public List<Punishment> findRecentBans( final int limit )
    {
        return BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().getRecentBans( limit )
                .stream()
                .map( Punishment::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_MUTE )
    public Punishment findCurrentMute( final UUID uuid, final String server )
    {
        final PunishmentInfo punishmentInfo = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().getCurrentMute( uuid, server );

        return punishmentInfo.isLoaded() ? Punishment.of( punishmentInfo ) : null;
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_MUTE )
    public Punishment findCurrentIpMute( final String ip, final String server )
    {
        final PunishmentInfo punishmentInfo = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().getCurrentIPMute( ip, server );

        return punishmentInfo.isLoaded() ? Punishment.of( punishmentInfo ) : null;
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_MUTE )
    public List<Punishment> findAllMutesFor( final UUID uuid, final String server )
    {
        final List<PunishmentInfo> mutes;

        if ( Strings.isNullOrEmpty( server ) )
        {
            mutes = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().getMutes( uuid );
        }
        else
        {
            mutes = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().getMutes( uuid, server );
        }

        return mutes
                .stream()
                .map( Punishment::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_MUTE )
    public List<Punishment> findAllIpMutesFor( final String ip, final String server )
    {
        final List<PunishmentInfo> ipMutes;

        if ( Strings.isNullOrEmpty( server ) )
        {
            ipMutes = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().getIPMutes( ip );
        }
        else
        {
            ipMutes = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().getIPMutes( ip, server );
        }

        return ipMutes
                .stream()
                .map( Punishment::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_MUTE )
    public List<Punishment> findAllMutesExecutedBy( final String name )
    {
        return BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().getMutesExecutedBy( name )
                .stream()
                .map( Punishment::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_MUTE )
    public Punishment findMuteByPunishmentUid( final String uid )
    {
        final PunishmentInfo punishmentInfo = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().getByPunishmentId( uid );

        return punishmentInfo.isLoaded() ? Punishment.of( punishmentInfo ) : null;
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_MUTE )
    public List<Punishment> findRecentMutes( final int limit )
    {
        return BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao().getRecentMutes( limit )
                .stream()
                .map( Punishment::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_TRACK_DATA )
    public List<TrackData> findPunishmentTrackData( final UUID uuid, final String trackId, final String server )
    {
        return BuX.getApi().getStorageManager().getDao().getPunishmentDao().getTracksDao().getTrackInfos( uuid, trackId, server )
                .stream()
                .map( TrackData::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_KICK )
    public List<Punishment> findAllKicksFor( final UUID uuid )
    {
        return BuX.getApi().getStorageManager().getDao().getPunishmentDao().getKickAndWarnDao().getKicks( uuid )
                .stream()
                .map( Punishment::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_KICK )
    public List<Punishment> findAllKicksExecutedBy( final String name )
    {
        return BuX.getApi().getStorageManager().getDao().getPunishmentDao().getKickAndWarnDao().getKicksExecutedBy( name )
                .stream()
                .map( Punishment::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_WARN )
    public List<Punishment> findAllWarnsFor( final UUID uuid )
    {
        return BuX.getApi().getStorageManager().getDao().getPunishmentDao().getKickAndWarnDao().getWarns( uuid )
                .stream()
                .map( Punishment::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @RequiresPermission( ApiPermission.FIND_WARN )
    public List<Punishment> findAllWarnsExecutedBy( final String name )
    {
        return BuX.getApi().getStorageManager().getDao().getPunishmentDao().getKickAndWarnDao().getWarnsExecutedBy( name )
                .stream()
                .map( Punishment::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @SneakyThrows
    @RequiresPermission( ApiPermission.FIND_REPORT )
    public List<Report> findAllReports()
    {
        return BuX.getApi().getStorageManager().getDao().getReportsDao().getReports()
                .get()
                .stream()
                .map( Report::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @SneakyThrows
    @RequiresPermission( ApiPermission.FIND_REPORT )
    public List<Report> findActiveReports()
    {
        return BuX.getApi().getStorageManager().getDao().getReportsDao().getActiveReports()
                .get()
                .stream()
                .map( Report::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @SneakyThrows
    @RequiresPermission( ApiPermission.FIND_REPORT )
    public List<Report> findHandledReports()
    {
        return BuX.getApi().getStorageManager().getDao().getReportsDao().getHandledReports()
                .get()
                .stream()
                .map( Report::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @SneakyThrows
    @RequiresPermission( ApiPermission.FIND_REPORT )
    public List<Report> findAcceptedReports()
    {
        return BuX.getApi().getStorageManager().getDao().getReportsDao().getAcceptedReports()
                .get()
                .stream()
                .map( Report::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @SneakyThrows
    @RequiresPermission( ApiPermission.FIND_REPORT )
    public List<Report> findDeniedReports()
    {
        return BuX.getApi().getStorageManager().getDao().getReportsDao().getDeniedReports()
                .get()
                .stream()
                .map( Report::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @SneakyThrows
    @RequiresPermission( ApiPermission.FIND_REPORT )
    public List<Report> findRecentReports( final int days )
    {
        return BuX.getApi().getStorageManager().getDao().getReportsDao().getRecentReports( days )
                .get()
                .stream()
                .map( Report::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @SneakyThrows
    @RequiresPermission( ApiPermission.FIND_REPORT )
    public List<Report> findReportsFor( final UUID uuid )
    {
        return BuX.getApi().getStorageManager().getDao().getReportsDao().getReports( uuid )
                .get()
                .stream()
                .map( Report::of )
                .collect( Collectors.toList() );
    }

    @Cacheable
    @SneakyThrows
    @RequiresPermission( ApiPermission.FIND_REPORT )
    public List<Report> findReportsBy( final String name )
    {
        return BuX.getApi().getStorageManager().getDao().getReportsDao().getReportsHistory( name )
                .get()
                .stream()
                .map( Report::of )
                .collect( Collectors.toList() );
    }
}
