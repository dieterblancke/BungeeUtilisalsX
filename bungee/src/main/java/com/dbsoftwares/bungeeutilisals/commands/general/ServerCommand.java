package com.dbsoftwares.bungeeutilisals.commands.general;

/*
 * Created by DBSoftwares on 10/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Arrays;
import java.util.List;

public class ServerCommand extends Command {

    public ServerCommand() {
        super(
                "server",
                Arrays.asList(FileLocation.GENERALCOMMANDS.getConfiguration().getString("server.aliases").split(", ")),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString("server.permission")
        );
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length != 1) {
            user.sendLangMessage("general-commands.server.usage", "{server}", user.getServerName());
            return;
        }
        ServerInfo server = ProxyServer.getInstance().getServerInfo(args[0]);

        if (server == null) {
            user.sendLangMessage("general-commands.server.notfound", "{server}", args[0]);
            return;
        }
        if (user.getServerName().equalsIgnoreCase(server.getName())) {
            user.sendLangMessage("general-commands.server.alreadyconnected", "{server}", args[0]);
            return;
        }
        user.getParent().connect(server);
        user.sendLangMessage("general-commands.server.connecting", "{server}", args[0]);
    }
}
