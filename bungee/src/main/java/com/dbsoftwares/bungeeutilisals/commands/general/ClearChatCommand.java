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

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.utils.redis.Channels;
import com.dbsoftwares.bungeeutilisals.utils.redis.channeldata.ChatActionData;
import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Arrays;
import java.util.List;

public class ClearChatCommand extends Command {

    public ClearChatCommand() {
        super(
                "clearchat",
                Arrays.asList(FileLocation.GENERALCOMMANDS.getConfiguration().getString("clearchat.aliases").split(", ")),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString("clearchat.permission")
        );
    }

    public static void clearChat(String server, String by) {
        if (server.equalsIgnoreCase("ALL")) {
            BUCore.getApi().getUsers().forEach(u -> clearChat(u, by));
        } else {
            ServerInfo info = ProxyServer.getInstance().getServerInfo(server);

            if (info != null) {
                BUCore.getApi().getUsers().stream().filter(u -> u.getServerName().equalsIgnoreCase(info.getName()))
                        .forEach(u -> clearChat(u, by));
            }
        }
    }

    private static void clearChat(User user, String by) {
        for (int i = 0; i < 250; i++) {
            user.sendMessage(Utils.format("&e"));
        }

        user.sendLangMessage("general-commands.clearchat.cleared", "{user}", by);
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length == 0) {
            user.sendLangMessage("general-commands.clearchat.usage");
            return;
        }
        String server = args[0].toLowerCase().contains("g") ? "ALL" : user.getServerName();

        if (BungeeUtilisals.getInstance().getConfig().getBoolean("redis")) {
            BungeeUtilisals.getInstance().getRedisMessenger().sendChannelMessage(
                    Channels.CLEARCHAT,
                    new ChatActionData(server, user.getName())
            );
        } else {
            clearChat(server, user.getName());
        }
    }
}
