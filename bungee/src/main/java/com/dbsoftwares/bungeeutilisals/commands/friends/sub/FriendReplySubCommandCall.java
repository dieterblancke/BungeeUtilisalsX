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

import java.util.List;
import java.util.Optional;

public class FriendReplySubCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() < 1 )
        {
            user.sendLangMessage( "friends.reply.usage" );
            return;
        }
        if ( !user.getStorage().hasData( "FRIEND_MSG_LAST_USER" ) )
        {
            user.sendLangMessage( "friends.reply.no-target" );
            return;
        }

        final String name = user.getStorage().getData( "FRIEND_MSG_LAST_USER" );
        if ( user.getFriends().stream().noneMatch( data -> data.getFriend().equalsIgnoreCase( name ) ) )
        {
            user.sendLangMessage( "friends.reply.not-friend", "{user}", name );
            return;
        }
        if ( BUCore.getApi().getPlayerUtils().isOnline( name ) )
        {
            final Optional<User> optional = BUCore.getApi().getUser( name );
            final String message = String.join( " ", args );

            if ( optional.isPresent() )
            {
                final User target = optional.get();

                if ( !target.getFriendSettings().isMessages() )
                {
                    user.sendLangMessage( "friends.reply.disallowed" );
                    return;
                }

                // only needs to be set for target, as the current user (sender) still has this target as last user
                target.getStorage().setData( "FRIEND_MSG_LAST_USER", user.getName() );

                {
                    String msgMessage = target.buildLangMessage( "friends.reply.format.receive" );
                    msgMessage = Utils.c( msgMessage );
                    msgMessage = msgMessage.replace( "{sender}", user.getName() );
                    msgMessage = msgMessage.replace( "{message}", message );

                    target.sendRawMessage( msgMessage );
                }
                {
                    String msgMessage = user.buildLangMessage( "friends.reply.format.send" );
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
