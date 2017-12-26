package com.dbsoftwares.bungeeutilisals.api.command;

import com.dbsoftwares.bungeeutilisals.api.user.User;
import net.md_5.bungee.api.CommandSender;

public interface ICommand {

    void onExecute(User user, String[] args);

    void onExecute(CommandSender sender, String[] args);

}