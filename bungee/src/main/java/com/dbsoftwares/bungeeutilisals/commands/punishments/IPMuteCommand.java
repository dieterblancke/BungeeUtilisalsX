package com.dbsoftwares.bungeeutilisals.commands.punishments;

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.Utils;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;

import java.util.Arrays;
import java.util.List;

public class IPMuteCommand extends Command {

    public IPMuteCommand() {
        super("ipmute", Arrays.asList(FileLocation.PUNISHMENTS.getConfiguration()
                        .getString("commands.ipmute.aliases").split(", ")),
                FileLocation.PUNISHMENTS.getConfiguration().getString("commands.ipmute.permission"));
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
        Dao dao = BUCore.getApi().getStorageManager().getDao();
        String reason = Utils.formatList(Arrays.copyOfRange(args, 1, args.length), " ");

        if (!dao.getUserDao().exists(args[0])) {
            user.sendLangMessage("never-joined");
            return;
        }
        UserStorage storage = dao.getUserDao().getUserData(args[0]);
        if (dao.getPunishmentDao().isPunishmentPresent(PunishmentType.IPMUTE, null, storage.getIp(), true)) {
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

        PunishmentInfo info = dao.getPunishmentDao().insertPunishment(
                PunishmentType.IPMUTE, storage.getUuid(), storage.getUserName(), storage.getIp(),
                reason, 0L, user.getServerName(), true, user.getName()
        );

        api.getUser(storage.getUserName()).ifPresent(muted -> muted.sendLangMessage("punishments.ipmute.onmute",
                executor.getPlaceHolders(info).toArray(new Object[]{})));

        user.sendLangMessage("punishments.ipmute.executed", executor.getPlaceHolders(info));

        api.langBroadcast("punishments.ipmute.broadcast",
                FileLocation.PUNISHMENTS.getConfiguration().getString("commands.ipmute.broadcast"),
                executor.getPlaceHolders(info).toArray(new Object[]{}));
    }
}