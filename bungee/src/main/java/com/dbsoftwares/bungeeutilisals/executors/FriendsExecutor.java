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

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserLoadEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserUnloadEvent;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendRequest;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;
import java.util.Optional;

public class FriendsExecutor implements EventExecutor, Listener
{

    @Event
    public void onLoad( final UserLoadEvent event )
    {
        final User user = event.getUser();
        final List<FriendRequest> requests = BUCore.getApi().getStorageManager().getDao()
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
                BUCore.getApi().getUser( data.getUuid() ).ifPresent( friend ->
                        friend.sendLangMessage( "friends.join.join", "{user}", user.getName() ) ) );
    }

    @Event
    public void onFriendLeave( final UserUnloadEvent event )
    {
        final User user = event.getUser();

        user.getFriends().forEach( data ->
                BUCore.getApi().getUser( data.getUuid() ).ifPresent( friend ->
                        friend.sendLangMessage( "friends.leave", "{user}", user.getName() ) ) );
    }

    @Event
    public void onUserServerSwitch( final UserServerSwitchEvent event )
    {
        final User user = event.getUser();

        user.getFriends().forEach( data ->
                BUCore.getApi().getUser( data.getUuid() ).ifPresent( friend ->
                        friend.sendLangMessage( "friends.switch", "{user}", user.getName() ) ) );
    }

    @EventHandler
    public void onSwitch( final ServerConnectEvent event )
    {
        final ProxiedPlayer player = event.getPlayer();
        final Optional<User> optional = BungeeUtilisals.getApi().getUser( player );

        if ( !optional.isPresent() || player.getServer() == null )
        {
            return;
        }
        final User user = optional.get();

        user.getFriends().forEach( data ->
                BUCore.getApi().getUser( data.getUuid() ).ifPresent( friend ->
                        friend.sendLangMessage(
                                "friends.switch",
                                "{user}", user.getName(),
                                "{from}", user.getServerName(),
                                "{to}", event.getTarget().getName()
                        ) ) );
    }
}
