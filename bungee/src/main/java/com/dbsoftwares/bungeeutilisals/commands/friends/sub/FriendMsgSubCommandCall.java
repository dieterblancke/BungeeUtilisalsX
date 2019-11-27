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
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;

import java.util.Arrays;
import java.util.Optional;

public class FriendMsgSubCommandCall implements CommandCall
{

    @Override
    public void onExecute( User user, String[] args )
    {
        if ( args.length < 2 )
        {
            user.sendLangMessage( "friends.msg.usage" );
            return;
        }
        final String name = args[0];

        if ( user.getFriends().stream().noneMatch( data -> data.getFriend().equalsIgnoreCase( name ) ) )
        {
            user.sendLangMessage( "friends.msg.not-friend", "{user}", name );
            return;
        }

        if ( BUCore.getApi().getPlayerUtils().isOnline( name ) )
        {
            final Optional<User> optional = BUCore.getApi().getUser( name );
            final String message = String.join( " ", Arrays.copyOfRange( args, 1, args.length ) );

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

                {
                    String msgMessage = target.buildLangMessage( "friends.msg.format.receive" );
                    msgMessage = Utils.c( msgMessage );
                    msgMessage = msgMessage.replace( "{sender}", user.getName() );
                    msgMessage = msgMessage.replace( "{message}", message );

                    target.sendRawMessage( msgMessage );
                }
                {
                    String msgMessage = user.buildLangMessage( "friends.msg.format.send" );
                    msgMessage = Utils.c( msgMessage );
                    msgMessage = msgMessage.replace( "{receiver}", target.getName() );
                    msgMessage = msgMessage.replace( "{message}", message );

                    user.sendRawMessage( msgMessage );
                }
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
