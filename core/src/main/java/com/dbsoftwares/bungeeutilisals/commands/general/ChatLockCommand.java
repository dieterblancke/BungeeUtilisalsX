package com.dbsoftwares.bungeeutilisals.commands.general;

/*
 * Created by DBSoftwares on 10/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

public class ChatLockCommand extends Command {

    public ChatLockCommand() {
        super(
                "chatlock",
                Arrays.asList(FileLocation.GENERALCOMMANDS.getConfiguration().getString("chatlock.aliases").split(", ")),
                FileLocation.GENERALCOMMANDS.getConfiguration().getString("chatlock.permission")
        );
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
        String server = args[0].toLowerCase().contains("g") ? "ALL" : user.getServerName();

        if (BungeeUtilisals.getInstance().getConfig().getBoolean("redis")) {
            // TODO: Send chatlock message
        } else {
            // TODO: Lock chat
        }
    }

    @Data
    @AllArgsConstructor
    public class LockData {

        private String server;
        private String by;

    }
}
