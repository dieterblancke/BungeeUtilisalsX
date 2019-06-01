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

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.friends.FriendSettingType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.redis.RedisMessageHandler;
import com.dbsoftwares.bungeeutilisals.redis.handlers.FriendMsgMessageHandler;
import com.dbsoftwares.bungeeutilisals.utils.redisdata.FriendMessageData;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
            final Optional<User> optional = BUCore.getApi().getUser(name);
            final String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

            if (optional.isPresent()) {
                final User target = optional.get();

                if (!target.getFriendSettings().isMessages()) {
                    user.sendLangMessage("friends.msg.disallowed");
                    return;
                }

                user.getStorage().setData("FRIEND_MSG_LAST_USER", target.getName());

                {
                    String msgMessage = target.buildLangMessage("friends.msg.format.receive");
                    msgMessage = Utils.c(msgMessage);
                    msgMessage = msgMessage.replace("{user}", user.getName());
                    msgMessage = msgMessage.replace("{message}", message);

                    target.sendRawMessage(msgMessage);
                }
                {
                    String msgMessage = user.buildLangMessage("friends.msg.format.send");
                    msgMessage = Utils.c(msgMessage);
                    msgMessage = msgMessage.replace("{user}", target.getName());
                    msgMessage = msgMessage.replace("{message}", message);

                    user.sendRawMessage(msgMessage);
                }
            } else if (BungeeUtilisals.getInstance().getConfig().getBoolean("redis")) {
                final Dao dao = BUCore.getApi().getStorageManager().getDao();
                final UserStorage storage = dao.getUserDao().getUserData(name);
                final boolean allowed = dao.getFriendsDao().getSetting(storage.getUuid(), FriendSettingType.MESSAGES);

                if (!allowed) {
                    user.sendLangMessage("friends.msg.disallowed");
                    return;
                }

                final RedisMessageHandler<FriendMessageData> handler = BungeeUtilisals.getInstance()
                        .getRedisMessenger().getHandler(FriendMsgMessageHandler.class);

                handler.send(new FriendMessageData("msg", user.getUuid(), user.getName(), name, message));

                String msgMessage = user.buildLangMessage("friends.msg.format.send");
                msgMessage = Utils.c(msgMessage);
                msgMessage = msgMessage.replace("{user}", name);
                msgMessage = msgMessage.replace("{message}", message);

                user.sendRawMessage(msgMessage);
            } else {
                user.sendLangMessage("offline");
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
