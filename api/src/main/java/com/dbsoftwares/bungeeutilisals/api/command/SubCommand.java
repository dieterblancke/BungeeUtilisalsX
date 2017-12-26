package com.dbsoftwares.bungeeutilisals.api.command;

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Arrays;
import java.util.Optional;

public abstract class SubCommand implements ICommand {

    Command parent;
    String name;

    public SubCommand(Command parent, String name) {
        this.parent = parent;
        this.name = name;
    }

    public void execute(CommandSender sender, String[] args) {
        BUAPI api = BUCore.getApi();

        args = Arrays.copyOfRange(args, 1, args.length);

        if (sender instanceof ProxiedPlayer) {
            Optional<User> optional = api.getUser(sender.getName());

            if (optional.isPresent()) {
                User user = optional.get();

                try {
                    onExecute(user, args);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        try {
            onExecute(sender, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}