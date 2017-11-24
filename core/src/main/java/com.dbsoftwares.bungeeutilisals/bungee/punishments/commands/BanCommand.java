package com.dbsoftwares.bungeeutilisals.bungee.punishments.commands;

import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.permissions.Permissions;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.CommandSender;

import java.util.List;

public class BanCommand extends BUCommand {

    public BanCommand() {
        super("ban", BungeeUtilisals.getInstance().getAliases().getOrDefault("ban", Lists.newArrayList()), Permissions.BAN_PERMISSION);
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return null;
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length < 2) {
            // TODO: send usage message to user
            return;
        }

    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {

    }
}