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

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.redis.RedisMessageHandler;
import com.dbsoftwares.bungeeutilisals.redis.handlers.MsgMessageHandler;
import com.dbsoftwares.bungeeutilisals.utils.redisdata.MessageData;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ReplyCommand extends BUCommand {

    public ReplyCommand() {
        super(
                "reply",
                Arrays.asList(FileLocation.GENERALCOMMANDS.getConfiguration().getString("reply.aliases").split(", ")),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString("reply.permission")
        );
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length < 1) {
            user.sendLangMessage("general-commands.reply.usage");
            return;
        }
        if (!user.getStorage().hasData("MSG_LAST_USER")) {
            user.sendLangMessage("general-commands.reply.no-target");
            return;
        }

        final String name = user.getStorage().getData("MSG_LAST_USER");
        if (BUCore.getApi().getPlayerUtils().isOnline(name)) {
            final Optional<User> optional = BUCore.getApi().getUser(name);
            final String message = String.join(" ", args);

            if (optional.isPresent()) {
                final User target = optional.get();

                if (target.getStorage().getIgnoredUsers().stream().anyMatch(ignored -> ignored.equalsIgnoreCase(user.getName()))) {
                    user.sendLangMessage("general-commands.reply.ignored");
                    return;
                }

                // only needs to be set for target, as the current user (sender) still has this target as last user
                target.getStorage().setData("MSG_LAST_USER", user.getName());

                {
                    String msgMessage = target.buildLangMessage("general-commands.reply.format.receive");
                    msgMessage = Utils.c(msgMessage);
                    msgMessage = msgMessage.replace("{sender}", user.getName());
                    msgMessage = msgMessage.replace("{message}", message);

                    target.sendRawMessage(msgMessage);
                }
                {
                    String msgMessage = user.buildLangMessage("general-commands.reply.format.send");
                    msgMessage = Utils.c(msgMessage);
                    msgMessage = msgMessage.replace("{receiver}", target.getName());
                    msgMessage = msgMessage.replace("{message}", message);

                    user.sendRawMessage(msgMessage);
                }
            } else if (BungeeUtilisals.getInstance().getConfig().getBoolean("redis")) {
                final Dao dao = BUCore.getApi().getStorageManager().getDao();
                final UserStorage storage = dao.getUserDao().getUserData(name);

                if (storage.getIgnoredUsers().stream().anyMatch(ignored -> ignored.equalsIgnoreCase(user.getName()))) {
                    user.sendLangMessage("general-commands.reply.ignored");
                    return;
                }

                final RedisMessageHandler<MessageData> handler = BungeeUtilisals.getInstance()
                        .getRedisMessenger().getHandler(MsgMessageHandler.class);

                handler.send(new MessageData("reply", user.getUuid(), user.getName(), name, message));

                String msgMessage = user.buildLangMessage("general-commands.reply.format.send");
                msgMessage = Utils.c(msgMessage);
                msgMessage = msgMessage.replace("{receiver}", name);
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
    public List<String> onTabComplete(User user, String[] args) {
        return ImmutableList.of();
    }
}
