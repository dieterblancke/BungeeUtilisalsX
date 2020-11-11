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
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.StaffUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;

import java.util.List;
import java.util.Optional;

public class FriendMsgSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 2 )
        {
            user.sendLangMessage( "friends.msg.usage" );
            return;
        }
        final String name = args.get( 0 );

        if ( user.getFriends().stream().noneMatch( data -> data.getFriend().equalsIgnoreCase( name ) ) )
        {
            user.sendLangMessage( "friends.msg.not-friend", "{user}", name );
            return;
        }

        if ( BUCore.getApi().getPlayerUtils().isOnline( name ) && !StaffUtils.isHidden( name ) )
        {
            final Optional<User> optional = BUCore.getApi().getUser( name );
            final String message = String.join( " ", args.subList( 1, args.size() ) );

            if ( optional.isPresent() )
            {
                final User target = optional.get();

                if ( !target.getFriendSettings().isMessages() )
                {
                    user.sendLangMessage( "friends.msg.disallowed" );
                    return;
                }

                user.getStorage().setData( "FRIEND_MSG_LAST_USER", target.getName() );
                target.getStorage().setData( "FRIEND_MSG_LAST_USER", user.getName() );


                target.sendLangMessage(
                        "friends.msg.format.receive",
                        false,
                        Utils::c,
                        null,
                        "{sender}", user.getName(),
                        "{message}", message
                );
                user.sendLangMessage(
                        "friends.msg.format.send",
                        false,
                        Utils::c,
                        null,
                        "{receiver}", target.getName(),
                        "{message}", message
                );
            }
            else
            {
                user.sendLangMessage( "offline" );
            }
        }
        else
        {
            user.sendLangMessage( "offline" );
        }
    }
}
