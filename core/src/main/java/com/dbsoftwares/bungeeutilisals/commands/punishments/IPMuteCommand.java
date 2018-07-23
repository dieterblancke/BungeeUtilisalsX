package com.dbsoftwares.bungeeutilisals.commands.punishments;

import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;

import java.util.Arrays;
import java.util.List;

public class IPMuteCommand extends Command {

    public IPMuteCommand() {
        super("ipmute", Arrays.asList(FileLocation.PUNISHMENTS_CONFIG.getConfiguration()
                        .getString("commands.ipmute.aliases").split(", ")),
                FileLocation.PUNISHMENTS_CONFIG.getConfiguration().getString("commands.ipmute.permission"));
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return null;
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length < 2) {
            user.sendLangMessage("punishments.ipmute.usage");
            return;
        }
        String reason = Utils.formatList(Arrays.copyOfRange(args, 1, args.length), " ");

        if (!BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().isUserPresent(args[0])) {
            user.sendLangMessage("never-joined");
            return;
        }
        UserStorage storage = BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().getUser(args[0]);
        if (BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager().isIPMutePresent(storage.getIp(), true)) {
            user.sendLangMessage("punishments.ipmute.already-muted");
            return;
        }

        UserPunishEvent event = new UserPunishEvent(PunishmentType.IPMUTE, user, storage.getUuid(),
                storage.getUserName(), storage.getIp(), reason, user.getServerName(), null);
        api.getEventLoader().launchEvent(event);

        if (event.isCancelled()) {
            user.sendLangMessage("punishments.cancelled");
            return;
        }
        IPunishmentExecutor executor = api.getPunishmentExecutor();
        PunishmentInfo info = executor.addIPMute(storage.getUuid(), storage.getUserName(), storage.getIp(),
                reason, user.getServerName(), user.getName());

        api.getUser(storage.getUserName()).ifPresent(muted -> muted.sendLangMessage("punishments.ipmute.onmute",
                executor.getPlaceHolders(info).toArray(new Object[]{})));

        user.sendLangMessage("punishments.ipmute.executed", executor.getPlaceHolders(info));

        api.langBroadcast("punishments.ipmute.broadcast",
                FileLocation.PUNISHMENTS_CONFIG.getConfiguration().getString("commands.ipmute.broadcast"),
                executor.getPlaceHolders(info).toArray(new Object[]{}));
    }
}