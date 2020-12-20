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

package be.dieterblancke.bungeeutilisalsx.common.commands.general.message;

import be.dieterblancke.bungeeutilisalsx.common.BuX;
import be.dieterblancke.bungeeutilisalsx.common.api.command.CommandCall;
import be.dieterblancke.bungeeutilisalsx.common.api.user.interfaces.User;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.StaffUtils;
import be.dieterblancke.bungeeutilisalsx.common.api.utils.Utils;
import be.dieterblancke.bungeeutilisalsx.common.api.event.events.user.UserPrivateMessageEvent;

import java.util.List;
import java.util.Optional;

public class MsgCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( user.isMsgToggled() )
        {
            user.sendLangMessage( "general-commands.msgtoggle.pm-cmd-disabled" );
            return;
        }
        if ( args.size() < 2 )
        {
            user.sendLangMessage( "general-commands.msg.usage" );
            return;
        }
        final String name = args.get( 0 );

        if ( user.getName().equalsIgnoreCase( name ) )
        {
            user.sendLangMessage( "general-commands.msg.self-msg" );
            return;
        }

        if ( BuX.getApi().getPlayerUtils().isOnline( name ) && !StaffUtils.isHidden( name ) )
        {
            final Optional<User> optional = BuX.getApi().getUser( name );
            final String message = String.join( " ", args.subList( 1, args.size() ) );

            if ( optional.isPresent() )
            {
                final User target = optional.get();

                if ( target.isMsgToggled() )
                {
                    user.sendLangMessage( "general-commands.msgtoggle.not-receiving-pms" );
                    return;
                }

                if ( target.getStorage().getIgnoredUsers().stream().anyMatch( ignored -> ignored.equalsIgnoreCase( user.getName() ) ) )
                {
                    user.sendLangMessage( "general-commands.msg.ignored" );
                    return;
                }

                user.getStorage().setData( "MSG_LAST_USER", target.getName() );
                target.getStorage().setData( "MSG_LAST_USER", user.getName() );

                target.sendLangMessage(
                        "general-commands.msg.format.receive",
                        false,
                        Utils::c,
                        null,
                        "{sender}", user.getName(),
                        "{message}", message
                );
                user.sendLangMessage(
                        "general-commands.msg.format.send",
                        false,
                        Utils::c,
                        null,
                        "{receiver}", target.getName(),
                        "{message}", message
                );

                BuX.getApi().getEventLoader().launchEventAsync( new UserPrivateMessageEvent( user, target, message ) );
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
