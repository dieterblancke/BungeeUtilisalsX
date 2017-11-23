package com.dbsoftwares.bungeeutilisals.bungee.punishments.commands;

import com.dbsoftwares.bungeeutilisals.api.command.BUCommand;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import net.md_5.bungee.api.CommandSender;

import java.util.List;

public class BanCommand extends BUCommand {

    public BanCommand(String name, List<String> aliases, String permission) {
        super(name, aliases, permission);
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return null;
    }

    @Override
    public void onExecute(User user, String[] args) {

    }

    @Override
    public void onExecute(CommandSender sender, String[] args) {

    }
}