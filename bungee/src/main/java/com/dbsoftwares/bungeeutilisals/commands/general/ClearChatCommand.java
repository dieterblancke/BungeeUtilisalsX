package com.dbsoftwares.bungeeutilisals.commands.general;

/*
 * Created by DBSoftwares on 10/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.utils.redis.Channels;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Data;
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
                    new ClearData(server, user.getName())
            );
        } else {
            clearChat(server, user.getName());
        }
    }

    @Data
    @AllArgsConstructor
    public class ClearData {

        private String server;
        private String by;

    }
}
