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

package be.dieterblancke.bungeeutilisalsx.common.commands.friends.sub;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.StaffUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;

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
        if ( BuX.getApi().getPlayerUtils().isOnline( name ) && !StaffUtils.isHidden( name ) )
        {
            final Optional<User> optional = BuX.getApi().getUser( name );
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

                target.sendLangMessage(
                        "friends.reply.format.receive",
                        false,
                        Utils::c,
                        null,
                        "{sender}", user.getName(),
                        "{message}", message
                );
                user.sendLangMessage(
                        "friends.reply.format.send",
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
