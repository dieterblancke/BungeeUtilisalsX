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

package com.dbsoftwares.bungeeutilisals.commands.general;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;

import java.util.List;
import java.util.Optional;

public class HelpOpCommandCall implements CommandCall
{

    @Override
    public void onExecute( final User user, final List<String> args, final List<String> parameters )
    {
        if ( args.size() == 0 )
        {
            user.sendLangMessage( "general-commands.helpop.usage" );
            return;
        }
        if ( args.get( 0 ).equalsIgnoreCase( "reply" ) && args.size() > 2 )
        {
            executeReplySubCommand( user, args );
            return;
        }
        final String message = String.join( " ", args );
        final String permission = FileLocation.GENERALCOMMANDS.getConfiguration().getString( "helpop.receive-broadcast" );

        if ( !user.hasPermission( permission ) )
        {
            user.sendLangMessage(
                    "general-commands.helpop.broadcast",
                    "{message}", message,
                    "{user}", user.getName()
            );
        }

        BUCore.getApi().langPermissionBroadcast(
                "general-commands.helpop.broadcast",
                permission,
                "{message}", message,
                "{user}", user.getName()
        );
    }

    private void executeReplySubCommand( final User user, final List<String> args )
    {
        if ( !user.hasPermission( FileLocation.GENERALCOMMANDS.getConfiguration().getString( "helpop.reply-permission" ) ) )
        {
            user.sendLangMessage( "no-permission" );
            return;
        }

        final String targetName = args.get( 1 );
        final Optional<User> optionalTarget = BUCore.getApi().getUser( targetName );
        final String message = String.join( " ", args.subList( 2, args.size() ) );

        if ( !optionalTarget.isPresent() )
        {
            user.sendLangMessage( "offline" );
            return;
        }
        final User target = optionalTarget.get();

        target.sendLangMessage(
                "general-commands.helpop.reply-receive",
                "{user}", user.getName(),
                "{message}", message
        );
        user.sendLangMessage(
                "general-commands.helpop.reply-send",
                "{user}", target.getName(),
                "{message}", message
        );
    }
}
