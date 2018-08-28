package com.dbsoftwares.bungeeutilisals.commands.plugin;

/*
 * Created by DBSoftwares on 10/01/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.command.SubCommand;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.google.common.collect.Lists;

import java.util.List;

public class PluginCommand extends Command {

    private List<SubCommand> subCommands = Lists.newArrayList();

    public PluginCommand() {
        super("bungeeutilisals", Lists.newArrayList("bu", "butilisals", "butili"), "bungeeutilisals.admin");

        subCommands.add(new VersionSubCommand());
        subCommands.add(new ReloadSubCommand());
        subCommands.add(new DumpSubCommand());
        subCommands.add(new ImportSubCommand());
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        List<String> completions = Lists.newArrayList();

        if (args.length == 0) {
            subCommands.forEach(subCommand -> completions.add(subCommand.getName()));
        } else if (args.length == 1) {
            subCommands.stream().filter(subCommand -> subCommand.getName().toLowerCase().startsWith(args[0].toLowerCase()))
                    .forEach(subCommand -> completions.add(subCommand.getName()));
        } else {
            SubCommand command = findSubCommand(args[0]);

            if (command != null) {
                return command.getCompletions(user, args);
            }
        }
        return completions;
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
        user.sendMessage("&aAdmin Commands help:");
        subCommands.forEach(cmd -> user.sendMessage("&b- &e" + cmd.getUsage()));
    }

    private SubCommand findSubCommand(String name) {
        return subCommands.stream().filter(subCommand -> subCommand.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }
}
