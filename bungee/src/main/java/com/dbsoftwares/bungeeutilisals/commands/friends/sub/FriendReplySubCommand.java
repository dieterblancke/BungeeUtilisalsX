/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *  *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *  *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.commands.friends.sub;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FriendReplySubCommand extends SubCommand {

    public FriendReplySubCommand() {
        super(
                "reply", 1, -1,
                Arrays.asList(FileLocation.FRIENDS_CONFIG.getConfiguration().getString("subcommands.reply.aliases").split(", "))
        );
    }

    @Override
    public String getUsage() {
        return "/friends reply (message)";
    }

    @Override
    public String getPermission() {
        return FileLocation.FRIENDS_CONFIG.getConfiguration().getString("subcommands.reply.permission");
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (!user.getStorage().hasData("FRIEND_MSG_LAST_USER")) {
            user.sendLangMessage("friends.reply.no-target");
            return;
        }

        final String name = user.getStorage().getData("FRIEND_MSG_LAST_USER");

        if (user.getFriends().stream().noneMatch(data -> data.getFriend().equalsIgnoreCase(name))) {
            user.sendLangMessage("friends.reply.not-friend", "{user}", name);
            return;
        }
        final Optional<User> optional = BUCore.getApi().getUser(name);

        if (optional.isPresent()) {
            final String message = String.join(" ", args);
            final User target = optional.get();

            // only needs to be set for target, as the current user (sender) still has this target as last user
            target.getStorage().setData("FRIEND_MSG_LAST_USER", user.getName());

            {
                String msgMessage = user.buildLangMessage("friends.reply.format.send");
                msgMessage = Utils.c(msgMessage);
                msgMessage = msgMessage.replace("{user}", target.getName());
                msgMessage = msgMessage.replace("{message}", message);

                user.sendRawMessage(msgMessage);
            }
            {
                String msgMessage = target.buildLangMessage("friends.reply.format.receive");
                msgMessage = Utils.c(msgMessage);
                msgMessage = msgMessage.replace("{user}", user.getName());
                msgMessage = msgMessage.replace("{message}", message);

                target.sendRawMessage(msgMessage);
            }
        } else {
            user.sendLangMessage("offline");
        }
    }

    @Override
    public List<String> getCompletions(User user, String[] args) {
        return ImmutableList.of();
    }
}
