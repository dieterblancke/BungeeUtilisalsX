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

package com.dbsoftwares.bungeeutilisalsx.common.commands.friends.sub;

import com.dbsoftwares.bungeeutilisalsx.common.BuX;
import com.dbsoftwares.bungeeutilisalsx.common.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.api.friends.FriendSettingType;
import com.dbsoftwares.bungeeutilisalsx.common.api.friends.FriendUtils;
import com.dbsoftwares.bungeeutilisalsx.common.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisalsx.common.api.user.interfaces.User;

import java.util.List;
import java.util.Optional;

public class FriendAddSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 1 )
        {
            user.sendLangMessage( "friends.add.usage" );
            return;
        }
        final int friendLimit = FriendUtils.getFriendLimit( user );

        if ( user.getFriends().size() >= friendLimit )
        {
            user.sendLangMessage( "friends.add.limited", "{limit}", friendLimit );
            return;
        }
        final String name = args.get( 0 );
        final Dao dao = BuX.getApi().getStorageManager().getDao();

        if ( user.getName().equalsIgnoreCase( name ) )
        {
            user.sendLangMessage( "friends.add.selfadd" );
            return;
        }

        if ( user.getFriends().stream().anyMatch( data -> data.getFriend().equalsIgnoreCase( name ) ) )
        {
            user.sendLangMessage( "friends.add.already-friend", "{friend}", name );
            return;
        }

        final Optional<User> optionalTarget = BuX.getApi().getUser( name );
        final UserStorage storage;
        final boolean accepts;

        if ( optionalTarget.isPresent() )
        {
            final User target = optionalTarget.get();

            storage = target.getStorage();
            accepts = target.getFriendSettings().isRequests();
        }
        else
        {
            if ( !dao.getUserDao().exists( args.get( 0 ) ) )
            {
                user.sendLangMessage( "never-joined" );
                return;
            }

            storage = dao.getUserDao().getUserData( name );
            accepts = dao.getFriendsDao().getSetting( storage.getUuid(), FriendSettingType.REQUESTS );
        }

        if ( !accepts )
        {
            user.sendLangMessage( "friends.add.disallowed" );
            return;
        }

        if ( dao.getFriendsDao().hasOutgoingFriendRequest( user.getUuid(), storage.getUuid() ) )
        {
            user.sendLangMessage( "friends.add.already-requested", "{name}", name );
            return;
        }
        if ( dao.getFriendsDao().hasIncomingFriendRequest( user.getUuid(), storage.getUuid() ) )
        {
            user.sendLangMessage( "friends.add.use-accept-instead", "{name}", name );
            return;
        }

        dao.getFriendsDao().addFriendRequest( user.getUuid(), storage.getUuid() );
        user.sendLangMessage( "friends.add.request-sent", "{user}", name );

        optionalTarget.ifPresent( target -> target.sendLangMessage( "friends.request-received", "{name}", user.getName() ) );
    }
}
