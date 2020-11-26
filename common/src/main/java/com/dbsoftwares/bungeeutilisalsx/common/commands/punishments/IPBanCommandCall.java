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

package com.dbsoftwares.bungeeutilisalsx.common.commands.punishments;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.punishment.UserPunishmentFinishEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;

import java.util.List;

public class IPBanCommandCall extends PunishmentCommand
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final PunishmentCommand.PunishmentArgs punishmentArgs = loadArguments( user, args, false );

        if ( punishmentArgs == null )
        {
            user.sendLangMessage( "punishments.ipban.usage" + ( useServerPunishments() ? "-server" : "" ) );
            return;
        }
        if ( !punishmentArgs.hasJoined() )
        {
            user.sendLangMessage( "never-joined" );
            return;
        }
        final String reason = punishmentArgs.getReason();
        final UserStorage storage = punishmentArgs.getStorage();
        if ( dao().getPunishmentDao().getBansDao().isIPBanned( storage.getIp(), punishmentArgs.getServerOrAll() ) )
        {
            user.sendLangMessage( "punishments.ipban.already-banned" );
            return;
        }

        if ( punishmentArgs.launchEvent( PunishmentType.IPBAN ) )
        {
            return;
        }
        final IPunishmentExecutor executor = BuX.getApi().getPunishmentExecutor();

        final PunishmentInfo info = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getBansDao().insertIPBan(
                storage.getUuid(),
                storage.getUserName(),
                storage.getIp(),
                reason,
                punishmentArgs.getServerOrAll(),
                true,
                user.getName()
        );

        // Attempting to kick if player is online. If briding is enabled and player is not online, it will attempt to kick on other bungee's.
        super.attemptKick( storage, "punishments.ipban.kick", info );

        user.sendLangMessage( "punishments.ipban.executed", executor.getPlaceHolders( info ).toArray( new Object[0] ) );

        if ( !parameters.contains( "-s" ) )
        {
            if ( parameters.contains( "-nbp" ) )
            {
                BuX.getApi().langBroadcast(
                        "punishments.ipban.broadcast",
                        executor.getPlaceHolders( info ).toArray( new Object[]{} )
                );
            }
            else
            {
                BuX.getApi().langPermissionBroadcast(
                        "punishments.ipban.broadcast",
                        ConfigFiles.PUNISHMENTS.getConfig().getString( "commands.ipban.broadcast" ),
                        executor.getPlaceHolders( info ).toArray( new Object[]{} )
                );
            }
        }

        BuX.getApi().getEventLoader().launchEvent( new UserPunishmentFinishEvent(
                PunishmentType.IPBAN,
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