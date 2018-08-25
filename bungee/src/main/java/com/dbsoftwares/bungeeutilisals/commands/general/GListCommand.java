package com.dbsoftwares.bungeeutilisals.commands.general;

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
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
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
        IConfiguration config = FileLocation.GENERALCOMMANDS.getConfiguration();
        String color = config.getString("glist.playerlist.color");
        String separator = config.getString("glist.playerlist.separator");
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
                        "%playerlist%", Utils.c(color + Joiner.on(separator).join(group.getPlayerList()))
                ));
            }
        } else {
            for (ServerInfo info : ProxyServer.getInstance().getServers().values()) {
                messages.add(MessageBuilder.buildMessage(user, config.getSection("glist.format"),
                        "%server%", info.getName(),
                        "%players%", String.valueOf(info.getPlayers().size()),
                        "%playerlist%", Utils.c(color + Joiner.on(separator).join(info.getPlayers()))
                ));
            }
        }
        messages.add(MessageBuilder.buildMessage(user, config.getSection("glist.total"),
                "%total%", BUCore.getApi().getPlayerUtils().getTotalCount(),
                "%playerlist%", Utils.c(color + Joiner.on(separator)
                        .join(BUCore.getApi().getPlayerUtils().getPlayers()))
        ));

        messages.forEach(user::sendMessage);
    }
}
