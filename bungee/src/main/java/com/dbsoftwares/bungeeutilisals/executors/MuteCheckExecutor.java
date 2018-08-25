package com.dbsoftwares.bungeeutilisals.executors;

/*
 * Created by DBSoftwares on 15/02/2018
 * Developer: Dieter Blancke
 * Project: BungeeUtilisals
 */

import com.dbsoftwares.bungeeutilisals.api.BUCore;
import com.dbsoftwares.bungeeutilisals.api.event.event.Event;
import com.dbsoftwares.bungeeutilisals.api.event.event.EventExecutor;
import com.dbsoftwares.bungeeutilisals.api.event.event.Priority;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserChatEvent;
import com.dbsoftwares.bungeeutilisals.api.event.events.user.UserCommandEvent;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentInfo;
import com.dbsoftwares.bungeeutilisals.api.punishments.PunishmentType;
import com.dbsoftwares.bungeeutilisals.api.user.interfaces.User;
import com.dbsoftwares.bungeeutilisals.api.utils.file.FileLocation;

public class MuteCheckExecutor implements EventExecutor {

    @Event
    public void onCommand(UserCommandEvent event) {
        User user = event.getUser();

        if (!user.isMuted()) {
            return;
        }
        PunishmentInfo info = user.getMuteInfo();
        if (checkTemporaryMute(user, info)) {
            return;
        }

        if (FileLocation.PUNISHMENTS.getConfiguration().getStringList("blocked-mute-commands")
                .contains(event.getActualCommand().replaceFirst("/", ""))) {

            user.sendLangMessage("punishments." + info.getType().toString().toLowerCase() + ".onmute",
                    event.getApi().getPunishmentExecutor().getPlaceHolders(info).toArray(new Object[]{}));
            event.setCancelled(true);
        }
    }

    // high priority
    @Event(priority = Priority.HIGHEST)
    public void onChat(UserChatEvent event) {
        User user = event.getUser();

        if (!user.isMuted()) {
            return;
        }
        PunishmentInfo info = user.getMuteInfo();
        if (checkTemporaryMute(user, info)) {
            return;
        }

        user.sendLangMessage("punishments." + info.getType().toString().toLowerCase() + ".onmute",
                event.getApi().getPunishmentExecutor().getPlaceHolders(info).toArray(new Object[]{}));
        event.setCancelled(true);
    }

    private boolean checkTemporaryMute(User user, PunishmentInfo info) {
        if (info.isTemporary()) {
            if (info.getExpireTime() <= System.currentTimeMillis()) {
                if (info.getType().equals(PunishmentType.TEMPMUTE)) {
                    BUCore.getApi().getStorageManager().getDao().getPunishmentDao().removePunishment(
                            PunishmentType.TEMPMUTE, user.getParent().getUniqueId(), null
                    );
                } else {
                    BUCore.getApi().getStorageManager().getDao().getPunishmentDao().removePunishment(
                            PunishmentType.IPTEMPMUTE, null, user.getIP()
                    );
                }
                return true;
            }
        }
        return false;
    }
}