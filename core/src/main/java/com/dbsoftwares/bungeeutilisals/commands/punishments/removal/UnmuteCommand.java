package com.dbsoftwares.bungeeutilisals.commands.punishments.removal;

import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishRemoveEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.storage.dao.Dao;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.BungeeUtilisals;

import java.util.Arrays;
import java.util.List;

public class UnmuteCommand extends Command {

    public UnmuteCommand() {
        super("unmute", Arrays.asList(FileLocation.PUNISHMENTS_CONFIG.getConfiguration()
                        .getString("commands.unmute.aliases").split(", ")),
                FileLocation.PUNISHMENTS_CONFIG.getConfiguration().getString("commands.unmute.permission"));
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return null;
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length < 1) {
            user.sendLangMessage("punishments.unmute.usage");
            return;
        }
        Dao dao = BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager();

        if (!dao.getUserDao().exists(args[0])) {
            user.sendLangMessage("never-joined");
            return;
        }
        UserStorage storage = dao.getUserDao().getUserData(args[0]);
        if (!dao.getPunishmentDao().isPunishmentPresent(PunishmentType.BAN, storage.getUuid(), null, true)) {
            user.sendLangMessage("punishments.unmute.not-banned");
            return;
        }

        UserPunishRemoveEvent event = new UserPunishRemoveEvent(UserPunishRemoveEvent.PunishmentRemovalAction.UNMUTE, user, storage.getUuid(),
                storage.getUserName(), storage.getIp(), user.getServerName());
        api.getEventLoader().launchEvent(event);

        if (event.isCancelled()) {
            user.sendLangMessage("punishments.cancelled");
            return;
        }
        IPunishmentExecutor executor = api.getPunishmentExecutor();
        executor.removeBan(storage.getUuid());

        PunishmentInfo info = new PunishmentInfo();
        info.setUser(args[0]);
        info.setId(-1);
        info.setExecutedBy(user.getName());
        info.setRemovedBy(user.getName());

        user.sendLangMessage("punishments.unmute.executed", executor.getPlaceHolders(info));

        api.langBroadcast("punishments.unmute.broadcast",
                FileLocation.PUNISHMENTS_CONFIG.getConfiguration().getString("commands.unmute.broadcast"),
                executor.getPlaceHolders(info).toArray(new Object[]{}));
    }
}