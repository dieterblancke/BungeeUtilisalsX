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
import com.dbsoftwares.bungeeutilisals.utils.redis.Channels;
import com.dbsoftwares.bungeeutilisals.utils.redis.channeldata.StaffChatData;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.List;

public class StaffChatCommand extends BUCommand implements Listener {

    public StaffChatCommand() {
        super(
                "staffchat",
                Arrays.asList(FileLocation.GENERALCOMMANDS.getConfiguration().getString("staffchat.aliases").split(", ")),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString("staffchat.permission")
        );
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeUtilisals.getInstance(), this);
    }

    public static void sendStaffChatMessage(String serverName, String userName, String message) {
        for (User user : BUCore.getApi().getUsers()) {
            ProxiedPlayer parent = user.getParent();

            if (parent.hasPermission(FileLocation.GENERALCOMMANDS.getConfiguration().getString("staffchat.permission"))
                    || parent.hasPermission("bungeeutilisals.commands.*")
                    || parent.hasPermission("bungeeutilisals.*")
                    || parent.hasPermission("*")) {
                user.sendLangMessage(false, "general-commands.staffchat.format",
                        "{user}", userName, "{server}", serverName, "{message}", message);
            }
        }
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return Lists.newArrayList("on", "off");
    }

    @Override
    public void onExecute(User user, String[] args) {
        user.setInStaffChat(!user.isInStaffChat());

        user.sendLangMessage("general-commands.staffchat."
                + (user.isInStaffChat() ? "enabled" : "disabled"));
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
        BUCore.getApi().getUser(player).ifPresent(user -> {
            if (user.isInStaffChat()) {
                event.setCancelled(true);
                if (BungeeUtilisals.getInstance().getConfig().getBoolean("redis")) {
                    BungeeUtilisals.getInstance().getRedisMessenger().sendChannelMessage(
                            Channels.STAFFCHAT,
                            new StaffChatData(user.getServerName(), user.getName(), event.getMessage())
                    );
                } else {
                    sendStaffChatMessage(user.getServerName(), user.getName(), event.getMessage());
                }
            }
        });
    }
}
