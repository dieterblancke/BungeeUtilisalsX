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

package com.dbsoftwares.bungeeutilisalsx.common.commands.friends;

import com.dbsoftwares.bungeeutilisalsx.common.api.command.CommandBuilder;
import com.dbsoftwares.bungeeutilisalsx.common.api.command.CommandCall;
import com.dbsoftwares.bungeeutilisalsx.common.api.command.ParentCommand;
import com.dbsoftwares.bungeeutilisalsx.common.api.utils.config.ConfigFiles;
import com.dbsoftwares.bungeeutilisalsx.common.commands.friends.sub.*;

public class FriendsCommandCall extends ParentCommand implements CommandCall
{

    public FriendsCommandCall()
    {
        super( "friends.help.message" );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "add" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.add" )
                        .executable( new FriendAddSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "accept" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.accept" )
                        .executable( new FriendAcceptSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "deny" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.deny" )
                        .executable( new FriendDenySubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "removerequest" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.removerequest" )
                        .executable( new FriendRemoveRequestSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "remove" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.remove" )
                        .executable( new FriendRemoveSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "list" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.list" )
                        .executable( new FriendListSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "requests" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.requests" )
                        .executable( new FriendRequestsSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "msg" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.msg" )
                        .executable( new FriendMsgSubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "reply" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.reply" )
                        .executable( new FriendReplySubCommandCall() )
                        .build()
        );

        super.registerSubCommand(
                CommandBuilder.builder()
                        .name( "settings" )
                        .fromSection( ConfigFiles.FRIENDS_CONFIG.getConfig(), "subcommands.settings" )
                        .executable( new FriendSettingsSubCommandCall() )
                        .build()
        );
    }
}
