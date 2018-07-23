package com.dbsoftwares.bungeeutilisals.commands;

/*
 * Created by DBSoftwares on 10/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.api.utils.server.ServerGroup;
import com.dbsoftwares.bungeeutilisals.utils.MessageBuilder;
import com.dbsoftwares.configuration.api.IConfiguration;
import com.dbsoftwares.configuration.api.ISection;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.List;

public class GListCommand extends Command {

    public GListCommand() {
        super(
                "glist",
                Arrays.asList(FileLocation.GENERALCOMMANDS.getConfiguration().getString("glist.aliases").split(", ")),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString("glist.permission")
        );
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(User user, String[] args) {
        // TODO
        IConfiguration config = FileLocation.GENERALCOMMANDS.getConfiguration();
        List<TextComponent> messages = Lists.newArrayList();

        if (config.getBoolean("glist.servers.enabled")) {
            for (String server : config.getStringList("glist.servers.list")) {
                ServerGroup group = FileLocation.SERVERGROUPS.getData(server);

                if (group == null) {
                    BUCore.log("Could not find a servergroup or -name for " + server + "!");
                    return;
                }

                messages.add(MessageBuilder.buildMessage(user, config.getSection("glist.format"),
                        "%server%", group.getName(),
                        "%players%", String.valueOf(group.getPlayers()),

                ));
                // TODO: %playerlist%

            }
        } else {
            // TODO
        }

        String color = config.getString("GList.PlayerListColor");
        if (!config.getBoolean("GList.Custom_GList")) {
            for (ServerInfo server : ProxyServer.getInstance().getServers().values()) {
                List<String> list = Lists.newArrayList();

                for (ProxiedPlayer pl : server.getPlayers()) {
                    list.add(pl.getDisplayName());
                }
                list.sort(String.CASE_INSENSITIVE_ORDER);
                sender.sendMessage(Utils.format(config.getString("GList.Format").replace("%server%", server.getName())
                        .replace("%players%", String.valueOf(server.getPlayers().size()))
                        .replace("%playerlist%", Joiner.on(color + ", " + color).join(list))));
            }
            sender.sendMessage(Utils.format(config.getString("GList.Total").replace("%totalnum%",
                    String.valueOf(ProxyServer.getInstance().getPlayers().size()))));
        } else {
            ISection cs = config.getSection("GList.Servers");
            for (String key : cs.getKeys()) {
                int serverPlayers = 0;
                List<String> list = Lists.newArrayList();

                String value = cs.getString(key);
                if (value.contains(",")) {
                    for (String calculate : value.split(",")) {
                        ServerInfo server = ProxyServer.getInstance().getServerInfo(calculate);
                        if (server == null) {
                            continue;
                        }
                        serverPlayers += server.getPlayers().size();
                        for (ProxiedPlayer pl : server.getPlayers()) {
                            list.add(pl.getDisplayName());
                        }
                    }
                } else {
                    ServerInfo server = ProxyServer.getInstance().getServerInfo(value);
                    if (server != null) {
                        serverPlayers += server.getPlayers().size();
                        for (ProxiedPlayer pl : server.getPlayers()) {
                            list.add(pl.getDisplayName());
                        }
                    }
                }
                list.sort(String.CASE_INSENSITIVE_ORDER);
                sender.sendMessage(Utils.format(config.getString("GList.Format").replace("%server%", key)
                        .replace("%playerlist%", Joiner.on(color + ", " + color).join(list)).replace("%players%", String.valueOf(serverPlayers.toString()))));
            }
            sender.sendMessage(Utils.format(config.getString("GList.Total").replace("%totalnum%", String.valueOf(ProxyServer.getInstance().getPlayers().size()))));
        }

    }
}
