package com.dbsoftwares.bungeeutilisals.api.command;

import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.configuration.IConfiguration;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.List;
import java.util.Optional;

public abstract class Command extends net.md_5.bungee.api.plugin.Command implements ICommand, TabExecutor {

    public BUAPI api;
    String permission = null;

    public Command(String name) {
        this(name, Lists.newArrayList(), null);
    }

    public Command(String name, String... aliases) {
        this(name, Lists.newArrayList(aliases), null);
    }

    public Command(String name, List<String> aliases) {
        this(name, aliases, null);
    }

    public Command(String name, List<String> aliases, String permission) {
        super(name, "", aliases.toArray(new String[aliases.size()]));
        this.permission = permission;

        Optional<BUAPI> optional = BUCore.getApiSafe();
        if (optional.isPresent()) {
            api = optional.get();

            api.getEventLoader().launchEventAsync(new CommandCreateEvent(this));
        }
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        BUAPI api = BUCore.getApi();
        IConfiguration configuration = api.getLanguageManager().getLanguageConfiguration(api.getPlugin(), sender);

        if (permission != null) {
            if (!sender.hasPermission(permission) && !sender.hasPermission("bungeeutilisals.commands.*")) {
                BUCore.sendMessage(sender, configuration.getString("no-permission").replace("%permission%", permission));
                return;
            }
        }

        if (sender instanceof ProxiedPlayer) {
            Optional<User> optional = api.getUser(sender.getName());

            if (optional.isPresent()) {
                User user = optional.get();

                try {
                    BUCore.getApi().getSimpleExecutor().asyncExecute(() -> onExecute(user, args));
                    // onExecute(user, args); | testing plugin with Async Commands.
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return;
            }
        }
        try {
            BUCore.getApi().getSimpleExecutor().asyncExecute(() -> onExecute(BUCore.getApi().getConsole(), args));
            //onExecute(sender, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        Optional<User> optional = api.getUser(sender.getName());
        if (sender instanceof ProxiedPlayer && optional.isPresent()) {
            List<String> tabCompletion = onTabComplete(optional.get(), args);

            if (tabCompletion == null) {
                if (args.length == 0) {
                    List<String> list = Lists.newArrayList();
                    for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                        list.add(p.getName());
                    }
                    return list;
                } else {
                    String lastWord = args[args.length - 1];
                    List<String> list = Lists.newArrayList();

                    for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
                        if (p.getName().toLowerCase().startsWith(lastWord.toLowerCase())) {
                            list.add(p.getName());
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

    public abstract List<String> onTabComplete(User user, String[] args);
}