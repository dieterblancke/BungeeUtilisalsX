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
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserLoadEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserServerConnectedEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserUnloadEvent;
import be.dieterblancke.bungeeutilisalsx.common.api.friends.FriendSetting;
import be.dieterblancke.bungeeutilisalsx.common.api.job.jobs.UserLanguageMessageJob;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.google.common.base.Strings;

public class FriendsExecutor implements EventExecutor
{

    @Event
    public void onLoad( final UserLoadEvent event )
    {
        final User user = event.getUser();
        BuX.getApi().getStorageManager().getDao().getFriendsDao().getIncomingFriendRequests( user.getUuid() )
                .thenAccept( requests ->
                {
                    if ( !requests.isEmpty() )
                    {
                        user.sendLangMessage( "friends.join.requests", "{amount}", requests.size() );
                    }
                } );
    }

    @Event
    public void onFriendJoin( final UserLoadEvent event )
    {
        final User user = event.getUser();

        if ( user.isVanished() )
        {
            return;
        }

        user.getFriends().forEach( data ->
        {
            if ( BuX.getApi().getPlayerUtils().isOnline( data.getFriend() ) )
            {
                BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                        data.getFriend(),
                        "friends.join.join",
                        "{user}", user.getName()
                ) );
            }
        } );
    }

    @Event
    public void onFriendLeave( final UserUnloadEvent event )
    {
        final User user = event.getUser();

        if ( user.isVanished() )
        {
            return;
        }

        user.getFriends().forEach( data ->
        {
            if ( BuX.getApi().getPlayerUtils().isOnline( data.getFriend() ) )
            {
                BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                        data.getFriend(),
                        "friends.leave",
                        "{user}", user.getName()
                ) );
            }
        } );
    }

    @Event
    public void onSwitch( final UserServerConnectedEvent event )
    {
        final User user = event.getUser();

        if ( user.isVanished() )
        {
            return;
        }

        if ( Strings.isNullOrEmpty( event.getTarget().getName() )
                || ConfigFiles.FRIENDS_CONFIG.isDisabledServerSwitch( event.getTarget().getName() ) )
        {
            return;
        }

        user.getFriends().forEach( data ->
        {
            if ( BuX.getApi().getPlayerUtils().isOnline( data.getFriend() ) )
            {
                BuX.getApi().getStorageManager().getDao().getFriendsDao().getSetting(
                        data.getUuid(),
                        FriendSetting.SERVER_SWITCH
                ).thenAccept( shouldSend ->
                {
                    if ( shouldSend )
                    {
                        BuX.getInstance().getJobManager().executeJob( new UserLanguageMessageJob(
                                data.getFriend(),
                                "friends.switch",
                                "{user}", user.getName(),
                                "{from}", user.getServerName(),
                                "{to}", event.getTarget().getName()
                        ) );
                    }
                } );
            }
        } );
    }
}
