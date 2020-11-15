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
import com.dbsoftwares.bungeeutilisalsx.common.api.event.event.Event;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.punishment.UserPunishmentFinishEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.PunishmentAction;
import com.dbsoftwares.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisalsx.common.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisals.utils.ReportUtils;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ProxyServer;

import java.util.Date;
import java.util.List;

public class UserPunishExecutor implements EventExecutor
{

    @Event
    public void handleReports( UserPunishmentFinishEvent event )
    {
        ProxyServer.getInstance().getScheduler().runAsync( BUCore.getApi().getPlugin(), () ->
                ReportUtils.handleReportsFor( event.getExecutor().getName(), event.getUuid(), event.getType() )
        );
    }

    @Event
    public void updateMute( UserPunishmentFinishEvent event )
    {
        if ( event.isMute() )
        {
            event.getUser().ifPresent( user ->
            {
                if ( !user.getStorage().hasData( "CURRENT_MUTES" ) )
                {
                    user.getStorage().setData( "CURRENT_MUTES", Lists.newArrayList() );
                }
                final List<PunishmentInfo> mutes = user.getStorage().getData( "CURRENT_MUTES" );

                mutes.add( event.getInfo() );
            } );
        }
    }

    @Event
    public void executeActions( UserPunishmentFinishEvent event )
    {
        if ( !ConfigFiles.PUNISHMENTS.getPunishmentActions().containsKey( event.getType() ) )
        {
            return;
        }
        final List<PunishmentAction> actions = ConfigFiles.PUNISHMENTS.getPunishmentActions().get( event.getType() );

        for ( PunishmentAction action : actions )
        {
            final long amount;

            if ( event.isUserPunishment() )
            {
                // uuid involved
                amount = BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getPunishmentsSince(
                        event.getType(),
                        event.getUuid(),
                        new Date( System.currentTimeMillis() - action.getUnit().toMillis( action.getTime() ) )
                );
            }
            else
            {
                // ip involved
                amount = BUCore.getApi().getStorageManager().getDao().getPunishmentDao().getIPPunishmentsSince(
                        event.getType(),
                        event.getIp(),
                        new Date( System.currentTimeMillis() - action.getUnit().toMillis( action.getTime() ) )
                );
            }

            if ( amount >= action.getLimit() )
            {
                action.getActions().forEach( command ->
                        ProxyServer.getInstance().getPluginManager().dispatchCommand(
                                ProxyServer.getInstance().getConsole(),
                                command.replace( "%user%", event.getName() )
                        )
                );

                if ( event.isUserPunishment() )
                {
                    BUCore.getApi().getStorageManager().getDao().getPunishmentDao().updateActionStatus(
                            action.getLimit(),
                            event.getType(),
                            event.getUuid(),
                            new Date( System.currentTimeMillis() - action.getUnit().toMillis( action.getTime() ) )
                    );
                }
                else
                {
                    BUCore.getApi().getStorageManager().getDao().getPunishmentDao().updateIPActionStatus(
                            action.getLimit(),
                            event.getType(),
                            event.getIp(),
                            new Date( System.currentTimeMillis() - action.getUnit().toMillis( action.getTime() ) )
                    );
                }
                BUCore.getApi().getStorageManager().getDao().getPunishmentDao().savePunishmentAction(
                        event.getUuid(),
                        event.getName(),
                        event.getIp(),
                        action.getUid()
                );
            }
        }
    }
}