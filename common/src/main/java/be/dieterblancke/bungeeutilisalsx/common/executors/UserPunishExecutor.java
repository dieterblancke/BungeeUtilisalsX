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

package be.dieterblancke.bungeeutilisalsx.common.executors;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.Event;
import be.dieterblancke.bungeeutilisalsx.common.api.event.event.EventExecutor;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.punishment.UserPunishmentFinishEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentAction;
import be.dieterblancke.bungeeutilisalsx.common.api.punishments.PunishmentInfo;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.ReportUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.TrackUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.google.common.collect.Lists;

import java.util.Date;
import java.util.List;

public class UserPunishExecutor implements EventExecutor
{

    @Event
    public void handleReports( UserPunishmentFinishEvent event )
    {
        BuX.getInstance().getScheduler().runAsync( () ->
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
        if ( !ConfigFiles.PUNISHMENT_ACTIONS.getPunishmentActions().containsKey( event.getType() ) )
        {
            return;
        }
        final List<PunishmentAction> actions = ConfigFiles.PUNISHMENT_ACTIONS.getPunishmentActions().get( event.getType() );

        for ( PunishmentAction action : actions )
        {
            final long amount;

            if ( event.isUserPunishment() )
            {
                // uuid involved
                amount = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getPunishmentsSince(
                        event.getType(),
                        event.getUuid(),
                        new Date( System.currentTimeMillis() - action.getUnit().toMillis( action.getTime() ) )
                );
            }
            else
            {
                // ip involved
                amount = BuX.getApi().getStorageManager().getDao().getPunishmentDao().getIPPunishmentsSince(
                        event.getType(),
                        event.getIp(),
                        new Date( System.currentTimeMillis() - action.getUnit().toMillis( action.getTime() ) )
                );
            }

            if ( amount >= action.getLimit() )
            {
                action.getActions().forEach( command ->
                        BuX.getApi().getConsoleUser().executeCommand( command.replace( "%user%", event.getName() ) )
                );

                if ( event.isUserPunishment() )
                {
                    BuX.getApi().getStorageManager().getDao().getPunishmentDao().updateActionStatus(
                            action.getLimit(),
                            event.getType(),
                            event.getUuid(),
                            new Date( System.currentTimeMillis() - action.getUnit().toMillis( action.getTime() ) )
                    );
                }
                else
                {
                    BuX.getApi().getStorageManager().getDao().getPunishmentDao().updateIPActionStatus(
                            action.getLimit(),
                            event.getType(),
                            event.getIp(),
                            new Date( System.currentTimeMillis() - action.getUnit().toMillis( action.getTime() ) )
                    );
                }
                BuX.getApi().getStorageManager().getDao().getPunishmentDao().savePunishmentAction(
                        event.getUuid(),
                        event.getName(),
                        event.getIp(),
                        action.getUid()
                );
            }
        }
    }

    @Event
    public void countForTrack( final UserPunishmentFinishEvent event )
    {
        TrackUtils.addTrackPunishment( event.getUuid(), event.getInfo() );
    }
}