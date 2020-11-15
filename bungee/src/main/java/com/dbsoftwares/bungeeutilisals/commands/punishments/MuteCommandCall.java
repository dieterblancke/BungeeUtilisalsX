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
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.punishment.UserPunishmentFinishEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.utils.config.ConfigFiles;

import java.util.List;

public class MuteCommandCall extends PunishmentCommand
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final PunishmentArgs punishmentArgs = loadArguments( user, args, false );

        if ( punishmentArgs == null )
        {
            user.sendLangMessage( "punishments.mute.usage" + ( useServerPunishments() ? "-server" : "" ) );
            return;
        }
        if ( !punishmentArgs.hasJoined() )
        {
            user.sendLangMessage( "never-joined" );
            return;
        }

        final String reason = punishmentArgs.getReason();
        final UserStorage storage = punishmentArgs.getStorage();
        if ( dao().getPunishmentDao().getMutesDao().isMuted( storage.getUuid(), punishmentArgs.getServerOrAll() ) )
        {
            user.sendLangMessage( "punishments.mute.already-muted" );
            return;
        }

        if ( punishmentArgs.launchEvent( PunishmentType.MUTE ) )
        {
            return;
        }
        final IPunishmentExecutor executor = BUCore.getApi().getPunishmentExecutor();
        final PunishmentInfo info = dao().getPunishmentDao().getMutesDao().insertMute(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                reason,
                punishmentArgs.getServerOrAll(),
                true,
                user.getName()
        );

        BUCore.getApi().getUser( storage.getUserName() ).ifPresent( muted ->
        {
            List<String> mute = null;
            if ( BUCore.getApi().getPunishmentExecutor().isTemplateReason( reason ) )
            {
                mute = BUCore.getApi().getPunishmentExecutor().searchTemplate(
                        muted.getLanguageConfig(), PunishmentType.MUTE, reason
                );
            }
            if ( mute == null )
            {
                muted.sendLangMessage( "punishments.mute.onmute", BUCore.getApi().getPunishmentExecutor().getPlaceHolders( info ).toArray() );
            }
            else
            {
                mute.forEach( str -> muted.sendRawColorMessage( BUCore.getApi().getPunishmentExecutor().setPlaceHolders( str, info ) ) );
            }
        } );

        user.sendLangMessage( "punishments.mute.executed", executor.getPlaceHolders( info ).toArray( new Object[0] ) );

        if ( !parameters.contains( "-s" ) )
        {
            if ( parameters.contains( "-nbp" ) )
            {
                BUCore.getApi().langBroadcast(
                        "punishments.mute.broadcast",
                        executor.getPlaceHolders( info ).toArray( new Object[]{} )
                );
            }
            else
            {
                BUCore.getApi().langPermissionBroadcast(
                        "punishments.mute.broadcast",
                        ConfigFiles.PUNISHMENTS.getConfig().getString( "commands.mute.broadcast" ),
                        executor.getPlaceHolders( info ).toArray( new Object[]{} )
                );
            }
        }

        BUCore.getApi().getEventLoader().launchEvent( new UserPunishmentFinishEvent(
                PunishmentType.MUTE,
                user,
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                reason,
                punishmentArgs.getServerOrAll(),
                null
        ) );
    }
}