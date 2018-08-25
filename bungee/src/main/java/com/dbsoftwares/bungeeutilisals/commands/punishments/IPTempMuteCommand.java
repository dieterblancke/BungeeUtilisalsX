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

public class IPTempMuteCommand extends Command {

    public IPTempMuteCommand() {
        super("iptempmute", Arrays.asList(FileLocation.PUNISHMENTS.getConfiguration()
                        .getString("commands.iptempmute.aliases").split(", ")),
                FileLocation.PUNISHMENTS.getConfiguration().getString("commands.iptempmute.permission"));
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return null;
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length < 3) {
            user.sendLangMessage("punishments.iptempmute.usage");
            return;
        }
        Dao dao = BUCore.getApi().getStorageManager().getDao();
        String timeFormat = args[1];
        String reason = Utils.formatList(Arrays.copyOfRange(args, 2, args.length), " ");
        Long time = Utils.parseDateDiff(timeFormat);

        if (time == 0L) {
            user.sendLangMessage("punishments.iptempmute.non-valid");
            return;
        }
        if (!dao.getUserDao().exists(args[0])) {
            user.sendLangMessage("never-joined");
            return;
        }
        UserStorage storage = dao.getUserDao().getUserData(args[0]);
        if (dao.getPunishmentDao().isPunishmentPresent(PunishmentType.IPTEMPMUTE, null, storage.getIp(), true)) {
            user.sendLangMessage("punishments.iptempmute.already-muted");
            return;
        }

        UserPunishEvent event = new UserPunishEvent(PunishmentType.IPTEMPMUTE, user, storage.getUuid(),
                storage.getUserName(), storage.getIp(), reason, user.getServerName(), time);
        api.getEventLoader().launchEvent(event);

        if (event.isCancelled()) {
            user.sendLangMessage("punishments.cancelled");
            return;
        }
        IPunishmentExecutor executor = api.getPunishmentExecutor();

        PunishmentInfo info = dao.getPunishmentDao().insertPunishment(
                PunishmentType.IPTEMPMUTE, storage.getUuid(), storage.getUserName(), storage.getIp(),
                reason, time, user.getServerName(), true, user.getName()
        );

        api.getUser(storage.getUserName()).ifPresent(muted -> muted.sendLangMessage("punishments.iptempmute.onmute",
                executor.getPlaceHolders(info).toArray(new Object[]{})));

        user.sendLangMessage("punishments.iptempmute.executed", executor.getPlaceHolders(info));

        api.langBroadcast("punishments.iptempmute.broadcast",
                FileLocation.PUNISHMENTS.getConfiguration().getString("commands.iptempmute.broadcast"),
                executor.getPlaceHolders(info).toArray(new Object[]{}));
    }
}