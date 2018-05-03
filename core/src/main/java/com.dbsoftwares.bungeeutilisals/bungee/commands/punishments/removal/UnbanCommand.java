package com.dbsoftwares.bungeeutilisals.bungee.commands.punishments.removal;

import com.dbsoftwares.bungeeutilisals.api.command.Command;
import com.dbsoftwares.bungeeutilisals.api.event.events.punishment.UserPunishRemoveEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.IPunishmentExecutor;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.storage.DataManager;
import com.dbsoftwares.bungeeutilisals.api.user.UserStorage;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;
import com.dbsoftwares.bungeeutilisals.bungee.BungeeUtilisals;

import java.util.Arrays;
import java.util.List;

public class UnbanCommand extends Command {

    public UnbanCommand() {
        super("unban", Arrays.asList(BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG)
                        .getString("commands.unban.aliases").split(", ")),
                BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG).getString("commands.unban.permission"));
    }

    @Override
    public List<String> onTabComplete(User user, String[] args) {
        return null;
    }

    @Override
    public void onExecute(User user, String[] args) {
        if (args.length < 1) {
            user.sendLangMessage("punishments.unban.usage");
            return;
        }
        DataManager dataManager = BungeeUtilisals.getInstance().getDatabaseManagement().getDataManager();

        if (!dataManager.isUserPresent(args[0])) {
            user.sendLangMessage("never-joined");
            return;
        }
        UserStorage storage = dataManager.getUser(args[0]);
        if (!dataManager.isBanPresent(storage.getUuid(), true)) {
            user.sendLangMessage("punishments.unban.not-banned");
            return;
        }

        UserPunishRemoveEvent event = new UserPunishRemoveEvent(UserPunishRemoveEvent.PunishmentRemovalAction.UNBAN, user, storage.getUuid(),
                storage.getUserName(), storage.getIp(), user.getServerName());
        api.getEventLoader().launchEvent(event);

        if (event.isCancelled()) {
            user.sendLangMessage("punishments.cancelled");
            return;
        }
        IPunishmentExecutor executor = api.getPunishmentExecutor();
        executor.removeBan(storage.getUuid());

        PunishmentInfo info = PunishmentInfo.builder().user(args[0])
                .id(-1).executedBy(user.getName()).removedBy(user.getName()).build();

        user.sendLangMessage("punishments.unban.executed", executor.getPlaceHolders(info));

        api.langBroadcast("punishments.unban.broadcast",
                BungeeUtilisals.getConfiguration(FileLocation.PUNISHMENTS_CONFIG).getString("commands.unban.broadcast"),
                executor.getPlaceHolders(info).toArray(new Object[]{}));
    }
}