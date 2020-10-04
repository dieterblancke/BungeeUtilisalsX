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

package com.dbsoftwares.bungeeutilisals.commands.general.message;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.command.TabCall;
import com.dbsoftwares.bungeeutilisals.api.command.TabCompleter;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserPrivateMessageEvent;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.StaffUtils;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.user.BUser;

import java.util.List;
import java.util.Optional;

public class ReplyCommandCall implements CommandCall, TabCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( user instanceof BUser && ( (BUser) user ).isMsgToggled() )
        {
            user.sendLangMessage( "general-commands.msgtoggle.pm-cmd-disabled" );
            return;
        }
        if ( args.size() < 1 )
        {
            user.sendLangMessage( "general-commands.reply.usage" );
            return;
        }
        if ( !user.getStorage().hasData( "MSG_LAST_USER" ) )
        {
            user.sendLangMessage( "general-commands.reply.no-target" );
            return;
        }

        final String name = user.getStorage().getData( "MSG_LAST_USER" );
        if ( BUCore.getApi().getPlayerUtils().isOnline( name ) && !StaffUtils.isHidden( name ) )
        {
            final Optional<User> optional = BUCore.getApi().getUser( name );
            final String message = String.join( " ", args );

            if ( optional.isPresent() )
            {
                final User target = optional.get();

                if ( target instanceof BUser && ( (BUser) target ).isMsgToggled() )
                {
                    user.sendLangMessage( "general-commands.msgtoggle.not-receiving-pms" );
                    return;
                }

                if ( target.getStorage().getIgnoredUsers().stream().anyMatch( ignored -> ignored.equalsIgnoreCase( user.getName() ) ) )
                {
                    user.sendLangMessage( "general-commands.reply.ignored" );
                    return;
                }

                // only needs to be set for target, as the current user (sender) still has this target as last user
                target.getStorage().setData( "MSG_LAST_USER", user.getName() );

                {
                    String msgMessage = target.buildLangMessage( "general-commands.reply.format.receive" );
                    msgMessage = Utils.c( msgMessage );
                    msgMessage = msgMessage.replace( "{sender}", user.getName() );
                    msgMessage = msgMessage.replace( "{message}", message );

                    target.sendRawMessage( msgMessage );
                }
                {
                    String msgMessage = user.buildLangMessage( "general-commands.reply.format.send" );
                    msgMessage = Utils.c( msgMessage );
                    msgMessage = msgMessage.replace( "{receiver}", target.getName() );
                    msgMessage = msgMessage.replace( "{message}", message );

                    user.sendRawMessage( msgMessage );
                }

                BUCore.getApi().getEventLoader().launchEventAsync( new UserPrivateMessageEvent( user, target, message ) );
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

    @Override
    public List<String> onTabComplete( final User user, final String[] args )
    {
        return TabCompleter.empty();
    }
}
