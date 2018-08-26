package com.dbsoftwares.bungeeutilisals.commands.plugin;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.google.common.collect.ImmutableList;

import java.util.List;

/*
 * Created by DBSoftwares on 26 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class VersionSubCommand extends SubCommand {

    public VersionSubCommand() {
        super("version", 0);
    }

    @Override
    public String getUsage() {
        return "/bungeeutilisals version";
    }

    @Override
    public String getPermission() {
        return "bungeeutilisals.admin";
    }

    @Override
    public void onExecute(User user, String[] args) {
        user.sendMessage("&fYou are running BungeeUtilisals v&c" + BungeeUtilisals.getInstance().getDescription().getVersion() + "&f!");
    }

    @Override
    public List<String> getCompletions(User user, String[] args) {
        return ImmutableList.of();
    }
}
