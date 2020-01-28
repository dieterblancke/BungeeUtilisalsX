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

package com.dbsoftwares.bungeeutilisals.commands.punishments;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishmentFinishEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;

import java.util.List;

public class IPBanCommand implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 2 )
        {
            user.sendLangMessage( "punishments.ipban.usage" );
            return;
        }
        final Dao dao = BUCore.getApi().getStorageManager().getDao();
        final String reason = Utils.formatList( args.subList( 1, args.size() ), " " );

        if ( !dao.getUserDao().exists( args.get( 0 ) ) )
        {
            user.sendLangMessage( "never-joined" );
            return;
        }
        final UserStorage storage = dao.getUserDao().getUserData( args.get( 0 ) );
        if ( dao.getPunishmentDao().getBansDao().isIPBanned( storage.getIp() ) )
        {
            user.sendLangMessage( "punishments.ipban.already-banned" );
            return;
        }

        final UserPunishEvent event = new UserPunishEvent( PunishmentType.IPBAN, user, storage.getUuid(),
                storage.getUserName(), storage.getIp(), reason, user.getServerName(), null );
        BUCore.getApi().getEventLoader().launchEvent( event );

        if ( event.isCancelled() )
        {
            user.sendLangMessage( "punishments.cancelled" );
            return;
        }
        final IPunishmentExecutor executor = BUCore.getApi().getPunishmentExecutor();

        final PunishmentInfo info = dao.getPunishmentDao().getBansDao().insertIPBan(
                storage.getUuid(), storage.getUserName(), storage.getIp(), reason, user.getServerName(), true, user.getName()
        );

        BUCore.getApi().getUser( storage.getUserName() ).ifPresent( banned ->
        {
            String kick = null;
            if ( BUCore.getApi().getPunishmentExecutor().isTemplateReason( reason ) )
            {
                kick = Utils.formatList( BUCore.getApi().getPunishmentExecutor().searchTemplate(
                        banned.getLanguageConfig(), PunishmentType.IPBAN, reason
                ), "\n" );
            }
            if ( kick == null )
            {
                kick = Utils.formatList( banned.getLanguageConfig().getStringList( "punishments.ipban.kick" ), "\n" );
            }
            kick = executor.setPlaceHolders( kick, info );

            banned.kick( kick );
        } );

        user.sendLangMessage( "punishments.ipban.executed", executor.getPlaceHolders( info ).toArray( new Object[0] ) );

        if ( parameters.contains( "-nbp" ) )
        {
            BUCore.getApi().langBroadcast(
                    "punishments.ipban.broadcast",
                    executor.getPlaceHolders( info ).toArray( new Object[]{} )
            );
        }
        else
        {
            BUCore.getApi().langPermissionBroadcast(
                    "punishments.ipban.broadcast",
                    FileLocation.PUNISHMENTS.getConfiguration().getString( "commands.ipban.broadcast" ),
                    executor.getPlaceHolders( info ).toArray( new Object[]{} )
            );
        }

        BUCore.getApi().getEventLoader().launchEvent( new UserPunishmentFinishEvent(
                PunishmentType.IPBAN, user, storage.getUuid(),
                storage.getUserName(), storage.getIp(), reason, user.getServerName(), null
        ) );
    }
}