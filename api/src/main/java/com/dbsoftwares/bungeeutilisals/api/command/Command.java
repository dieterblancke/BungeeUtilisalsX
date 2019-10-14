/*
 * Copyright (C) 2018 DBSoftwares - Dieter Blancke
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.dbsoftwares.bungeeutilisals.api.command;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.TimeUnit;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Data;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.List;
import java.util.Optional;

@Data
public class Command {

    private final String name;
    private final String[] aliases;
    private final String permission;
    private final int cooldown;
    private final CommandCall command;
    private final TabCall tab;

    private CommandHolder commandHolder;

    public Command(final String name, final String permission, final String[] aliases, final int cooldown, final CommandCall command, final TabCall tab) {
        this.name = name;
        this.aliases = aliases;
        this.permission = permission;
        this.cooldown = cooldown;
        this.command = command;
        this.tab = tab;
    }

    public void execute(User user, String[] args) {
        if (permission != null
                && !permission.isEmpty()
                && !user.hasPermission(permission)
                && !user.hasPermission("bungeeutilisals.commands.*")
                && !user.hasPermission("bungeeutilisals.*")
                && !user.hasPermission("*")) {
            user.sendLangMessage("no-permission", "%permission%", permission);
            return;
        }

        BUCore.getApi().getSimpleExecutor().asyncExecute(() -> {
            try {
                if (cooldown > 0 && !user.getCooldowns().canUse("COMMAND_COOLDOWNS_" + name)) {
                    user.sendLangMessage(
                            "general-commands.cooldown",
                            "{time}",
                            user.getCooldowns().getLeftTime("COMMAND_COOLDOWNS_" + name) / 1000
                    );
                    return;
                }

                command.onExecute(user, args);

                if (cooldown > 0) {
                    user.getCooldowns().updateTime("COMMAND_COOLDOWNS_" + name, TimeUnit.SECONDS, cooldown);
                }
            } catch (Exception e) {
                BUCore.getLogger().error("An error occured: ", e);
            }
        });
    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (!(sender instanceof ProxiedPlayer)) {
            return ImmutableList.of();
        }

        final Optional<User> optional = BUCore.getApi().getUser(sender.getName());
        if (optional.isPresent()) {
            final User user = optional.get();
            final List<String> tabCompletion = tab.onTabComplete(user, args);

            if (tabCompletion == null) {
                if (args.length == 0) {
                    return BUCore.getApi().getPlayerUtils().getPlayers();
                } else {
                    final String lastWord = args[args.length - 1];
                    final List<String> list = Lists.newArrayList();

                    for (String p : BUCore.getApi().getPlayerUtils().getPlayers()) {
                        if (p.toLowerCase().startsWith(lastWord.toLowerCase())) {
                            list.add(p);
                        }
                    }

                    return list;
                }
            }
            return tabCompletion;
        } else {
            return ImmutableList.of();
        }
    }

    public void unload() {
        ProxyServer.getInstance().getPluginManager().unregisterCommand(commandHolder);
        commandHolder = null;
    }

    public Command register() {
        if (commandHolder != null) {
            throw new RuntimeException("This command is already registered");
        }
        commandHolder = new CommandHolder(name, aliases);

        ProxyServer.getInstance().getPluginManager().registerCommand(BUCore.getApi().getPlugin(), commandHolder);
        return this;
    }

    boolean check(final String[] args) {
        if (args.length == 0) {
            return false;
        }
        if (name.equalsIgnoreCase(args[0])) {
            return true;
        }
        for (String alias : aliases) {
            if (alias.equalsIgnoreCase(args[0])) {
                return true;
            }
        }
        return false;
    }

    private class CommandHolder extends net.md_5.bungee.api.plugin.Command implements TabExecutor {

        public CommandHolder(final String name, final String[] aliases) {
            super(name, "", aliases);
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (sender instanceof ProxiedPlayer) {
                BUCore.getApi().getUser(sender.getName()).ifPresent(user -> Command.this.execute(user, args));
            } else {
                Command.this.execute(BUCore.getApi().getConsole(), args);
            }
        }

        @Override
        public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
            return Command.this.onTabComplete(sender, args);
        }
    }
}