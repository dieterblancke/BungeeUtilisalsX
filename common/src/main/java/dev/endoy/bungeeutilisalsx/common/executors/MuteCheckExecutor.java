package dev.endoy.bungeeutilisalsx.common.executors;

import dev.endoy.bungeeutilisalsx.common.BuX;
import dev.endoy.bungeeutilisalsx.common.api.event.event.Event;
import dev.endoy.bungeeutilisalsx.common.api.event.event.EventExecutor;
import dev.endoy.bungeeutilisalsx.common.api.event.event.Priority;
import dev.endoy.bungeeutilisalsx.common.api.event.events.user.UserChatEvent;
import dev.endoy.bungeeutilisalsx.common.api.event.events.user.UserCommandEvent;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import dev.endoy.bungeeutilisalsx.common.api.punishments.PunishmentType;
import dev.endoy.bungeeutilisalsx.common.api.storage.dao.punishments.MutesDao;
import dev.endoy.bungeeutilisalsx.common.api.user.UserStorageKey;
import dev.endoy.bungeeutilisalsx.common.api.user.interfaces.User;
import dev.endoy.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.google.common.collect.Lists;

import java.util.List;

import static dev.endoy.bungeeutilisalsx.common.api.storage.dao.PunishmentDao.useServerPunishments;

public class MuteCheckExecutor implements EventExecutor
{

    @Event
    public void onCommand( UserCommandEvent event )
    {
        final User user = event.getUser();

        if ( !isMuted( user, user.getServerName() ) )
        {
            return;
        }
        final PunishmentInfo info = getCurrentMuteForUser( user, user.getServerName() );
        if ( info == null || checkTemporaryMute( user, info ) )
        {
            return;
        }

        if ( ConfigFiles.PUNISHMENT_CONFIG.getConfig().getStringList( "blocked-mute-commands" )
            .contains( event.getActualCommand().replaceFirst( "/", "" ) ) )
        {

            user.sendLangMessage(
                "punishments." + info.getType().toString().toLowerCase() + ".onmute",
                event.getApi().getPunishmentExecutor().getPlaceHolders( info )
            );
            event.setCancelled( true );
        }
    }

    // high priority
    @Event( priority = Priority.HIGHEST )
    public void onChat( UserChatEvent event )
    {
        final User user = event.getUser();

        if ( !isMuted( user, user.getServerName() ) )
        {
            return;
        }
        final PunishmentInfo info = getCurrentMuteForUser( user, user.getServerName() );
        if ( info == null || checkTemporaryMute( user, info ) )
        {
            return;
        }

        user.sendLangMessage(
            "punishments." + info.getType().toString().toLowerCase() + ".onmute",
            event.getApi().getPunishmentExecutor().getPlaceHolders( info )
        );
        event.setCancelled( true );
    }

    private boolean checkTemporaryMute( final User user, final PunishmentInfo info )
    {
        if ( info.isExpired() )
        {
            final MutesDao mutesDao = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao();

            if ( info.getType().equals( PunishmentType.TEMPMUTE ) )
            {
                mutesDao.removeCurrentMute( user.getUuid(), "CONSOLE", info.getServer() );
            }
            else
            {
                mutesDao.removeCurrentIPMute( user.getIp(), "CONSOLE", info.getServer() );
            }
            return true;
        }
        return false;
    }

    private boolean isMuted( final User user, final String server )
    {
        return getCurrentMuteForUser( user, server ) != null;
    }

    private PunishmentInfo getCurrentMuteForUser( final User user, final String server )
    {
        if ( !user.getStorage().hasData( UserStorageKey.CURRENT_MUTES ) )
        {
            // mutes seem to not have loaded yet, loading them now ...
            final MutesDao dao = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao();
            final List<PunishmentInfo> mutes = Lists.newArrayList();

            mutes.addAll( dao.getActiveMutes( user.getUuid() ).join() );
            mutes.addAll( dao.getActiveIPMutes( user.getIp() ).join() );

            user.getStorage().setData( UserStorageKey.CURRENT_MUTES, mutes );
        }
        final List<PunishmentInfo> mutes = user.getStorage().getData( UserStorageKey.CURRENT_MUTES );
        if ( mutes.isEmpty() )
        {
            return null;
        }

        if ( useServerPunishments() )
        {
            return mutes.stream()
                .filter( mute -> mute.getServer().equalsIgnoreCase( "ALL" ) || mute.getServer().equalsIgnoreCase( server ) )
                .findAny()
                .orElse( null );
        }
        else
        {
            return mutes.get( 0 );
        }
    }
}