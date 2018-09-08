package com.dbsoftwares.bungeeutilisals.commands.friends.sub;

import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;

import java.util.List;

/*
 * Created by DBSoftwares on 09 september 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class AddFriendSubCommand extends SubCommand {

    public AddFriendSubCommand() {
        super("add", 1);
    }

    @Override
    public String getUsage() {
        return "/friends add (name)";
    }

    @Override
    public String getPermission() {
        return FileLocation.FRIENDS_CONFIG.getConfiguration().getString("subcommands.add.permission");
    }

    @Override
    public void onExecute(User user, String[] args) {
        // TODO
    }

    @Override
    public List<String> getCompletions(User user, String[] args) {
        return null;
    }
}
