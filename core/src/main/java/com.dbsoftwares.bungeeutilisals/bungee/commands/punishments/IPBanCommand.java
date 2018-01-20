package com.dbsoftwares.bungeeutilisals.bungee.commands.punishments;

import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;
import com.dbsoftwares.bungeeutilisals.bungee.storage.SQLStatements;

import java.util.Arrays;
import java.util.List;

public class IPBanCommand extends Command {

    public IPBanCommand() {
        super("ipban", Arrays.asList(BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG)
                        .getString("commands.ipban.aliases").split(", ")),
                BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG).getString("commands.ipban.permission"));
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return null;
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length < 2) {
            user.sendLangMessage("punishments.ipban.usage");
            return;
        }
        String reason = Utils.formatList(Arrays.copyOfRange(args, 1, args.length), " ");

        if (!SQLStatements.isUserPresent(args[0])) {
            user.sendLangMessage("never-joined");
            return;
        }
        UserStorage storage = SQLStatements.getUser(args[0]);
        if (SQLStatements.isIPBanPresent(storage.getIp(), true)) {
            user.sendLangMessage("punishments.ipban.already-banned");
            return;
        }

        UserPunishEvent event = new UserPunishEvent(PunishmentType.IPBAN, user, storage.getUuid(),
                storage.getUserName(), storage.getIp(), reason, user.getServerName(), null);
        api.getEventLoader().launchEvent(event);

        if (event.isCancelled()) {
            user.sendLangMessage("punishments.cancelled");
            return;
        }
        IPunishmentExecutor executor = api.getPunishmentExecutor();
        PunishmentInfo info = executor.addIPBan(storage.getUuid(), storage.getUserName(), storage.getIp(),
                reason, user.getServerName(), user.getName());

        api.getUser(storage.getUserName()).ifPresent(banned -> {
            String kick = Utils.formatList(banned.getLanguageConfig().getStringList("punishments.ipban.kick"), "\n");
            kick = executor.setPlaceHolders(kick, info);

            banned.kick(kick);
        });

        api.langBroadcast("punishments.ipban.broadcast",
                BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG).getString("commands.ipban.broadcast"),
                executor.getPlaceHolders(info).toArray(new Object[]{}));
    }
}