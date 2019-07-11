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
import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.text.LanguageUtils;
import com.dbsoftwares.bungeeutilisals.redis.RedisMessageHandler;
import com.dbsoftwares.bungeeutilisals.redis.handlers.ChatLockMessageHandler;
import com.dbsoftwares.bungeeutilisals.utils.redisdata.ChatActionData;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ChatLockCommand extends BUCommand implements Listener {

    private static final List<String> lockedChatServers = Lists.newArrayList();

    public ChatLockCommand() {
        super(
                "chatlock",
                Arrays.asList(FileLocation.GENERALCOMMANDS.getConfiguration().getString("chatlock.aliases").split(", ")),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString("chatlock.permission")
        );

        ProxyServer.getInstance().getPluginManager().registerListener(BungeeUtilisals.getInstance(), this);
    }

    public static void lockChat(String server, String by) {
        Stream<User> users = server.equals("ALL")
                ? BUCore.getApi().getUsers().stream()
                : BUCore.getApi().getUsers().stream().filter(u -> u.getServerName().equalsIgnoreCase(server));

        if (lockedChatServers.contains(server)) {
            lockedChatServers.remove(server);

            users.forEach(u -> u.sendLangMessage("general-commands.chatlock.unlocked", "{user}", by));
        } else {
            lockedChatServers.add(server);

            users.forEach(u -> u.sendLangMessage("general-commands.chatlock.locked", "{user}", by));
        }
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length == 0) {
            user.sendLangMessage("general-commands.chatlock.usage");
            return;
        }
        final String server = args[0].toLowerCase().contains("g") ? "ALL" : user.getServerName();

        if (BungeeUtilisals.getInstance().getConfig().getBoolean("redis")) {
            final RedisMessageHandler handler = BungeeUtilisals.getInstance().getRedisMessenger().getHandler(ChatLockMessageHandler.class);

            handler.send(new ChatActionData(server, user.getName()));
        } else {
            lockChat(server, user.getName());
        }
    }

    @Override
    public void unload() {
        super.unload();

        ProxyServer.getInstance().getPluginManager().unregisterListener(this);
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (event.isCommand() || event.isCancelled()) {
            return;
        }
        final ProxiedPlayer player = (ProxiedPlayer) event.getSender();
        final boolean canTalk = player.hasPermission(FileLocation.GENERALCOMMANDS.getConfiguration().getString("chatlock.bypass"))
                || !lockedChatServers.contains("ALL") && !lockedChatServers.contains(player.getServer().getInfo().getName());

        if (!canTalk) {
            event.setCancelled(true);
            LanguageUtils.sendLangMessage(player, "general-commands.chatlock.onchat");
        }
    }
}
