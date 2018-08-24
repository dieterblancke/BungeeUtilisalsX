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
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.utils.LanguageUtils;
import com.dbsoftwares.bungeeutilisals.utils.redis.Channels;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class ChatLockCommand extends Command implements Listener {

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
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
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
            BungeeUtilisals.getInstance().getRedisMessenger().sendChannelMessage(
                    Channels.CHATLOCK,
                    new LockData(server, user.getName())
            );
        } else {
            lockChat(server, user.getName());
        }
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
            IConfiguration config = BUCore.getApi().getLanguageManager().getLanguageConfiguration(BungeeUtilisals.getInstance(), player);

            event.setCancelled(true);
            LanguageUtils.sendLangMessage(player, "general-commands.chatlock.onchat");
        }
    }

    @Override
    public void unload() {
        super.unload();

        ProxyServer.getInstance().getPluginManager().unregisterListener(this);
    }

    @Data
    @AllArgsConstructor
    public class LockData {

        private String server;
        private String by;

    }
}
