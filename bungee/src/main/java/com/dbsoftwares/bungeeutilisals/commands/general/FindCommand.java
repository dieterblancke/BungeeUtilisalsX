package com.dbsoftwares.bungeeutilisals.commands.general;

/*
 * Created by DBSoftwares on 10/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.google.common.collect.ImmutableList;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Arrays;
import java.util.List;

public class FindCommand extends Command {

    public FindCommand() {
        super(
                "find",
                Arrays.asList(FileLocation.GENERALCOMMANDS.getConfiguration().getString("find.aliases").split(", ")),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString("find.permission")
        );
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return ImmutableList.of();
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length != 1) {
            user.sendLangMessage("general-commands.find.usage");
            return;
        }
        ServerInfo server = BUCore.getApi().getPlayerUtils().findPlayer(args[0]);

        if (server == null) {
            user.sendLangMessage("offline");
            return;
        }
        user.sendLangMessage("general-commands.find.message", "{user}", args[0], "{server}", server.getName());
    }
}
