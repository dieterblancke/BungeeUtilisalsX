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
import com.dbsoftwares.bungeeutilisals.api.friends.FriendUtils;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;

import java.util.Optional;

public class FriendRemoveSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( User user, String[] args )
    {
        if ( args.length < 1 )
        {
            user.sendLangMessage( "friends.remove.usage" );
            return;
        }
        final String name = args[0];
        final int friendLimit = FriendUtils.getFriendsLimit( user );
        final Dao dao = BUCore.getApi().getStorageManager().getDao();

        if ( user.getFriends().stream().noneMatch( data -> data.getFriend().equalsIgnoreCase( name ) ) )
        {
            user.sendLangMessage( "friends.remove.no-friend", "{user}", name );
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
            if ( !dao.getUserDao().exists( args[0] ) )
            {
                user.sendLangMessage( "never-joined" );
                return;
            }

            storage = dao.getUserDao().getUserData( name );
        }

        dao.getFriendsDao().removeFriend( user.getUuid(), storage.getUuid() );
        dao.getFriendsDao().removeFriend( storage.getUuid(), user.getUuid() );

        user.getFriends().removeIf( data -> data.getFriend().equalsIgnoreCase( name ) );

        user.sendLangMessage( "friends.remove.removed", "{user}", name );
        optionalTarget.ifPresent( target ->
        {
            target.getFriends().removeIf( data -> data.getFriend().equalsIgnoreCase( user.getName() ) );
            target.sendLangMessage( "friends.remove.friend-removed" );
        } );
    }
}
