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

package com.dbsoftwares.bungeeutilisals.commands.friends.sub;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.api.friends.FriendData;
import com.dbsoftwares.bungeeutilisalsx.common.api.friends.FriendUtils;
import com.dbsoftwares.bungeeutilisalsx.common.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public class FriendAcceptSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 1 )
        {
            user.sendLangMessage( "friends.accept.usage" );
            return;
        }
        final int friendLimit = FriendUtils.getFriendLimit( user );

        if ( user.getFriends().size() >= friendLimit )
        {
            user.sendLangMessage( "friends.accept.limited", "{limit}", friendLimit );
            return;
        }
        final String name = args.get( 0 );
        final Dao dao = BUCore.getApi().getStorageManager().getDao();

        if ( user.getFriends().stream().anyMatch( data -> data.getFriend().equalsIgnoreCase( name ) ) )
        {
            user.sendLangMessage( "friends.accept.already-friend", "{friend}", name );
            return;
        }

        final Optional<User> optionalTarget = BUCore.getApi().getUser( name );
        final UserStorage storage;

        if ( optionalTarget.isPresent() )
        {
            storage = optionalTarget.get().getStorage();
        }
        else
        {
            if ( !dao.getUserDao().exists( args.get( 0 ) ) )
            {
                user.sendLangMessage( "never-joined" );
                return;
            }

            storage = dao.getUserDao().getUserData( name );
        }

        if ( !dao.getFriendsDao().hasIncomingFriendRequest( user.getUuid(), storage.getUuid() ) )
        {
            user.sendLangMessage( "friends.accept.no-request", "{user}", name );
            return;
        }

        dao.getFriendsDao().removeFriendRequest( user.getUuid(), storage.getUuid() );
        dao.getFriendsDao().addFriend( user.getUuid(), storage.getUuid() );
        dao.getFriendsDao().addFriend( storage.getUuid(), user.getUuid() );

        user.getFriends().add( new FriendData( storage.getUuid(), name, new Date(), storage.getLastLogout() ) );

        user.sendLangMessage( "friends.accept.accepted", "{user}", name );
        optionalTarget.ifPresent( target ->
        {
            target.sendLangMessage( "friends.accept.request-accepted", "{user}", user.getName() );
            target.getFriends().add( new FriendData( user.getUuid(), user.getName(), new Date(), user.getStorage().getLastLogout() ) );
        } );
    }
}
