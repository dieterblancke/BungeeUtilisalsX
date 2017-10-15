package com.dbsoftwares.bungeeutilisals.api.command;
import com.dbsoftwares.bungeeutilisals.api.BUAPI;
import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.user.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.config.Configuration;
import java.util.List;
import java.util.Optional;

public abstract class BUCommand extends Command implements TabExecutor {

    public BUAPI api;
    String permission = null;

    public BUCommand(String name) {
        this(name, Lists.newArrayList(), null);
    }

    public BUCommand(String name, String... aliases) {
        this(name, Lists.newArrayList(aliases), null);
    }

    public BUCommand(String name, List<String> aliases) {
        this(name, aliases, null);
    }

    public BUCommand(String name, List<String> aliases, String permission) {
        super(name, permission, aliases.toArray(new String[aliases.size()]));
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
        Configuration configuration = api.getLanguageManager().getLanguageConfiguration(api.getPlugin(), sender);

        if (permission != null && !sender.hasPermission(permission)) {
            sender.sendMessage(Utils.format(api.getPrefix(), configuration.getString("no-permission").replace("%permission%", permission)));
            return;
        }

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

    public abstract void onExecute(User user, String[] args);

    public abstract void onExecute(CommandSender sender, String[] args);
}