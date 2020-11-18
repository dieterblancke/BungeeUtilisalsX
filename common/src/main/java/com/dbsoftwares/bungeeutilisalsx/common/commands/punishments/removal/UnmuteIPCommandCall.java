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

package com.dbsoftwares.bungeeutilisalsx.common.commands.punishments.removal;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.punishment.UserPunishRemoveEvent.PunishmentRemovalAction;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.common.commands.punishments.PunishmentCommand;

import java.util.List;

public class UnmuteIPCommandCall extends PunishmentCommand
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        final PunishmentRemovalArgs punishmentRemovalArgs = loadRemovalArguments( user, args );

        if ( punishmentRemovalArgs == null )
        {
            user.sendLangMessage( "punishments.unmuteip.usage" + ( useServerPunishments() ? "-server" : "" ) );
            return;
        }
        if ( !punishmentRemovalArgs.hasJoined() )
        {
            user.sendLangMessage( "never-joined" );
            return;
        }
        final UserStorage storage = punishmentRemovalArgs.getStorage();
        if ( !dao().getPunishmentDao().getMutesDao().isIPMuted( storage.getIp(), punishmentRemovalArgs.getServerOrAll() ) )
        {
            user.sendLangMessage( "punishments.unmuteip.not-muted" );
            return;
        }

        if ( punishmentRemovalArgs.launchEvent( PunishmentRemovalAction.UNMUTEIP ) )
        {
            return;
        }

        final IPunishmentExecutor executor = BuX.getApi().getPunishmentExecutor();
        dao().getPunishmentDao().getMutesDao().removeCurrentIPMute(
                storage.getIp(),
                user.getName(),
                punishmentRemovalArgs.getServerOrAll()
        );

        final PunishmentInfo info = new PunishmentInfo();
        info.setUser( punishmentRemovalArgs.getPlayer() );
        info.setId( "-1" );
        info.setExecutedBy( user.getName() );
        info.setRemovedBy( user.getName() );
        info.setServer( punishmentRemovalArgs.getServerOrAll() );

        punishmentRemovalArgs.removeCachedMute();

        user.sendLangMessage( "punishments.unmuteip.executed", executor.getPlaceHolders( info ).toArray( new Object[0] ) );

        if ( !parameters.contains( "-s" ) )
        {
            if ( parameters.contains( "-nbp" ) )
            {
                BuX.getApi().langBroadcast(
                        "punishments.unmuteip.broadcast",
                        executor.getPlaceHolders( info ).toArray( new Object[]{} )
                );
            }
            else
            {
                BuX.getApi().langPermissionBroadcast(
                        "punishments.unmuteip.broadcast",
                        ConfigFiles.PUNISHMENTS.getConfig().getString( "commands.unmuteip.broadcast" ),
                        executor.getPlaceHolders( info ).toArray( new Object[]{} )
                );
            }
        }
    }
}