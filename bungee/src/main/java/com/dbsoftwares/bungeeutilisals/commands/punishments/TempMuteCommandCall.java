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
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishmentFinishEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.config.ConfigFiles;

import java.util.List;

public class TempMuteCommandCall extends PunishmentCommand
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final PunishmentArgs punishmentArgs = loadArguments( user, args, true );

        if ( punishmentArgs == null )
        {
            user.sendLangMessage( "punishments.tempmute.usage" + ( useServerPunishments() ? "-server" : "" ) );
            return;
        }
        if ( !punishmentArgs.hasJoined() )
        {
            user.sendLangMessage( "never-joined" );
            return;
        }

        final String reason = punishmentArgs.getReason();
        final UserStorage storage = punishmentArgs.getStorage();
        final long time = punishmentArgs.getTime();

        if ( time == 0L )
        {
            user.sendLangMessage( "punishments.tempmute.non-valid" );
            return;
        }
        if ( dao().getPunishmentDao().getBansDao().isBanned( storage.getUuid(), punishmentArgs.getServerOrAll() ) )
        {
            user.sendLangMessage( "punishments.tempmute.already-muted" );
            return;
        }
        if ( punishmentArgs.launchEvent( PunishmentType.TEMPMUTE ) )
        {
            return;
        }
        final IPunishmentExecutor executor = BUCore.getApi().getPunishmentExecutor();
        final PunishmentInfo info = dao().getPunishmentDao().getMutesDao().insertTempMute(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                reason,
                punishmentArgs.getServerOrAll(),
                true,
                user.getName(),
                time
        );

        BUCore.getApi().getUser( storage.getUserName() ).ifPresent( muted ->
        {
            List<String> mute = null;
            if ( BUCore.getApi().getPunishmentExecutor().isTemplateReason( reason ) )
            {
                mute = BUCore.getApi().getPunishmentExecutor().searchTemplate(
                        muted.getLanguageConfig(), PunishmentType.TEMPMUTE, reason
                );
            }
            if ( mute == null )
            {
                mute = muted.getLanguageConfig().getStringList( "punishments.tempmute.onmute" );
            }

            mute.forEach( str -> muted.sendRawColorMessage( BUCore.getApi().getPunishmentExecutor().setPlaceHolders( str, info ) ) );
        } );

        user.sendLangMessage( "punishments.tempmute.executed", executor.getPlaceHolders( info ).toArray( new Object[0] ) );

        if ( !parameters.contains( "-s" ) )
        {
            if ( parameters.contains( "-nbp" ) )
            {
                BUCore.getApi().langBroadcast(
                        "punishments.tempmute.broadcast",
                        executor.getPlaceHolders( info ).toArray( new Object[]{} )
                );
            }
            else
            {
                BUCore.getApi().langPermissionBroadcast(
                        "punishments.tempmute.broadcast",
                        ConfigFiles.PUNISHMENTS.getConfig().getString( "commands.tempmute.broadcast" ),
                        executor.getPlaceHolders( info ).toArray( new Object[]{} )
                );
            }
        }

        BUCore.getApi().getEventLoader().launchEvent( new UserPunishmentFinishEvent(
                PunishmentType.TEMPMUTE,
                user,
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                reason,
                punishmentArgs.getServerOrAll(),
                time
        ) );
    }
}