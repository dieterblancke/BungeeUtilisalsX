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

package com.dbsoftwares.bungeeutilisalsx.common.executors;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.event.Event;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.user.UserLoadEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.user.UserServerConnectEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.event.events.user.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisalsx.common.api.friends.FriendRequest;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;

public class FriendsExecutor implements EventExecutor
{

    @Event
    public void onLoad( final UserLoadEvent event )
    {
        final User user = event.getUser();
        final List<FriendRequest> requests = BuX.getApi().getStorageManager().getDao()
                .getFriendsDao().getIncomingFriendRequests( user.getUuid() );

        if ( !requests.isEmpty() )
        {
            user.sendLangMessage( "friends.join.requests", "{amount}", requests.size() );
        }
    }

    @Event
    public void onFriendJoin( final UserLoadEvent event )
    {
        final User user = event.getUser();

        user.getFriends().forEach( data ->
                BuX.getApi().getUser( data.getUuid() ).ifPresent( friend ->
                        friend.sendLangMessage( "friends.join.join", "{user}", user.getName() ) ) );
    }

    @Event
    public void onFriendLeave( final UserUnloadEvent event )
    {
        final User user = event.getUser();

        user.getFriends().forEach( data ->
                BuX.getApi().getUser( data.getUuid() ).ifPresent( friend ->
                        friend.sendLangMessage( "friends.leave", "{user}", user.getName() ) ) );
    }

    @Event
    public void onSwitch( final UserServerConnectEvent event )
    {
        final User user = event.getUser();

        user.getFriends().forEach( data ->
                BuX.getApi().getUser( data.getUuid() ).ifPresent( friend ->
                        friend.sendLangMessage(
                                "friends.switch",
                                "{user}", user.getName(),
                                "{from}", user.getServerName(),
                                "{to}", event.getTarget().getName()
                        ) ) );
    }
}
