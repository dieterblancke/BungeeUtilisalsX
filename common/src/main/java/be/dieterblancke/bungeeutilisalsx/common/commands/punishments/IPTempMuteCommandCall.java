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

package be.dieterblancke.bungeeutilisalsx.common.commands.punishments;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.IPunishmentHelper;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentType;
import be.dieterblancke.bungeeutilisalsx.common.api.user.UserStorage;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.List;

public class IPTempMuteCommandCall extends PunishmentCommand
{

    public IPTempMuteCommandCall()
    {
        super( "punishments.iptempmute", true );
    }

    @Override
    public void onPunishmentExecute( final User user, final List<String> args, final List<String> parameters, final PunishmentArgs punishmentArgs )
    {
        final String reason = punishmentArgs.getReason();
        final UserStorage storage = punishmentArgs.getStorage();
        final long time = punishmentArgs.getTime();

        if ( time == 0L )
        {
            user.sendLangMessage( "punishments.iptempmute.non-valid" );
            return;
        }
        if ( dao().getPunishmentDao().getMutesDao().isIPMuted( storage.getIp(), punishmentArgs.getServerOrAll() ) )
        {
            user.sendLangMessage( "punishments.iptempmute.already-muted" );
            return;
        }
        if ( punishmentArgs.launchEvent( PunishmentType.IPTEMPMUTE ) )
        {
            return;
        }
        final IPunishmentHelper executor = BuX.getApi().getPunishmentExecutor();
        final PunishmentInfo info = dao().getPunishmentDao().getMutesDao().insertTempIPMute(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                reason,
                punishmentArgs.getServerOrAll(),
                true,
                user.getName(),
                time
        );

        BuX.getApi().getUser( storage.getUserName() ).ifPresent( muted ->
        {
            List<String> mute = null;
            if ( BuX.getApi().getPunishmentExecutor().isTemplateReason( reason ) )
            {
                mute = BuX.getApi().getPunishmentExecutor().searchTemplate(
                        muted.getLanguageConfig().getConfig(), PunishmentType.IPTEMPMUTE, reason
                );
            }
            if ( mute == null )
            {
                mute = muted.getLanguageConfig().getConfig().getStringList( "punishments.iptempmute.onmute" );
            }

            mute.forEach( str -> muted.sendRawColorMessage( BuX.getApi().getPunishmentExecutor().setPlaceHolders( str, info ) ) );
        } );

        user.sendLangMessage( "punishments.iptempmute.executed", executor.getPlaceHolders( info ).toArray( new Object[0] ) );

        if ( !parameters.contains( "-s" ) )
        {
            if ( parameters.contains( "-nbp" ) )
            {
                BuX.getApi().langBroadcast(
                        "punishments.iptempmute.broadcast",
                        executor.getPlaceHolders( info ).toArray( new Object[]{} )
                );
            }
            else
            {
                BuX.getApi().langPermissionBroadcast(
                        "punishments.iptempmute.broadcast",
                        ConfigFiles.PUNISHMENT_CONFIG.getConfig().getString( "commands.iptempmute.broadcast" ),
                        executor.getPlaceHolders( info ).toArray( new Object[]{} )
                );
            }
        }

        punishmentArgs.launchPunishmentFinishEvent( PunishmentType.IPTEMPMUTE );
    }
}