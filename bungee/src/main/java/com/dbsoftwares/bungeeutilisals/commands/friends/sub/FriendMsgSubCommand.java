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

import java.util.Arrays;
import java.util.List;

public class FriendMsgSubCommand extends SubCommand {

    public FriendMsgSubCommand() {
        super(
                "msg", 2, -1,
                Arrays.asList(FileLocation.FRIENDS_CONFIG.getConfiguration().getString("subcommands.msg.aliases").split(", "))
        );
    }

    @Override
    public String getUsage() {
        return "/friends msg (name) (message)";
    }

    @Override
    public String getPermission() {
        return FileLocation.FRIENDS_CONFIG.getConfiguration().getString("subcommands.msg.permission");
    }

    @Override
    public void onExecute(User user, String[] args) {
        final String name = args[0];

        if (user.getFriends().stream().noneMatch(data -> data.getFriend().equalsIgnoreCase(name))) {
            user.sendLangMessage("friends.msg.not-friend", "{user}", name);
            return;
        }
        if (BUCore.getApi().getPlayerUtils().isOnline(name)) {
            final String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            // TODO
            final User target = optional.get();

            if (!target.getFriendSettings().isMessages()) {
                user.sendLangMessage("friends.msg.disallowed");
                return;
            }

            user.getStorage().setData("FRIEND_MSG_LAST_USER", target.getName());
            target.getStorage().setData("FRIEND_MSG_LAST_USER", user.getName());

            {
                String msgMessage = user.buildLangMessage("friends.msg.format.send");
                msgMessage = Utils.c(msgMessage);
                msgMessage = msgMessage.replace("{user}", target.getName());
                msgMessage = msgMessage.replace("{message}", message);

                user.sendRawMessage(msgMessage);
            }
            {
                String msgMessage = target.buildLangMessage("friends.msg.format.receive");
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
        return null;
    }
}
