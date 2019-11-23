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

package com.dbsoftwares.bungeeutilisals.commands.friends;

import com.dbsoftwares.bungeeutilisals.api.command.CommandBuilder;
import com.dbsoftwares.bungeeutilisals.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisals.api.command.ParentCommand;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.commands.friends.sub.*;

public class FriendsCommandCall extends ParentCommand implements CommandCall
{

    public FriendsCommandCall()
    {
        super( "friends.help.message" );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "add" )
                        .fromSection( FileLocation.FRIENDS_CONFIG.getConfiguration(), "subcommands.add" )
                        .executable( new FriendAddSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "accept" )
                        .fromSection( FileLocation.FRIENDS_CONFIG.getConfiguration(), "subcommands.accept" )
                        .executable( new FriendAcceptSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "deny" )
                        .fromSection( FileLocation.FRIENDS_CONFIG.getConfiguration(), "subcommands.deny" )
                        .executable( new FriendDenySubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "removerequest" )
                        .fromSection( FileLocation.FRIENDS_CONFIG.getConfiguration(), "subcommands.removerequest" )
                        .executable( new FriendRemoveRequestSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "remove" )
                        .fromSection( FileLocation.FRIENDS_CONFIG.getConfiguration(), "subcommands.remove" )
                        .executable( new FriendRemoveSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "list" )
                        .fromSection( FileLocation.FRIENDS_CONFIG.getConfiguration(), "subcommands.list" )
                        .executable( new FriendListSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "requests" )
                        .fromSection( FileLocation.FRIENDS_CONFIG.getConfiguration(), "subcommands.requests" )
                        .executable( new FriendRequestsSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "msg" )
                        .fromSection( FileLocation.FRIENDS_CONFIG.getConfiguration(), "subcommands.msg" )
                        .executable( new FriendMsgSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "reply" )
                        .fromSection( FileLocation.FRIENDS_CONFIG.getConfiguration(), "subcommands.reply" )
                        .executable( new FriendReplySubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "settings" )
                        .fromSection( FileLocation.FRIENDS_CONFIG.getConfiguration(), "subcommands.settings" )
                        .executable( new FriendSettingsSubCommandCall() )
                        .build()
        );
    }
}
