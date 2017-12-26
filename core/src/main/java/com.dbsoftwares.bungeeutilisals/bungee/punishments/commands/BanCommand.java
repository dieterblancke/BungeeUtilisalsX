package com.dbsoftwares.bungeeutilisals.bungee.punishments.commands;

import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.permissions.Permissions;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.settings.FileLocations;
import net.md_5.bungee.api.CommandSender;

import java.util.Arrays;
import java.util.List;

public class BanCommand extends Command {

    public BanCommand() {
        super("ban", Arrays.asList(BungeeUtilisals.getConfigurations().get(FileLocations.PUNISHMENTS_CONFIG)
                .getString("commands.ban.aliases").split(", ")), Permissions.BAN_PERMISSION);
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