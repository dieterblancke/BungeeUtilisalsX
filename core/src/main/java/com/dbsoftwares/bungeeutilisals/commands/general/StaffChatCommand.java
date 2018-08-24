package com.dbsoftwares.bungeeutilisals.commands.general;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.List;

/*
 * Created by DBSoftwares on 24 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class StaffChatCommand extends Command implements Listener {

    public StaffChatCommand() {
        super(
                "staffchat",
                Arrays.asList(FileLocation.GENERALCOMMANDS.getConfiguration().getString("staffchat.aliases").split(", ")),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString("staffchat.permission")
        );
        ProxyServer.getInstance().getPluginManager().registerListener(BungeeUtilisals.getInstance(), this);
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
                // TODO: send staffchat message (check redis too)
            }
        });
    }


    @Data
    @AllArgsConstructor
    public class StaffChatData {

        private String server;
        private String player;
        private String message;

    }
}
