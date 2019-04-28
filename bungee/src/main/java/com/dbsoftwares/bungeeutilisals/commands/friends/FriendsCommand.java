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

import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.commands.friends.sub.*;

import java.util.Arrays;
import java.util.List;

public class FriendsCommand extends BUCommand {

    public FriendsCommand() {
        super(
                "friends",
                Arrays.asList(FileLocation.FRIENDS_CONFIG.getConfiguration().getString("command.aliases").split(", ")),
                FileLocation.FRIENDS_CONFIG.getConfiguration().getString("command.permission")
        );

        subCommands.add(new FriendAddSubCommand());
        subCommands.add(new FriendAcceptSubCommand());
        subCommands.add(new FriendDenySubCommand());
        subCommands.add(new FriendRemoveRequestSubCommand());
        subCommands.add(new FriendRemoveSubCommand());
        subCommands.add(new FriendListSubCommand());
        subCommands.add(new FriendRequestsSubCommand());
        subCommands.add(new FriendMsgSubCommand());
        subCommands.add(new FriendReplySubCommand());
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return getSubcommandCompletions(user, args);
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length == 0) {
            sendHelpList(user);
            return;
        }
        for (SubCommand subCommand : subCommands) {
            if (subCommand.execute(user, args)) {
                return;
            }
        }
        sendHelpList(user);
    }

    private void sendHelpList(User user) {
        user.sendLangMessage("friends.help.header");
        subCommands.forEach(cmd -> user.sendLangMessage("friends.help.format", "%usage%", cmd.getUsage()));
    }
}
