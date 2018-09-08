package com.dbsoftwares.bungeeutilisals.commands.friends;

/*
 * Created by DBSoftwares on 10/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.commands.friends.sub.AddFriendSubCommand;

import java.util.Arrays;
import java.util.List;

public class FriendsCommand extends Command {

    public FriendsCommand() {
        super(
                "friends",
                Arrays.asList(FileLocation.FRIENDS_CONFIG.getConfiguration().getString("command.aliases").split(", ")),
                FileLocation.FRIENDS_CONFIG.getConfiguration().getString("command.permission")
        );


        subCommands.add(new AddFriendSubCommand());
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return getSubcommandCompletions(user, args);
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length == 0) {
            sendHelpList(user);
            return;
        }
        for (SubCommand subCommand : subCommands) {
            if (subCommand.execute(user, args)) {
                return;
            }
        }
        sendHelpList(user);
    }

    private void sendHelpList(User user) {
        user.sendLangMessage("friends.help.header");
        subCommands.forEach(cmd -> user.sendLangMessage("friends.help.format", "%usage%", cmd.getUsage()));
    }
}
