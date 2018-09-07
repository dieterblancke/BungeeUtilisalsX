package com.dbsoftwares.bungeeutilisals.commands.plugin;

import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.List;

/*
 * Created by DBSoftwares on 26 augustus 2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

public class ReloadSubCommand extends SubCommand {

    public ReloadSubCommand() {
        super("reload", 0);
    }

    @Override
    public String getUsage() {
        return "/bungeeutilisals reload";
    }

    @Override
    public String getPermission() {
        return "bungeeutilisals.admin.reload";
    }

    @Override
    public void onExecute(User user, String[] args) {
        for (FileLocation location : FileLocation.values()) {
            try {
                location.getConfiguration().reload();

                location.getData().clear();
                location.getDataList().clear();
                location.loadData();
            } catch (IOException e) {
                e.printStackTrace();
                user.sendMessage("&fCould not reload " + location.toString().toLowerCase().replace("_", " ") + "!");
            }
        }
        BungeeUtilisals.getInstance().reload();
        user.sendMessage("&fAll configuration files have been reloaded!");
    }

    @Override
    public List<String> getCompletions(User user, String[] args) {
        return ImmutableList.of();
    }
}
