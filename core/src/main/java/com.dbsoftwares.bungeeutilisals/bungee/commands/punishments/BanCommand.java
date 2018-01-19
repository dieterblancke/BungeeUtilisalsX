package com.dbsoftwares.bungeeutilisals.bungee.commands.punishments;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserBanEvent;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.storage.SQLStatements;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BanCommand extends Command {

    public BanCommand() {
        super("ban", Arrays.asList(BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG)
                        .getString("commands.ban.aliases").split(", ")),
                BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG).getString("commands.ban.permission"));
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return null;
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length < 2) {
            user.sendLangMessage("punishments.ban.usage");
            return;
        }
        String reason = Utils.formatList(Arrays.copyOfRange(args, 1, args.length), " ");

        if (!SQLStatements.isUserPresent(args[0])) {
            user.sendLangMessage("never-joined");
            return;
        }

        UserStorage storage = SQLStatements.getUser(args[0]);

        UserBanEvent event = new UserBanEvent(user, storage.getUuid(), storage.getUserName(), storage.getIp(), reason, user.getServerName());
        BUCore.getApi().getEventLoader().launchEvent(event);

        if (event.isCancelled()) {
            user.sendLangMessage("punishments.cancelled");
            return;
        }

        Optional<User> optionalUser = BUCore.getApi().getUser(storage.getUserName());
        if (optionalUser.isPresent()) {
            User banned = optionalUser.get();

            banned.kick(Utils.formatList(banned.getLanguageConfig().getStringList("punishments.ban.kick"), "\n")
                    .replace("%reason%", reason).replace("%bandate%", Utils.getCurrentDate()
                            + " " + Utils.getCurrentTime()).replace("%bannedby%", user.getName()));
        }

        BUCore.getApi().getPunishmentExecutor().addBan(storage.getUuid(), storage.getUserName(), storage.getIp(),
                reason, user.getServerName(), user.getName());

        BUCore.getApi().langBroadcast("punishments.ban.broadcast",
                BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG).getString("commands.ban.broadcast"),
                "%banner%", user.getName(), "%banned%", storage.getUserName(), "%reason%", reason);
    }
}