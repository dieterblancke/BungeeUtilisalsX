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

package com.dbsoftwares.bungeeutilisals.commands.punishments.removal;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishRemoveEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;

import java.util.List;

public class UnbanCommand implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 1 )
        {
            user.sendLangMessage( "punishments.unban.usage" );
            return;
        }
        final Dao dao = BungeeUtilisals.getInstance().getDatabaseManagement().getDao();

        if ( !dao.getUserDao().exists( args.get( 0 ) ) )
        {
            user.sendLangMessage( "never-joined" );
            return;
        }
        final UserStorage storage = dao.getUserDao().getUserData( args.get( 0 ) );

        if ( !dao.getPunishmentDao().getBansDao().isBanned( storage.getUuid() ) )
        {
            user.sendLangMessage( "punishments.unban.not-banned" );
            return;
        }

        final UserPunishRemoveEvent event = new UserPunishRemoveEvent(
                UserPunishRemoveEvent.PunishmentRemovalAction.UNBAN, user, storage.getUuid(),
                storage.getUserName(), storage.getIp(), user.getServerName()
        );
        BUCore.getApi().getEventLoader().launchEvent( event );

        if ( event.isCancelled() )
        {
            user.sendLangMessage( "punishments.cancelled" );
            return;
        }

        final IPunishmentExecutor executor = BUCore.getApi().getPunishmentExecutor();
        dao.getPunishmentDao().getBansDao().removeCurrentBan( storage.getUuid(), user.getName() );

        final PunishmentInfo info = new PunishmentInfo();
        info.setUser( args.get( 0 ) );
        info.setId( "-1" );
        info.setExecutedBy( user.getName() );
        info.setRemovedBy( user.getName() );
        info.setServer( user.getServerName() );

        user.sendLangMessage( "punishments.unban.executed", executor.getPlaceHolders( info ).toArray( new Object[0] ) );

        if ( parameters.contains( "-nbp" ) )
        {
            BUCore.getApi().langBroadcast(
                    "punishments.unban.broadcast",
                    executor.getPlaceHolders( info ).toArray( new Object[]{} )
            );
        }
        else
        {
            BUCore.getApi().langPermissionBroadcast(
                    "punishments.unban.broadcast",
                    FileLocation.PUNISHMENTS.getConfiguration().getString( "commands.unban.broadcast" ),
                    executor.getPlaceHolders( info ).toArray( new Object[]{} )
            );
        }
    }
}