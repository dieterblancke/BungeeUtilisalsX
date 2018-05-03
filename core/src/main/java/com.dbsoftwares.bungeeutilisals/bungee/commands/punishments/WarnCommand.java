package com.dbsoftwares.bungeeutilisals.bungee.commands.punishments;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class WarnCommand extends Command {

    public WarnCommand() {
        super("warn", Arrays.asList(BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG)
                        .getString("commands.warn.aliases").split(", ")),
                BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG).getString("commands.warn.permission"));
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return null;
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length < 2) {
            user.sendLangMessage("punishments.warn.usage");
            return;
        }
        String reason = Utils.formatList(Arrays.copyOfRange(args, 1, args.length), " ");

        Optional<User> optionalUser = BUCore.getApi().getUser(args[0]);
        if (!optionalUser.isPresent()) {
            user.sendLangMessage("offline");
            return;
        }
        User target = optionalUser.get();
        UserStorage storage = target.getStorage();

        UserPunishEvent event = new UserPunishEvent(PunishmentType.WARN, user, storage.getUuid(),
                storage.getUserName(), storage.getIp(), reason, user.getServerName(), null);
        api.getEventLoader().launchEvent(event);

        if (event.isCancelled()) {
            user.sendLangMessage("punishments.cancelled");
            return;
        }
        IPunishmentExecutor executor = api.getPunishmentExecutor();
        PunishmentInfo info = executor.addWarn(storage.getUuid(), storage.getUserName(), storage.getIp(), reason, user.getServerName(), user.getName());

        target.sendLangMessage("punishments.warn.onwarn", executor.getPlaceHolders(info).toArray(new Object[]{}));

        user.sendLangMessage("punishments.warn.executed", executor.getPlaceHolders(info));

        api.langBroadcast("punishments.warn.broadcast",
                BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG).getString("commands.warn.broadcast"),
                executor.getPlaceHolders(info).toArray(new Object[]{}));
    }
}