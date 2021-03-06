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

package com.dbsoftwares.bungeeutilisals.executors;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.event.Priority;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserCommandEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.MutesDao;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;
import com.google.common.collect.Lists;

import java.util.List;

import static com.dbsoftwares.bungeeutilisals.api.storage.dao.punishments.BansDao.useServerPunishments;

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
        if ( checkTemporaryMute( user, info ) )
        {
            return;
        }

        if ( ConfigFiles.PUNISHMENTS.getConfig().getStringList( "blocked-mute-commands" )
                .contains( event.getActualCommand().replaceFirst( "/", "" ) ) )
        {

            user.sendLangMessage( "punishments." + info.getType().toString().toLowerCase() + ".onmute",
                    event.getApi().getPunishmentExecutor().getPlaceHolders( info ).toArray( new Object[]{} ) );
            event.setCancelled( true );
        }
    }

    // high priority
    @Event(priority = Priority.HIGHEST)
    public void onChat( UserChatEvent event )
    {
        final User user = event.getUser();

        if ( !isMuted( user, user.getServerName() ) )
        {
            return;
        }
        final PunishmentInfo info = getCurrentMuteForUser( user, user.getServerName() );
        if ( checkTemporaryMute( user, info ) )
        {
            return;
        }

        user.sendLangMessage( "punishments." + info.getType().toString().toLowerCase() + ".onmute",
                event.getApi().getPunishmentExecutor().getPlaceHolders( info ).toArray( new Object[]{} ) );
        event.setCancelled( true );
    }

    private boolean checkTemporaryMute( final User user, final PunishmentInfo info )
    {
        if ( info.isExpired() )
        {
            final MutesDao mutesDao = BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao();

            if ( info.getType().equals( PunishmentType.TEMPMUTE ) )
            {
                mutesDao.removeCurrentMute( user.getParent().getUniqueId(), "CONSOLE", info.getServer() );
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
        if ( !user.getStorage().hasData( "CURRENT_MUTES" ) )
        {
            // mutes seem to not have loaded yet, loading them now ...
            final MutesDao dao = BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getMutesDao();
            final List<PunishmentInfo> mutes = Lists.newArrayList();

            mutes.addAll( dao.getActiveMutes( user.getUuid() ) );
            mutes.addAll( dao.getActiveIPMutes( user.getIp() ) );

            user.getStorage().setData( "CURRENT_MUTES", mutes );
        }
        final List<PunishmentInfo> mutes = user.getStorage().getData( "CURRENT_MUTES" );
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